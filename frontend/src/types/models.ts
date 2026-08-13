// Re-export from api.ts for convenience
export type { Result, PageResult } from './api'

/** 用户信息（JWT 解析） */
export interface UserInfo {
  userId: number
  username: string
  role: 'admin' | 'student'
  studentId?: number
}

/** 登录/注册响应 */
export interface LoginResult {
  token: string
}

/** 学生 */
export interface Student {
  studentId?: number
  studentNo?: string
  studentName: string
  gender?: number
  birthday?: string
  grade?: string
  email?: string
  phone?: string
  classId?: number
  className?: string
  enrollmentYear?: string
  studentStatus?: number
}

/** 班级 */
export interface ClassInfo {
  id?: number
  classCode: string
  className: string
  grade?: string
  major?: string
  classSort?: number
  classStatus?: number
  classRemark?: string
}

/** 教师 */
export interface Teacher {
  id?: number
  teacherNo?: string
  teacherName: string
  gender?: number
  title?: string
  phone?: string
  email?: string
  teacherStatus?: number
  teacherRemark?: string
}

/** 课程 */
export interface Course {
  courseId?: number
  courseCode: string
  courseName: string
  credit?: number
  courseHours?: number
  courseType?: 'required' | 'elective'
  teacherId?: number
  teacherName?: string
  semester?: string
  courseStatus?: number
  courseRemark?: string
}

/** 选课记录 */
export interface Enrollment {
  relId?: number
  studentId?: number
  courseId?: number
  courseCode?: string
  courseName?: string
  credit?: number
  courseType?: string
  teacherName?: string
  semester?: string
  score?: number
  relStatus?: number
}

/** 秒杀课程 */
export interface SeckillCourse {
  courseId: number
  courseCode: string
  courseName: string
  credit: number
  courseType: string
  semester: string
  maxStudents: number
  remaining: number
}

/** 学院 */
export interface College {
  id?: number
  collegeName: string
  collegeCode: string
  collegeStatus?: number
  collegeRemark?: string
}

/** 专业 */
export interface Major {
  id?: number
  majorName: string
  majorCode: string
  collegeId?: number
  collegeName?: string
  majorStatus?: number
  majorRemark?: string
}

/** 培养方案 */
export interface TrainingPlan {
  id?: number
  planName: string
  majorId?: number
  majorName?: string
  grade?: string
  version?: number
  totalRequiredCredits?: number
  majorElectiveMinCredits?: number
  generalElectiveMinCredits?: number
  planStatus?: number
  planRemark?: string
}

/** 毕业审核结果 */
export interface GraduationResult {
  passed: boolean
  planName?: string
  studentName?: string
  studentNo?: string
  collegeName?: string
  majorName?: string
  grade?: string
  totalEarnedCredits?: number
  totalMaxCredits?: number
  creditDetails?: CreditDetail[]
  missingItems?: string[]
}

export interface CreditDetail {
  categoryName: string
  requiredCredits: number
  earnedCredits: number
  maxCredits: number
  gap: number
  satisfied: boolean
}

/** 教室 */
export interface Classroom {
  id?: number
  classroomName: string
  classroomCode: string
  capacity: number
  classroomType: string
  location?: string
  building?: string
  floor?: number
  classroomStatus?: number
  classroomRemark?: string
  gmtCreate?: string
}

/** 时间段 */
export interface TimeSlot {
  id: number
  slotName: string
  dayOfWeek: number
  startPeriod: number
  endPeriod: number
  slotStatus: number
}

/** 排课结果 */
export interface ScheduleEntry {
  scheduleId: number
  courseId: number
  courseName: string
  courseCode: string
  credit: number
  teacherId: number
  teacherName: string
  classroomId: number
  classroomName: string
  classroomCode: string
  classroomType: string
  capacity: number
  timeSlotId: number
  slotName: string
  dayOfWeek: number
  startPeriod: number
  endPeriod: number
  clazzId: number
  className: string
  semester: string
  weekStart: number
  weekEnd: number
  scheduleStatus: number
}

/** 统计快照 */
export interface StatSnapshot {
  statDate?: string
  totalStudents: number
  totalTeachers: number
  totalCourses: number
  totalEnrollments: number
}

/** 学生查询参数 */
export interface StudentQuery {
  page?: number
  size?: number
  studentName?: string
  studentNo?: string
  classId?: number
  className?: string
  grade?: string
  studentStatus?: number
}

/** 班级查询参数 */
export interface ClassQuery {
  page?: number
  size?: number
  className?: string
  classCode?: string
  grade?: string
  classStatus?: number
}

/** 教师查询参数 */
export interface TeacherQuery {
  page?: number
  size?: number
  teacherName?: string
  teacherNo?: string
  title?: string
  teacherStatus?: number
}

/** 课程查询参数 */
export interface CourseQuery {
  page?: number
  size?: number
  courseName?: string
  courseCode?: string
  courseType?: string
  courseStatus?: number
}
