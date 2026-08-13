package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.*;
import com.example.demo.teaching.mapper.*;
import com.example.demo.teaching.service.GraduationService;
import com.example.demo.vo.CreditSummaryVO;
import com.example.demo.vo.GraduationResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GraduationServiceImpl implements GraduationService {

    private static final Logger log = LoggerFactory.getLogger(GraduationServiceImpl.class);

    @Autowired private StudentMapper studentMapper;
    @Autowired private ClazzMapper clazzMapper;
    @Autowired private MajorMapper majorMapper;
    @Autowired private CollegeMapper collegeMapper;
    @Autowired private TrainingPlanMapper trainingPlanMapper;
    @Autowired private PlanCourseMapper planCourseMapper;
    @Autowired private EnrollmentMapper enrollmentMapper;
    @Autowired private CourseMapper courseMapper;
    @Autowired private GraduationResultMapper graduationResultMapper;

    private static final BigDecimal PASS_SCORE = new BigDecimal("60");

    @Override
    @Transactional
    public GraduationResultVO checkGraduation(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) throw new RuntimeException("学生不存在: " + studentId);

        Clazz clazz = clazzMapper.selectById(student.getClassId());
        if (clazz == null || clazz.getMajorId() == null)
            throw new RuntimeException("学生班级或专业信息缺失，无法审核");

        Major major = majorMapper.selectById(clazz.getMajorId());
        if (major == null) throw new RuntimeException("专业不存在: " + clazz.getMajorId());

        College college = collegeMapper.selectById(major.getCollegeId());

        String grade = student.getEnrollmentYear();
        TrainingPlan plan = trainingPlanMapper.selectByMajorAndGrade(major.getId(), grade);
        if (plan == null)
            throw new RuntimeException(String.format("未找到培养方案: 专业=%s, 年级=%s", major.getMajorName(), grade));

        List<PlanCourse> planCourses = planCourseMapper.selectByPlanId(plan.getId());

        LambdaQueryWrapper<Enrollment> enrollWrapper = new LambdaQueryWrapper<>();
        enrollWrapper.eq(Enrollment::getStudentId, studentId)
                     .eq(Enrollment::getRelStatus, 2);
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollWrapper);

        Set<Long> passedCourseIds = enrollments.stream()
                .filter(e -> e.getScore() != null && e.getScore().compareTo(PASS_SCORE) >= 0)
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        // 从培养方案全部课程构建 courseMap（含未选课程）用于计算学分
        Map<Long, Course> planCourseMap = buildPlanCourseMap(planCourses);

        BigDecimal requiredEarned = BigDecimal.ZERO;
        BigDecimal majorElectiveEarned = BigDecimal.ZERO;
        BigDecimal generalElectiveEarned = BigDecimal.ZERO;

        // 培养方案中各类别最大学分（全部课程学分总和）
        BigDecimal requiredMax = BigDecimal.ZERO;
        BigDecimal majorElectiveMax = BigDecimal.ZERO;
        BigDecimal generalElectiveMax = BigDecimal.ZERO;

        List<String> missingItems = new ArrayList<>();

        for (PlanCourse pc : planCourses) {
            Course course = planCourseMap.get(pc.getCourseId());
            BigDecimal credit = (course != null) ? course.getCredit() : BigDecimal.ZERO;
            boolean passed = passedCourseIds.contains(pc.getCourseId());

            switch (pc.getCourseCategory()) {
                case "REQUIRED":
                    requiredMax = requiredMax.add(credit);
                    if (passed) {
                        requiredEarned = requiredEarned.add(credit);
                    } else if (pc.getIsRequired() != null && pc.getIsRequired() == 1) {
                        missingItems.add(String.format("必修课未通过: %s (课程ID=%d)",
                                course != null ? course.getCourseName() : "未知", pc.getCourseId()));
                    }
                    break;
                case "MAJOR_ELECTIVE":
                    majorElectiveMax = majorElectiveMax.add(credit);
                    if (passed) majorElectiveEarned = majorElectiveEarned.add(credit);
                    break;
                case "GENERAL_ELECTIVE":
                    generalElectiveMax = generalElectiveMax.add(credit);
                    if (passed) generalElectiveEarned = generalElectiveEarned.add(credit);
                    break;
            }
        }

        List<CreditSummaryVO> creditDetails = new ArrayList<>();

        CreditSummaryVO requiredDetail = buildCreditDetail("REQUIRED", "必修课",
                plan.getTotalRequiredCredits(), requiredEarned, requiredMax);
        creditDetails.add(requiredDetail);

        CreditSummaryVO majorElectiveDetail = buildCreditDetail("MAJOR_ELECTIVE", "专业选修课",
                plan.getMajorElectiveMinCredits(), majorElectiveEarned, majorElectiveMax);
        creditDetails.add(majorElectiveDetail);

        CreditSummaryVO generalElectiveDetail = buildCreditDetail("GENERAL_ELECTIVE", "通识选修课",
                plan.getGeneralElectiveMinCredits(), generalElectiveEarned, generalElectiveMax);
        creditDetails.add(generalElectiveDetail);

        boolean allSatisfied = creditDetails.stream().allMatch(CreditSummaryVO::isSatisfied);
        BigDecimal totalEarned = requiredEarned.add(majorElectiveEarned).add(generalElectiveEarned);
        BigDecimal totalMax = requiredMax.add(majorElectiveMax).add(generalElectiveMax);

        GraduationResult result = new GraduationResult();
        result.setStudentId(studentId);
        result.setPlanId(plan.getId());
        result.setTotalEarnedCredits(totalEarned);
        result.setRequiredEarnedCredits(requiredEarned);
        result.setMajorElectiveEarnedCredits(majorElectiveEarned);
        result.setGeneralElectiveEarnedCredits(generalElectiveEarned);
        result.setPassed(allSatisfied ? 1 : 0);
        result.setMissingItems(missingItems.isEmpty() ? null :
                "[\"" + String.join("\",\"", missingItems) + "\"]");
        result.setReviewTime(LocalDateTime.now());
        result.setGmtCreate(LocalDateTime.now());
        graduationResultMapper.insert(result);

        log.info("毕业审核完成: studentId={}, passed={}, totalEarned={}, missing={}",
                studentId, allSatisfied, totalEarned, missingItems.size());

        GraduationResultVO vo = new GraduationResultVO();
        vo.setStudentId(studentId);
        vo.setStudentNo(student.getStudentNo());
        vo.setStudentName(student.getStudentName());
        vo.setGrade(grade);
        vo.setMajorName(major.getMajorName());
        vo.setCollegeName(college != null ? college.getCollegeName() : "");
        vo.setPlanName(plan.getPlanName());
        vo.setPassed(allSatisfied);
        vo.setCreditDetails(creditDetails);
        vo.setMissingItems(missingItems);
        vo.setTotalEarnedCredits(totalEarned);
        vo.setTotalMaxCredits(totalMax);
        vo.setReviewTime(LocalDateTime.now());
        return vo;
    }

    @Override
    public List<GraduationResult> getHistory(Long studentId) {
        return graduationResultMapper.selectByStudentId(studentId);
    }

    private CreditSummaryVO buildCreditDetail(String category, String name,
                                               BigDecimal required, BigDecimal earned, BigDecimal max) {
        CreditSummaryVO vo = new CreditSummaryVO();
        vo.setCategory(category);
        vo.setCategoryName(name);
        vo.setRequiredCredits(required);
        vo.setEarnedCredits(earned);
        vo.setMaxCredits(max);
        BigDecimal gap = earned.subtract(required);
        vo.setGap(gap);
        vo.setSatisfied(gap.compareTo(BigDecimal.ZERO) >= 0);
        return vo;
    }

    /** 从培养方案的课程ID列表查询 Course 表，构建 ID→Course 映射（用于学分计算） */
    private Map<Long, Course> buildPlanCourseMap(List<PlanCourse> planCourses) {
        List<Long> courseIds = planCourses.stream()
                .map(PlanCourse::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        if (courseIds.isEmpty()) return Collections.emptyMap();

        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        return courses.stream().collect(Collectors.toMap(Course::getId, c -> c, (a, b) -> a));
    }
}
