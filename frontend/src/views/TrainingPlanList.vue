<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getTrainingPlanPage, addTrainingPlan, updateTrainingPlan, deleteTrainingPlan, getPlanCoursesBySemester } from '@/api/trainingPlan'
import { getMajorPage } from '@/api/major'
import type { TrainingPlan, Major } from '@/types/models'

const auth = useAuthStore()

const searchForm = reactive({ majorId: '', grade: '', planStatus: '' })
const majorOptions = ref<Major[]>([])

const columns = [
  { prop: 'id', label: 'ID' },
  { prop: 'planName', label: '方案名称' },
  { prop: 'majorName', label: '专业', formatter: (r: TrainingPlan) => r.majorName || '-' },
  { prop: 'grade', label: '年级' },
  { prop: 'version', label: '版本' },
  { prop: 'totalRequiredCredits', label: '必修学分' },
  { prop: 'majorElectiveMinCredits', label: '专选学分' },
  { prop: 'generalElectiveMinCredits', label: '通识学分' },
  { prop: 'planStatus', label: '状态', formatter: (r: TrainingPlan) => r.planStatus === 1 ? '启用' : '停用' }
]
const data = ref<TrainingPlan[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<TrainingPlan | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增培养方案')
const isEdit = ref(false)
const formData = reactive<Partial<TrainingPlan>>({ planName: '', majorId: undefined, grade: '', version: 1, totalRequiredCredits: 0, majorElectiveMinCredits: 0, generalElectiveMinCredits: 0, planStatus: 1, planRemark: '' })

// 关联课程弹窗（按学期分组）
const courseDialogVisible = ref(false)
const groupedPlanCourses = ref<Record<string, any[]>>({})
const planCoursesTotal = ref(0)

async function loadMajors() {
  try {
    const res = await getMajorPage({ page: 1, size: 9999 })
    if (res.data.code === 200 && res.data.data) majorOptions.value = res.data.data.records
  } catch { /* ignore */ }
}

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.majorId) params.majorId = searchForm.majorId
    if (searchForm.grade) params.grade = searchForm.grade
    if (searchForm.planStatus) params.planStatus = searchForm.planStatus
    const res = await getTrainingPlanPage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.majorId = ''; searchForm.grade = ''; searchForm.planStatus = ''; handleSearch() }
function handleRowClick(row: TrainingPlan) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增培养方案'
  Object.assign(formData, { planName: '', majorId: undefined, grade: '', version: 1, totalRequiredCredits: 0, majorElectiveMinCredits: 0, generalElectiveMinCredits: 0, planStatus: 1, planRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择方案'); return }
  isEdit.value = true; dialogTitle.value = '修改培养方案'
  const s = selected.value
  Object.assign(formData, {
    planName: s.planName, majorId: s.majorId, grade: s.grade || '', version: s.version ?? 1,
    totalRequiredCredits: s.totalRequiredCredits ?? 0, majorElectiveMinCredits: s.majorElectiveMinCredits ?? 0,
    generalElectiveMinCredits: s.generalElectiveMinCredits ?? 0, planStatus: s.planStatus ?? 1, planRemark: s.planRemark || ''
  })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.planName) { ElMessage.warning('请填写方案名称'); return }
  try {
    const res = isEdit.value ? await updateTrainingPlan({ ...formData, id: selected.value?.id }) : await addTrainingPlan(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中方案'); return }
  try {
    await ElMessageBox.confirm('确定删除该方案吗？', '确认删除', { type: 'warning' })
    const res = await deleteTrainingPlan(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

async function viewPlanCourses() {
  if (!selected.value?.id) { ElMessage.warning('请先选中一个培养方案'); return }
  try {
    const res = await getPlanCoursesBySemester(selected.value.id)
    if (res.data.code === 200 && res.data.data) {
      groupedPlanCourses.value = res.data.data.groups || {}
      planCoursesTotal.value = res.data.data.total || 0
      courseDialogVisible.value = true
    } else {
      ElMessage.error(res.data.message || '查询失败')
    }
  } catch { /* handled */ }
}

onMounted(() => { loadMajors(); loadData() })
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 培养方案查询</div>
      <div class="search-row">
        <el-select v-model="searchForm.majorId" placeholder="全部专业" size="small" style="width:180px" clearable>
          <el-option v-for="m in majorOptions" :key="m.id" :label="m.majorName" :value="String(m.id)" />
        </el-select>
        <el-input v-model="searchForm.grade" placeholder="年级 (如 2024)" size="small" style="width:130px" clearable />
        <el-select v-model="searchForm.planStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="启用" value="1" /><el-option label="停用" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">📋 培养方案列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.planName }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
          <el-button size="small" @click="viewPlanCourses">📖 查看课程</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>

    <!-- 新增/修改弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px">
      <el-form :model="formData" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="方案名称"><el-input v-model="formData.planName" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="所属专业"><el-select v-model="formData.majorId" placeholder="请选择专业" style="width:100%"><el-option v-for="m in majorOptions" :key="m.id" :label="m.majorName" :value="m.id" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="年级"><el-input v-model="formData.grade" placeholder="如 2024" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="版本号"><el-input-number v-model="formData.version" :min="1" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="必修最低学分"><el-input-number v-model="formData.totalRequiredCredits" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="专选最低学分"><el-input-number v-model="formData.majorElectiveMinCredits" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="通识最低学分"><el-input-number v-model="formData.generalElectiveMinCredits" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="formData.planStatus" style="width:100%"><el-option :value="1" label="启用" /><el-option :value="0" label="停用" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="formData.planRemark" placeholder="备注（选填）" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '新增' }}</el-button></template>
    </el-dialog>

    <!-- 关联课程弹窗（按学期分组） -->
    <el-dialog v-model="courseDialogVisible" title="📖 方案关联课程（按学期编排）" width="750px">
      <div style="margin-bottom:8px;color:var(--text3);font-size:13px">共 {{ planCoursesTotal }} 门课程</div>
      <el-collapse v-if="Object.keys(groupedPlanCourses).length > 0">
        <el-collapse-item v-for="(courses, semester) in groupedPlanCourses" :key="semester"
          :title="`${semester}（${courses.length} 门）`" :name="semester">
          <el-table :data="courses" border stripe size="small" max-height="300">
            <el-table-column prop="courseCode" label="课程编码" width="110" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="courseCategory" label="类别" width="100">
              <template #default="{ row }">
                {{ row.courseCategory === 'REQUIRED' ? '必修' : row.courseCategory === 'MAJOR_ELECTIVE' ? '专选' : '通识' }}
              </template>
            </el-table-column>
            <el-table-column prop="isRequired" label="必选" width="60">
              <template #default="{ row }">{{ row.isRequired === 1 ? '是' : '否' }}</template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <el-empty v-else description="该方案暂无关联课程" />
      <template #footer><el-button @click="courseDialogVisible = false">关闭</el-button></template>
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
</style>
