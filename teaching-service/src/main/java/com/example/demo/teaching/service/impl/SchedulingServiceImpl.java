package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.*;
import com.example.demo.teaching.mapper.*;
import com.example.demo.teaching.service.SchedulingService;
import com.example.demo.vo.ScheduleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 排课算法服务 — 贪心 + 约束满足
 */
@Service
public class SchedulingServiceImpl implements SchedulingService {

    private static final Logger log = LoggerFactory.getLogger(SchedulingServiceImpl.class);

    @Autowired private ScheduleMapper scheduleMapper;
    @Autowired private CourseMapper courseMapper;
    @Autowired private TeacherMapper teacherMapper;
    @Autowired private ClassroomMapper classroomMapper;
    @Autowired private TimeSlotMapper timeSlotMapper;
    @Autowired private ClazzMapper clazzMapper;
    @Autowired private TrainingPlanMapper trainingPlanMapper;
    @Autowired private PlanCourseMapper planCourseMapper;

    @Override
    @Transactional
    public Map<String, Object> generate(String semester, Long majorId) {
        // Phase 1: 数据收集
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getCourseStatus, 1)
                     .eq(Course::getCourseDeleted, 0);
        List<Course> courses = courseMapper.selectList(courseWrapper);

        // 如果指定专业，只排该专业培养方案内的课程
        if (majorId != null) {
            TrainingPlan plan = trainingPlanMapper.selectByMajorAndGrade(majorId, null);
            if (plan != null) {
                List<PlanCourse> planCourses = planCourseMapper.selectByPlanId(plan.getId());
                Set<Long> planCourseIds = planCourses != null ?
                        planCourses.stream().map(PlanCourse::getCourseId).collect(Collectors.toSet()) :
                        Collections.emptySet();
                courses = courses.stream().filter(c -> planCourseIds.contains(c.getId())).collect(Collectors.toList());
            }
        }

        // 加载可用教室、时间段、班级
        List<Classroom> classrooms = classroomMapper.selectList(
                new LambdaQueryWrapper<Classroom>()
                        .eq(Classroom::getClassroomStatus, 1)
                        .eq(Classroom::getClassroomDeleted, 0)
                        .orderByDesc(Classroom::getCapacity));
        List<TimeSlot> timeSlots = timeSlotMapper.selectList(
                new LambdaQueryWrapper<TimeSlot>().eq(TimeSlot::getSlotStatus, 1)
                        .orderByAsc(TimeSlot::getDayOfWeek, TimeSlot::getStartPeriod));
        List<Clazz> classes = clazzMapper.selectList(
                new LambdaQueryWrapper<Clazz>()
                        .eq(Clazz::getClassStatus, 1)
                        .eq(Clazz::getClassDeleted, 0));

        if (courses.isEmpty() || classrooms.isEmpty() || timeSlots.isEmpty() || classes.isEmpty()) {
            return Map.of("success", false, "message",
                    "数据不足：课程=" + courses.size() + " 教室=" + classrooms.size() +
                    " 时段=" + timeSlots.size() + " 班级=" + classes.size());
        }

        // 加载已有排课（冲突检查用）
        List<Schedule> existing = scheduleMapper.selectBySemester(semester);
        Set<String> teacherSlotKeys = new HashSet<>();
        Set<String> classroomSlotKeys = new HashSet<>();
        Set<String> classSlotKeys = new HashSet<>();
        for (Schedule s : existing) {
            if (s.getScheduleDeleted() != null && s.getScheduleDeleted() == 0) {
                teacherSlotKeys.add(s.getTeacherId() + "_" + s.getTimeSlotId());
                classroomSlotKeys.add(s.getClassroomId() + "_" + s.getTimeSlotId());
                classSlotKeys.add(s.getClazzId() + "_" + s.getTimeSlotId());
            }
        }

        // Phase 2: 优先级排序（必修优先 → 学分高优先）
        courses.sort((a, b) -> {
            int catCmp = categoryWeight(b) - categoryWeight(a);
            if (catCmp != 0) return catCmp;
            return BigDecimalComparator.compare(b.getCredit(), a.getCredit());
        });

        // Phase 3: 贪心分配
        List<Schedule> generated = new ArrayList<>();
        List<String> failedCourses = new ArrayList<>();
        Map<String, Integer> classCourseCount = new HashMap<>(); // 班级每天已排课数

        for (Course course : courses) {
            Long teacherId = course.getTeacherId();
            if (teacherId == null) {
                failedCourses.add("课程「" + course.getCourseName() + "」缺少授课教师，跳过");
                continue;
            }

            boolean assigned = false;
            // 尝试每个班级
            for (Clazz clazz : classes) {
                if (assigned) break;
                // 尝试每个时间段（早上优先）
                for (TimeSlot slot : timeSlots) {
                    if (assigned) break;

                    String tsKey = teacherId + "_" + slot.getId();
                    String csKey = clazz.getId() + "_" + slot.getId();
                    String classDayKey = clazz.getId() + "_" + slot.getDayOfWeek();

                    // 硬约束检查
                    if (teacherSlotKeys.contains(tsKey)) continue;
                    if (classSlotKeys.contains(csKey)) continue;

                    // 软约束：班级同一天连续上课 ≤ 4 节（2个时段）
                    int todayCount = classCourseCount.getOrDefault(classDayKey, 0);
                    if (todayCount >= 2) continue;

                    // 教室容量 ≥ 班级人数（班级人数估算35人）
                    Classroom chosen = null;
                    for (Classroom cr : classrooms) {
                        String crKey = cr.getId() + "_" + slot.getId();
                        if (!classroomSlotKeys.contains(crKey) && cr.getCapacity() >= 35) {
                            chosen = cr;
                            break;
                        }
                    }
                    if (chosen == null) continue;

                    // 分配成功
                    Schedule sched = new Schedule();
                    sched.setCourseId(course.getId());
                    sched.setTeacherId(teacherId);
                    sched.setClassroomId(chosen.getId());
                    sched.setTimeSlotId(slot.getId());
                    sched.setClazzId(clazz.getId());
                    sched.setSemester(semester);
                    sched.setWeekStart(1);
                    sched.setWeekEnd(18);
                    sched.setScheduleStatus(1);
                    sched.setScheduleDeleted(0);
                    scheduleMapper.insert(sched);
                    generated.add(sched);

                    teacherSlotKeys.add(tsKey);
                    classSlotKeys.add(csKey);
                    classroomSlotKeys.add(chosen.getId() + "_" + slot.getId());
                    classCourseCount.put(classDayKey, todayCount + 1);
                    assigned = true;
                }
            }

            if (!assigned) {
                failedCourses.add("课程「" + course.getCourseName()
                        + "」(ID=" + course.getId() + ") 无法安排，可能无可用时段或教室");
            }
        }

        log.info("排课完成: semester={}, 成功={}, 失败={}", semester, generated.size(), failedCourses.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("semester", semester);
        result.put("totalCourses", courses.size());
        result.put("scheduledCount", generated.size());
        result.put("failedCount", failedCourses.size());
        result.put("failedCourses", failedCourses);
        result.put("message", generated.size() + " 门课程排课成功"
                + (failedCourses.isEmpty() ? "" : "，" + failedCourses.size() + " 门失败"));
        return result;
    }

    @Override
    @Transactional
    public int clear(String semester) {
        return scheduleMapper.deleteBySemester(semester);
    }

    @Override
    public Map<String, Object> getPage(String semester, Long clazzId, Long teacherId, int page, int size) {
        int offset = (page - 1) * size;
        List<ScheduleVO> records = scheduleMapper.selectPageVO(semester, clazzId, teacherId, offset, size);
        long total = scheduleMapper.countVO(semester, clazzId, teacherId);
        return Map.of("records", records, "total", total, "page", page, "size", size);
    }

    @Override
    public List<ScheduleVO> getByTeacher(Long teacherId, String semester) {
        return scheduleMapper.selectByTeacher(teacherId, semester);
    }

    @Override
    public List<ScheduleVO> getByClassroom(Long classroomId, String semester) {
        return scheduleMapper.selectByClassroom(classroomId, semester);
    }

    @Override
    public List<ScheduleVO> getByClass(Long clazzId, String semester) {
        return scheduleMapper.selectByClass(clazzId, semester);
    }

    private int categoryWeight(Course course) {
        if (course.getCourseType() == null) return 0;
        return switch (course.getCourseType().toLowerCase()) {
            case "required" -> 3;
            case "elective" -> 1;
            default -> 0;
        };
    }

    /** BigDecimal 比较器，处理 null */
    private static class BigDecimalComparator {
        static int compare(java.math.BigDecimal a, java.math.BigDecimal b) {
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return a.compareTo(b);
        }
    }
}
