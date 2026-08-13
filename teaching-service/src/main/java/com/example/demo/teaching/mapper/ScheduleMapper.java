package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Schedule;
import com.example.demo.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /** 多表 JOIN 分页查询排课结果 */
    List<ScheduleVO> selectPageVO(@Param("semester") String semester,
                                  @Param("clazzId") Long clazzId,
                                  @Param("teacherId") Long teacherId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    long countVO(@Param("semester") String semester,
                 @Param("clazzId") Long clazzId,
                 @Param("teacherId") Long teacherId);

    /** 按教师查询课表 */
    List<ScheduleVO> selectByTeacher(@Param("teacherId") Long teacherId,
                                     @Param("semester") String semester);

    /** 按教室查询课表 */
    List<ScheduleVO> selectByClassroom(@Param("classroomId") Long classroomId,
                                       @Param("semester") String semester);

    /** 按班级查询课表 */
    List<ScheduleVO> selectByClass(@Param("clazzId") Long clazzId,
                                   @Param("semester") String semester);

    /** 查询某学期已有的排课冲突信息（用于排课算法约束检查） */
    List<Schedule> selectBySemester(@Param("semester") String semester);

    /** 清空某学期全部排课 */
    int deleteBySemester(@Param("semester") String semester);
}
