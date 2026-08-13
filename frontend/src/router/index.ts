import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/Dashboard.vue'),
      meta: { title: '数据看板' }
    },
    {
      path: '/students',
      name: 'StudentList',
      component: () => import('@/views/StudentList.vue'),
      meta: { title: '学生管理' }
    },
    {
      path: '/classes',
      name: 'ClassList',
      component: () => import('@/views/ClassList.vue'),
      meta: { title: '班级管理' }
    },
    {
      path: '/teachers',
      name: 'TeacherList',
      component: () => import('@/views/TeacherList.vue'),
      meta: { title: '教师管理' }
    },
    {
      path: '/courses',
      name: 'CourseList',
      component: () => import('@/views/CourseList.vue'),
      meta: { title: '课程管理' }
    },
    {
      path: '/enrollments',
      name: 'EnrollmentPage',
      component: () => import('@/views/EnrollmentPage.vue'),
      meta: { title: '选课管理' }
    },
    {
      path: '/colleges',
      name: 'CollegeList',
      component: () => import('@/views/CollegeList.vue'),
      meta: { title: '学院管理' }
    },
    {
      path: '/majors',
      name: 'MajorList',
      component: () => import('@/views/MajorList.vue'),
      meta: { title: '专业管理' }
    },
    {
      path: '/training-plans',
      name: 'TrainingPlanList',
      component: () => import('@/views/TrainingPlanList.vue'),
      meta: { title: '培养方案' }
    },
    {
      path: '/graduation',
      name: 'GraduationCheck',
      component: () => import('@/views/GraduationCheck.vue'),
      meta: { title: '毕业审核' }
    },
    {
      path: '/classrooms',
      name: 'ClassroomList',
      component: () => import('@/views/ClassroomList.vue'),
      meta: { title: '教室管理' }
    },
    {
      path: '/schedule',
      name: 'SchedulePage',
      component: () => import('@/views/SchedulePage.vue'),
      meta: { title: '排课管理' }
    },
    {
      path: '/kafka',
      name: 'KafkaPanel',
      component: () => import('@/views/KafkaPanel.vue'),
      meta: { title: 'Kafka 消息', requireAdmin: true }
    },
    {
      path: '/profile',
      name: 'UserProfile',
      component: () => import('@/views/UserProfile.vue'),
      meta: { title: '我的信息' }
    }
  ]
})

// 权限守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requireAdmin && token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (payload.role !== 'admin') {
        ElMessage.warning('此功能仅管理员可用')
        next('/dashboard')
        return
      }
    } catch {
      // token 无效，放行让 API 返回 401 处理
    }
  }

  next()
})

export default router
