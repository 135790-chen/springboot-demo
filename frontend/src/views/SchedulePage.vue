<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSchedulePage, generateSchedule, clearSchedule, getTimeSlots, getTeacherSchedule, getClassroomSchedule, getClassSchedule } from '@/api/schedule'
import { getClassroomPage } from '@/api/classroom'
import { getClassPage } from '@/api/class'
import { getTeacherPage } from '@/api/teacher'
import type { ScheduleEntry, Classroom, TimeSlot } from '@/types/models'

const semester = ref('2025-2026-1')
const majorId = ref<number | undefined>(undefined)
const generating = ref(false)
const genResult = ref<any>(null)

const timeSlots = ref<TimeSlot[]>([])
const classroomOptions = ref<Classroom[]>([])
const classOptions = ref<any[]>([])
const teacherOptions = ref<any[]>([])

const filterClazzId = ref<number | undefined>(undefined)
const filterTeacherId = ref<number | undefined>(undefined)

const columns = [
  { prop: 'slotName', label: '时段' },
  { prop: 'courseName', label: '课程' },
  { prop: 'teacherName', label: '教师' },
  { prop: 'className', label: '班级' },
  { prop: 'classroomName', label: '教室' },
  { prop: 'credit', label: '学分' },
  { prop: 'capacity', label: '容量' }
]
const data = ref<ScheduleEntry[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)

async function loadRefs() {
  try {
    const [tsRes, crRes, clRes, tcRes] = await Promise.all([
      getTimeSlots(),
      getClassroomPage({ page: 1, size: 99 }),
      getClassPage({ page: 1, size: 99 }),
      getTeacherPage({ page: 1, size: 99 })
    ])
    if (tsRes.data.code === 200) timeSlots.value = tsRes.data.data || []
    if (crRes.data.code === 200 && crRes.data.data) classroomOptions.value = crRes.data.data.records
    if (clRes.data.code === 200 && clRes.data.data) classOptions.value = clRes.data.data.records
    if (tcRes.data.code === 200 && tcRes.data.data) teacherOptions.value = tcRes.data.data.records
  } catch { /* ignore */ }
}

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 15, semester: semester.value }
    if (filterClazzId.value) params.clazzId = filterClazzId.value
    if (filterTeacherId.value) params.teacherId = filterTeacherId.value
    const res = await getSchedulePage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 15)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

async function doGenerate() {
  if (!semester.value) { ElMessage.warning('请输入学期'); return }
  try {
    await ElMessageBox.confirm(`确认对学期 ${semester.value} 执行自动排课？已有排课不会被覆盖，建议先"清空排课"。`, '确认排课', { type: 'info' })
  } catch { return }
  generating.value = true
  genResult.value = null
  try {
    const res = await generateSchedule(semester.value, majorId.value)
    if (res.data.code === 200) {
      genResult.value = res.data.data
      ElMessage.success(res.data.data.message || '排课完成')
      loadData(1)
    } else {
      ElMessage.error(res.data.message || '排课失败')
    }
  } catch { /* handled */ }
  finally { generating.value = false }
}

async function doClear() {
  if (!semester.value) { ElMessage.warning('请输入学期'); return }
  try {
    await ElMessageBox.confirm(`确定清空学期 ${semester.value} 的全部排课记录吗？此操作不可撤销。`, '确认清空', { type: 'warning' })
  } catch { return }
  try {
    const res = await clearSchedule(semester.value)
    ElMessage.success(res.data.data || '清空成功')
    data.value = []
    genResult.value = null
  } catch { /* handled */ }
}

function handleFilter() { loadData(1) }
function handleFilterReset() { filterClazzId.value = undefined; filterTeacherId.value = undefined; loadData(1) }

function getTypeLabel(type: string) {
  const map: Record<string, string> = { NORMAL: '普通教室', MULTIMEDIA: '多媒体', LAB: '实验室', LECTURE_HALL: '报告厅' }
  return map[type] || type
}

onMounted(() => { loadRefs(); loadData() })
</script>

<template>
  <div class="page">
    <!-- 排课操作 -->
    <div class="card">
      <div class="card-title">📅 自动排课</div>
      <div class="search-row" style="margin-top:12px">
        <el-input v-model="semester" placeholder="学期" size="small" style="width:150px" />
        <el-select v-model="majorId" placeholder="不限专业" size="small" style="width:180px" clearable>
          <el-option v-for="c in classOptions" :key="c.majorId" :label="c.major || '-'" :value="c.majorId" />
        </el-select>
        <el-button type="primary" size="small" :loading="generating" @click="doGenerate">🚀 执行排课</el-button>
        <el-button type="danger" size="small" @click="doClear">🗑 清空排课</el-button>
      </div>
      <div v-if="genResult" style="margin-top:10px;font-size:13px;color:var(--text2)">
        ✅ 排课完成：共 {{ genResult.totalCourses }} 门课程，成功 {{ genResult.scheduledCount }} 门
        <span v-if="genResult.failedCount > 0" style="color:var(--danger)">，失败 {{ genResult.failedCount }} 门</span>
        <ul v-if="genResult.failedCourses?.length" style="margin-top:4px;padding-left:20px;color:var(--danger)">
          <li v-for="(f, i) in genResult.failedCourses" :key="i">{{ f }}</li>
        </ul>
      </div>
    </div>

    <!-- 排课结果 -->
    <div class="card">
      <div class="card-title">📋 排课结果</div>
      <div class="search-row" style="margin-top:8px;margin-bottom:12px">
        <el-select v-model="filterClazzId" placeholder="全部班级" size="small" style="width:180px" clearable>
          <el-option v-for="c in classOptions" :key="c.id" :label="c.className || c.name" :value="c.id" />
        </el-select>
        <el-select v-model="filterTeacherId" placeholder="全部教师" size="small" style="width:180px" clearable>
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.teacherName || t.name" :value="t.id" />
        </el-select>
        <el-button type="primary" size="small" @click="handleFilter">筛选</el-button>
        <el-button size="small" @click="handleFilterReset">全部</el-button>
      </div>
      <el-table :data="data" border stripe size="small" v-loading="loading" max-height="500">
        <el-table-column prop="slotName" label="时间段" width="140" />
        <el-table-column prop="dayOfWeek" label="星期" width="60">
          <template #default="{ row }: { row: ScheduleEntry }">
            {{ ['','一','二','三','四','五','六','日'][row.dayOfWeek] || row.dayOfWeek }}
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="teacherName" label="教师" width="80" />
        <el-table-column prop="className" label="班级" width="140" />
        <el-table-column prop="classroomName" label="教室" width="140" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="classroomType" label="教室类型" width="90">
          <template #default="{ row }: { row: ScheduleEntry }">{{ getTypeLabel(row.classroomType) }}</template>
        </el-table-column>
      </el-table>
      <div style="margin-top:12px;display:flex;justify-content:center">
        <el-pagination v-model:current-page="currentPage" :total="totalPages * 15" :page-size="15" layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
</style>
