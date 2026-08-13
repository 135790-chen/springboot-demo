package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.example.demo.entity.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 纯 Mockito 环境下初始化 MyBatis-Plus lambda 列缓存。
 * 每个需要 LambdaUpdateWrapper.set() 的测试类在 @BeforeAll 中调用 {@link #initAll()}。
 */
final class LambdaCacheInitializer {

    private static volatile boolean initialized = false;

    @SuppressWarnings("unchecked")
    static synchronized void initAll() throws Exception {
        if (initialized) return;

        Field cacheField = LambdaUtils.class.getDeclaredField("COLUMN_CACHE_MAP");
        cacheField.setAccessible(true);
        Map<String, Map<String, ColumnCache>> cache =
                (Map<String, Map<String, ColumnCache>>) cacheField.get(null);

        cache.put(Clazz.class.getName(), mapOf(
                "ID,class_id", "CLASSNAME,class_name", "CLASSCODE,class_code",
                "COLLEGEID,college_id", "MAJORID,major_id", "COUNSELORID,counselor_id",
                "GRADE,grade", "MAJOR,major", "CLASSSORT,class_sort",
                "CLASSSTATUS,class_status", "CLASSDELETED,class_deleted",
                "CLASSREMARK,class_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Student.class.getName(), mapOf(
                "ID,student_id", "STUDENTNO,student_no", "STUDENTNAME,student_name",
                "GENDER,gender", "PHONE,phone", "EMAIL,email",
                "BIRTHDAY,birthday", "CLASSID,class_id", "ENROLLMENTYEAR,enrollment_year",
                "STUDENTSTATUS,student_status", "STUDENTDELETED,student_deleted",
                "STUDENTREMARK,student_remark", "GRADE,grade",
                "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Teacher.class.getName(), mapOf(
                "ID,teacher_id", "TEACHERNO,teacher_no", "TEACHERNAME,teacher_name",
                "GENDER,gender", "PHONE,phone", "EMAIL,email",
                "TITLE,title", "TEACHERSTATUS,teacher_status", "TEACHERDELETED,teacher_deleted",
                "TEACHERREMARK,teacher_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Course.class.getName(), mapOf(
                "ID,course_id", "COURSENAME,course_name", "COURSECODE,course_code",
                "CREDIT,credit", "COURSEHOURS,course_hours", "COURSETYPE,course_type",
                "TEACHERID,teacher_id", "SEMESTER,semester", "COURSESTATUS,course_status",
                "COURSEDELETED,course_deleted", "COURSEREMARK,course_remark",
                "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Enrollment.class.getName(), mapOf(
                "ID,rel_id", "STUDENTID,student_id", "COURSEID,course_id",
                "SCORE,score", "RELSTATUS,rel_status",
                "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(College.class.getName(), mapOf(
                "ID,college_id", "COLLEGENAME,college_name", "COLLEGECODE,college_code",
                "COLLEGESTATUS,college_status", "COLLEGEDELETED,college_deleted",
                "COLLEGEREMARK,college_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Major.class.getName(), mapOf(
                "ID,major_id", "MAJORNAME,major_name", "MAJORCODE,major_code",
                "COLLEGEID,college_id", "DIRECTORID,director_id",
                "MAJORSTATUS,major_status", "MAJORDELETED,major_deleted",
                "MAJORREMARK,major_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(TrainingPlan.class.getName(), mapOf(
                "ID,plan_id", "PLANNAME,plan_name", "MAJORID,major_id",
                "GRADE,grade", "VERSION,version",
                "TOTALREQUIREDCREDITS,total_required_credits",
                "MAJORELECTIVEMINCREDITS,major_elective_min_credits",
                "GENERALELECTIVEMINCREDITS,general_elective_min_credits",
                "PLANSTATUS,plan_status", "PLANDELETED,plan_deleted",
                "PLANREMARK,plan_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(School.class.getName(), mapOf(
                "ID,school_id", "SCHOOLNAME,school_name", "SCHOOLCODE,school_code",
                "SCHOOLSTATUS,school_status", "SCHOOLDELETED,school_deleted",
                "SCHOOLREMARK,school_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Counselor.class.getName(), mapOf(
                "ID,counselor_id", "USERID,user_id", "COUNSELORNO,counselor_no",
                "COUNSELORNAME,counselor_name", "PHONE,phone", "EMAIL,email",
                "COUNSELORSTATUS,counselor_status", "COUNSELORDELETED,counselor_deleted",
                "COUNSELORREMARK,counselor_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        cache.put(Supervisor.class.getName(), mapOf(
                "ID,supervisor_id", "USERID,user_id", "SUPERVISORNO,supervisor_no",
                "SUPERVISORNAME,supervisor_name", "PHONE,phone", "EMAIL,email",
                "SUPERVISORSTATUS,supervisor_status", "SUPERVISORDELETED,supervisor_deleted",
                "SUPERVISORREMARK,supervisor_remark", "GMTCREATE,gmt_create", "GMTMODIFIED,gmt_modified"));

        initialized = true;
    }

    private static Map<String, ColumnCache> mapOf(String... pairs) {
        Map<String, ColumnCache> m = new HashMap<>();
        for (String pair : pairs) {
            String[] parts = pair.split(",");
            m.put(parts[0], new ColumnCache(parts[1], parts[1]));
        }
        return m;
    }

    private LambdaCacheInitializer() {}
}