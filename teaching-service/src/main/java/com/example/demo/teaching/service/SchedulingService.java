package com.example.demo.teaching.service;

import com.example.demo.vo.ScheduleVO;

import java.util.List;
import java.util.Map;

public interface SchedulingService {

    /** 触发排课算法 */
    Map<String, Object> generate(String semester, Long majorId);

    /** 清空某学期排课 */
    int clear(String semester);

    /** 分页查询排课结果 */
    Map<String, Object> getPage(String semester, Long clazzId, Long teacherId, int page, int size);

    /** 按教师查课表 */
    List<ScheduleVO> getByTeacher(Long teacherId, String semester);

    /** 按教室查课表 */
    List<ScheduleVO> getByClassroom(Long classroomId, String semester);

    /** 按班级查课表 */
    List<ScheduleVO> getByClass(Long clazzId, String semester);
}
