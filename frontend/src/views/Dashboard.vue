<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '@/api/request'
import StatCard from '@/components/StatCard.vue'
import ChartBox from '@/components/ChartBox.vue'
import type { Result, PageResult, StatSnapshot, Student, Course } from '@/types/models'

const statData = ref<StatSnapshot>({
  totalStudents: 0, totalTeachers: 0, totalCourses: 0, totalEnrollments: 0
})

const trendOption = ref<any>(null)
const gradeOption = ref<any>(null)
const courseTypeOption = ref<any>(null)

onMounted(() => {
  loadDashboard()
})

async function loadDashboard() {
  try {
    // 1) 统计卡片
    const latestRes = await http.get<Result<StatSnapshot>>('/api/edu/stat/latest')
    if (latestRes.data.code === 200 && latestRes.data.data) {
      statData.value = latestRes.data.data
    }

    // 2) 趋势图
    const recentRes = await http.get<Result<StatSnapshot[]>>('/api/edu/stat/recent')
    if (recentRes.data.code === 200 && recentRes.data.data?.length) {
      const data = recentRes.data.data.reverse()
      trendOption.value = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['学生', '教师', '课程'], top: 0 },
        grid: { left: 50, right: 20, top: 35, bottom: 20 },
        xAxis: {
          type: 'category',
          data: data.map((d: StatSnapshot) => d.statDate),
          axisLabel: { rotate: 30, fontSize: 11 }
        },
        yAxis: { type: 'value' },
        series: [
          { name: '学生', type: 'bar', data: data.map((d: StatSnapshot) => d.totalStudents), itemStyle: { color: '#1890ff' } },
          { name: '教师', type: 'bar', data: data.map((d: StatSnapshot) => d.totalTeachers), itemStyle: { color: '#52c41a' } },
          { name: '课程', type: 'bar', data: data.map((d: StatSnapshot) => d.totalCourses), itemStyle: { color: '#faad14' } }
        ]
      }
    }

    // 3) 年级分布饼图
    const studentRes = await http.get<Result<PageResult<Student>>>('/api/edu/student/page', {
      params: { page: 1, size: 9999 }
    })
    if (studentRes.data.code === 200 && studentRes.data.data) {
      const gradeMap: Record<string, number> = {}
      studentRes.data.data.records.forEach((s: Student) => {
        const g = s.grade || '未知'
        gradeMap[g] = (gradeMap[g] || 0) + 1
      })
      gradeOption.value = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '55%'],
          data: Object.entries(gradeMap).map(([name, value]) => ({ name, value })),
          label: { formatter: '{b}\n{d}%' },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 }
        }]
      }
    }

    // 4) 课程类型饼图
    const courseRes = await http.get<Result<PageResult<Course>>>('/api/edu/course/page', {
      params: { page: 1, size: 9999 }
    })
    if (courseRes.data.code === 200 && courseRes.data.data) {
      const typeMap: Record<string, number> = {}
      courseRes.data.data.records.forEach((c: Course) => {
        const t = c.courseType === 'required' ? '必修' : c.courseType === 'elective' ? '选修' : '其他'
        typeMap[t] = (typeMap[t] || 0) + 1
      })
      courseTypeOption.value = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}门 ({d}%)' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '55%'],
          data: Object.entries(typeMap).map(([name, value]) => ({ name, value })),
          label: { formatter: '{b}\n{d}%' },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 }
        }]
      }
    }
  } catch {
    // 后端未启动时静默失败
  }
}
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <StatCard icon="🎓" iconBg="#e6f7ff" :value="statData.totalStudents" label="在读学生" />
      <StatCard icon="👨‍🏫" iconBg="#f6ffed" :value="statData.totalTeachers" label="在职教师" />
      <StatCard icon="📖" iconBg="#fff7e6" :value="statData.totalCourses" label="课程总数" />
      <StatCard icon="📝" iconBg="#fff2f0" :value="statData.totalEnrollments" label="选课记录" />
    </div>

    <!-- 趋势图 -->
    <div class="chart-card" style="margin-bottom: 16px">
      <div class="chart-title">📈 近30天数据趋势</div>
      <ChartBox :option="trendOption" height="300px" />
    </div>

    <!-- 饼图行 -->
    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-title">🎓 学生年级分布</div>
        <ChartBox :option="gradeOption" height="300px" />
      </div>
      <div class="chart-card">
        <div class="chart-title">📖 课程类型占比</div>
        <ChartBox :option="courseTypeOption" height="300px" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}
.chart-card {
  background: var(--surface);
  border-radius: var(--radius);
  padding: 16px 20px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--border);
}
.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
@media (max-width: 1000px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>
