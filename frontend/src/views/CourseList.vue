<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getCoursePage, addCourse, updateCourse, deleteCourse, getCourseStudents } from '@/api/course'
import type { Course, Student } from '@/types/models'
import { CourseTypeLabels } from '@/types/enums'

const auth = useAuthStore()

// ---- 课程主表 ----
const searchForm = reactive({ courseName: '', courseCode: '', courseType: '', courseStatus: '' })
const columns = [
  { prop: 'courseCode', label: '编码' },
  { prop: 'courseName', label: '名称' },
  { prop: 'credit', label: '学分' },
  { prop: 'courseHours', label: '学时' },
  { prop: 'courseType', label: '类型', formatter: (r: Course) => CourseTypeLabels[r.courseType || ''] || r.courseType || '-' },
  { prop: 'teacherName', label: '授课教师', formatter: (r: Course) => r.teacherName || '-' },
  { prop: 'semester', label: '学期', formatter: (r: Course) => r.semester || '-' },
  { prop: 'courseStatus', label: '状态', formatter: (r: Course) => r.courseStatus === 1 ? '开课' : '停课' }
]
const data = ref<Course[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<Course | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const isEdit = ref(false)
const formData = reactive<Partial<Course>>({ courseName: '', courseCode: '', credit: 0, courseHours: 0, courseType: 'required', teacherId: undefined, semester: '', courseStatus: 1, courseRemark: '' })

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.courseName) params.courseName = searchForm.courseName
    if (searchForm.courseCode) params.courseCode = searchForm.courseCode
    if (searchForm.courseType) params.courseType = searchForm.courseType
    if (searchForm.courseStatus) params.courseStatus = searchForm.courseStatus
    const res = await getCoursePage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; studentData.value = []; loadData(1) }
function handleReset() { searchForm.courseName = ''; searchForm.courseCode = ''; searchForm.courseType = ''; searchForm.courseStatus = ''; handleSearch() }
function handleRowClick(row: Course) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增课程'
  Object.assign(formData, { courseName: '', courseCode: '', credit: 0, courseHours: 0, courseType: 'required', teacherId: undefined, semester: '', courseStatus: 1, courseRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择课程'); return }
  isEdit.value = true; dialogTitle.value = '修改课程'
  const s = selected.value
  Object.assign(formData, {
    courseName: s.courseName, courseCode: s.courseCode, credit: s.credit ?? 0, courseHours: s.courseHours ?? 0,
    courseType: s.courseType || 'required', teacherId: s.teacherId, semester: s.semester || '',
    courseStatus: s.courseStatus ?? 1, courseRemark: s.courseRemark || ''
  })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.courseName) { ElMessage.warning('请填写课程名称'); return }
  try {
    const res = isEdit.value ? await updateCourse({ ...formData, courseId: selected.value?.courseId }) : await addCourse(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.courseId) { ElMessage.warning('请先选中课程'); return }
  try {
    await ElMessageBox.confirm('确定删除该课程吗？', '确认删除', { type: 'warning' })
    const res = await deleteCourse(selected.value.courseId)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

// ---- 课程选课学生子表 ----
const studentSearch = reactive({ studentName: '', relStatus: '' })
const studentColumns = [
  { prop: 'studentNo', label: '学号' },
  { prop: 'studentName', label: '姓名' },
  { prop: 'gender', label: '性别', formatter: (r: Student) => r.gender === 1 ? '男' : r.gender === 2 ? '女' : '未知' },
  { prop: 'className', label: '班级' },
  { prop: 'score', label: '成绩', formatter: (r: any) => r.score != null ? String(r.score) : '-' },
  { prop: 'relStatus', label: '状态', formatter: (r: any) => r.relStatus === 1 ? '在读' : r.relStatus === 2 ? '已修完' : r.relStatus === 3 ? '退课' : '' }
]
const studentData = ref<any[]>([])
const studentLoading = ref(false)
const studentPage = ref(1)
const studentTotalPages = ref(0)

async function loadCourseStudents(page: number = 1) {
  if (!selected.value?.courseId) { ElMessage.warning('请先点击上方课程行选中一个课程'); return }
  studentLoading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (studentSearch.studentName) params.studentName = studentSearch.studentName
    if (studentSearch.relStatus) params.relStatus = studentSearch.relStatus
    const res = await getCourseStudents(selected.value.courseId, params)
    if (res.data.code === 200 && res.data.data) {
      studentData.value = res.data.data.records
      studentTotalPages.value = Math.ceil(res.data.data.total / 10)
      studentPage.value = page
    }
  } finally { studentLoading.value = false }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page">
    <!-- 搜索 -->
    <div class="card">
      <div class="card-title">🔍 课程查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.courseName" placeholder="课程名称" size="small" style="width:140px" clearable />
        <el-input v-model="searchForm.courseCode" placeholder="课程编码" size="small" style="width:140px" clearable />
        <el-select v-model="searchForm.courseType" placeholder="全部类型" size="small" style="width:120px" clearable>
          <el-option label="必修" value="required" /><el-option label="选修" value="elective" />
        </el-select>
        <el-select v-model="searchForm.courseStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="开课" value="1" /><el-option label="停课" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>

    <!-- 课程表格 -->
    <div class="card">
      <div class="card-header">
        <div class="card-title">📖 课程列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.courseName }} (ID: {{ selected.courseId }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.courseId ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>

    <!-- 课程选课学生 -->
    <div class="card">
      <div class="card-title">👥 查看课程选课学生 <span class="hint" v-if="!selected">先点击上方课程行选中</span></div>
      <div class="selected-info" style="margin-bottom:8px" v-if="selected">课程: {{ selected.courseName }} (ID: {{ selected.courseId }})</div>
      <div class="search-row" style="margin-bottom:12px">
        <el-input v-model="studentSearch.studentName" placeholder="学生姓名" size="small" style="width:130px" clearable />
        <el-select v-model="studentSearch.relStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="在读" value="1" /><el-option label="已修完" value="2" /><el-option label="退课" value="3" />
        </el-select>
        <el-button type="primary" size="small" @click="loadCourseStudents(1)">查询选课学生</el-button>
      </div>
      <DataTable :columns="studentColumns" :data="studentData" :loading="studentLoading" :current-page="studentPage" :total-pages="studentTotalPages" empty-text="请先选中课程，再点「查询选课学生」" @page-change="loadCourseStudents" />
    </div>

    <!-- 新增/修改弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px">
      <el-form :model="formData" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程名称"><el-input v-model="formData.courseName" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程编码"><el-input v-model="formData.courseCode" placeholder="请输入" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="学分"><el-input-number v-model="formData.credit" :min="0" :step="0.5" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学时"><el-input-number v-model="formData.courseHours" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="类型"><el-select v-model="formData.courseType" style="width:100%"><el-option value="required" label="必修" /><el-option value="elective" label="选修" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="formData.courseStatus" style="width:100%"><el-option :value="1" label="开课" /><el-option :value="0" label="停课" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="教师ID"><el-input-number v-model="formData.teacherId" :min="0" style="width:100%" placeholder="请输入教师ID" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学期"><el-input v-model="formData.semester" placeholder="如 2024-2025-1" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="formData.courseRemark" placeholder="备注（选填）" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '新增' }}</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.card-header .card-title { margin-bottom: 0; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
.selected-info { font-size: 13px; color: var(--accent); font-weight: 500; }
.hint { font-size: 12px; color: var(--text3); font-weight: 400; margin-left: 4px; }
</style>
