export enum Gender {
  Unknown = 0,
  Male = 1,
  Female = 2
}

export enum StudentStatus {
  Withdrawn = 0,
  Active = 1,
  Suspended = 2,
  Graduated = 3
}

export enum TeacherStatus {
  Inactive = 0,
  Active = 1
}

export enum CourseStatus {
  Closed = 0,
  Open = 1
}

export enum ClassStatus {
  Disabled = 0,
  Normal = 1
}

export enum CollegeStatus {
  Disabled = 0,
  Normal = 1
}

export enum MajorStatus {
  Disabled = 0,
  Normal = 1
}

export enum PlanStatus {
  Disabled = 0,
  Enabled = 1
}

export enum EnrollmentStatus {
  Active = 1,
  Completed = 2,
  Dropped = 3
}

export const StudentStatusLabels: Record<number, string> = {
  [StudentStatus.Withdrawn]: '退学',
  [StudentStatus.Active]: '在读',
  [StudentStatus.Suspended]: '休学',
  [StudentStatus.Graduated]: '毕业'
}

export const GenderLabels: Record<number, string> = {
  [Gender.Unknown]: '未知',
  [Gender.Male]: '男',
  [Gender.Female]: '女'
}

export const CourseTypeLabels: Record<string, string> = {
  required: '必修',
  elective: '选修'
}
