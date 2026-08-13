<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import DataTable from '@/components/DataTable.vue'
import { getStudentPage, addStudent, updateStudent, deleteStudent, getStudentCourses } from '@/api/student'
import type { Student, Enrollment } from '@/types/models'
import { StudentStatusLabels } from '@/types/enums'

const auth = useAuthStore()
const app = useAppStore()

// ========== 查询 ==========
const searchForm = reactive({
  studentName: '',
  studentNo: '',
  classId: '',
  className: '',
  grade: '',
  studentStatus: ''
})

// ========== 表格 ==========
const studentColumns = [
  { prop: 'studentNo', label: '学号' },
  { prop: 'studentName', label: '姓名' },
  { prop: 'gender', label: '性别', formatter: (r: Student) => r.gender === 1 ? '男' : r.gender === 2 ? '女' : '未知' },
  { prop: 'className', label: '班级' },
  { prop: 'grade', label: '年级' },
  { prop: 'enrollmentYear', label: '入学年份' },
  { prop: 'studentStatus', label: '状态', formatter: (r: Student) => StudentStatusLabels[r.studentStatus ?? 1] },
  { prop: 'phone', label: '电话' }
]
const studentData = ref<Student[]>([])
const studentLoading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selectedStudent = ref<Student | null>(null)

// ========== 新增/修改弹窗 ==========
const dialogVisible = ref(false)
const dialogTitle = ref('新增学生')
const isEdit = ref(false)
const formData = reactive<Partial<Student>>({
  studentName: '', studentNo: '', gender: 0,
  grade: '', classId: undefined, enrollmentYear: '',
  phone: '', email: '', birthday: '', studentStatus: 1
})

// ========== 学生选课子表 ==========
const studentCourses = ref<Enrollment[]>([])
const courseLoading = ref(false)
const coursePage = ref(1)
const courseTotalPages = ref(0)
const courseColumns = [
  { prop: 'relId', label: 'relId' },
  { prop: 'courseCode', label: '课程编码' },
  { prop: 'courseName', label: '课程名称' },
  { prop: 'credit', label: '学分' },
  { prop: 'courseType', label: '类型', formatter: (r: Enrollment) => r.courseType === 'required' ? '必修' : '选修' },
  { prop: 'teacherName', label: '教师' },
  { prop: 'semester', label: '学期' },
  { prop: 'score', label: '成绩', formatter: (r: Enrollment) => r.score != null ? String(r.score) : '-' },
  { prop: 'relStatus', label: '状态', formatter: (r: Enrollment) =>
    r.relStatus === 1 ? '在读' : r.relStatus === 2 ? '已修完' : r.relStatus === 3 ? '退课' : '' }
]

// ========== 方法 ==========
async function loadStudents(page: number = 1) {
  studentLoading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.studentName) params.studentName = searchForm.studentName
    if (searchForm.studentNo) params.studentNo = searchForm.studentNo
    if (searchForm.classId) params.classId = searchForm.classId
    if (searchForm.className) params.className = searchForm.className
    if (searchForm.grade) params.grade = searchForm.grade
    if (searchForm.studentStatus) params.studentStatus = searchForm.studentStatus

    const res = await getStudentPage(params)
    if (res.data.code === 200 && res.data.data) {
      studentData.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } catch {
    // handled by interceptor
  } finally {
    studentLoading.value = false
  }
}

function handleSearch() {
  selectedStudent.value = null
  studentCourses.value = []
  loadStudents(1)
}

function handleReset() {
  searchForm.studentName = ''
  searchForm.studentNo = ''
  searchForm.classId = ''
  searchForm.className = ''
  searchForm.grade = ''
  searchForm.studentStatus = ''
  handleSearch()
}

function handleRowClick(row: Student) {
  selectedStudent.value = row
  if (row.studentId) app.selectStudent(row.studentId, row.studentName)
}

function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '新增学生'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog() {
  if (!selectedStudent.value) {
    ElMessage.warning('请先在表格中点击选择学生')
    return
  }
  isEdit.value = true
  dialogTitle.value = '修改学生'
  const s = selectedStudent.value
  Object.assign(formData, {
    studentName: s.studentName,
    studentNo: s.studentNo || '',
    gender: s.gender ?? 0,
    grade: s.grade || '',
    classId: s.classId,
    enrollmentYear: s.enrollmentYear || '',
    phone: s.phone || '',
    email: s.email || '',
    birthday: s.birthday || '',
    studentStatus: s.studentStatus ?? 1
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(formData, {
    studentName: '', studentNo: '', gender: 0,
    grade: '', classId: undefined, enrollmentYear: '',
    phone: '', email: '', birthday: '', studentStatus: 1
  })
}

async function handleSubmit() {
  if (!formData.studentName) {
    ElMessage.warning('请填写学生姓名')
    return
  }
  try {
    let res: any
    if (isEdit.value) {
      res = await updateStudent({ ...formData, studentId: selectedStudent.value?.studentId })
      ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    } else {
      res = await addStudent(formData)
      ElMessage[res.data.code === 200 ? 'success' : 'error'](
        res.data.code === 200 ? '新增成功' : res.data.message
      )
    }
    if (res.data.code === 200) {
      dialogVisible.value = false
      loadStudents(currentPage.value)
    }
  } catch {
    // handled by interceptor
  }
}

async function handleDelete() {
  if (!selectedStudent.value?.studentId) {
    ElMessage.warning('请先选中学生')
    return
  }
  try {
    await ElMessageBox.confirm('确定逻辑删除该学生吗？', '确认删除', { type: 'warning' })
    const res = await deleteStudent(selectedStudent.value.studentId)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selectedStudent.value = null
    loadStudents(currentPage.value)
  } catch {
    // 用户取消
  }
}

async function loadStudentCourses(page: number = 1) {
  if (!selectedStudent.value?.studentId) {
    ElMessage.warning('请先点击上方学生行选中一个学生')
    return
  }
  courseLoading.value = true
  try {
    const res = await getStudentCourses(selectedStudent.value.studentId, { page, size: 10 })
    if (res.data.code === 200 && res.data.data) {
      studentCourses.value = res.data.data.records
      courseTotalPages.value = Math.ceil(res.data.data.total / 10)
      coursePage.value = page
    }
  } finally {
    courseLoading.value = false
  }
}
</script>

<template>
  <div class="student-page">
    <!-- 搜索区 -->
    <div class="card">
      <div class="card-title">🔍 学生查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.studentName" placeholder="姓名" size="small" style="width:120px" clearable />
        <el-input v-model="searchForm.studentNo" placeholder="学号" size="small" style="width:120px" clearable />
        <el-input v-model="searchForm.classId" placeholder="班级ID" size="small" style="width:100px" clearable />
        <el-input v-model="searchForm.className" placeholder="班级名称" size="small" style="width:130px" clearable />
        <el-input v-model="searchForm.grade" placeholder="年级" size="small" style="width:110px" clearable />
        <el-select v-model="searchForm.studentStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="在读" value="1" />
          <el-option label="休学" value="2" />
          <el-option label="毕业" value="3" />
          <el-option label="退学" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>

    <!-- 学生表格 -->
    <div class="card">
      <div class="card-header">
        <div class="card-title">📋 学生列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selectedStudent">
            已选中: {{ selectedStudent.studentName }} (ID: {{ selectedStudent.studentId }})
          </span>
          <el-button type="primary" size="small" @click="openAddDialog">+ 新增</el-button>
          <el-button size="small" @click="openEditDialog">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable
        :columns="studentColumns"
        :data="studentData"
        :loading="studentLoading"
        :current-page="currentPage"
        :total-pages="totalPages"
        :selected-id="selectedStudent?.studentId ?? null"
        empty-text="点击「查询」加载学生数据"
        @row-click="handleRowClick"
        @page-change="loadStudents"
      />
    </div>

    <!-- 已选课程子表 -->
    <div class="card">
      <div class="card-header">
        <div class="card-title">
          📋 已选课程
          <span class="hint" v-if="!selectedStudent">先点击上方学生行，再点查询</span>
        </div>
        <el-button type="primary" size="small" @click="loadStudentCourses(1)">查询该生选课</el-button>
      </div>
      <div class="selected-info" style="margin-bottom:12px" v-if="selectedStudent">
        学生: {{ selectedStudent.studentName }} (ID: {{ selectedStudent.studentId }})
      </div>
      <DataTable
        :columns="courseColumns"
        :data="studentCourses"
        :loading="courseLoading"
        :current-page="coursePage"
        :total-pages="courseTotalPages"
        empty-text="请先选中学生，再点「查询该生选课」"
        @page-change="loadStudentCourses"
      />
    </div>

    <!-- 新增/修改弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px">
      <el-form :model="formData" label-width="80px" label-position="right">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="formData.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号">
              <el-input v-model="formData.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="formData.gender" style="width:100%">
                <el-option :value="0" label="未知" />
                <el-option :value="1" label="男" />
                <el-option :value="2" label="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级">
              <el-select v-model="formData.grade" style="width:100%">
                <el-option value="" label="请选择" />
                <el-option value="大一" label="大一" />
                <el-option value="大二" label="大二" />
                <el-option value="大三" label="大三" />
                <el-option value="大四" label="大四" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="生日">
              <el-input v-model="formData.birthday" placeholder="如 2002-05-15" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级ID">
              <el-input v-model="formData.classId" placeholder="请输入班级ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="formData.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="入学年份">
              <el-input v-model="formData.enrollmentYear" placeholder="如 2024" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="formData.studentStatus" style="width:100%">
                <el-option :value="1" label="在读" />
                <el-option :value="2" label="休学" />
                <el-option :value="3" label="毕业" />
                <el-option :value="0" label="退学" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '新增' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card {
  background: var(--surface);
  border-radius: var(--radius);
  padding: 20px 22px;
  margin-bottom: 14px;
  box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04);
  border: 1px solid var(--border);
}
.card-title {
  font-size: 14px;
  font-weight: 620;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.card-header .card-title { margin-bottom: 0; }
.search-row {
  display: flex;
  gap: 9px;
  flex-wrap: wrap;
  align-items: center;
}
.selected-info { font-size: 13px; color: var(--accent); font-weight: 500; }
.hint { font-size: 12px; color: var(--text3); font-weight: 400; margin-left: 4px; }
</style>
