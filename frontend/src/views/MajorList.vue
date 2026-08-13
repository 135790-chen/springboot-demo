<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getMajorPage, addMajor, updateMajor, deleteMajor } from '@/api/major'
import { getCollegePage } from '@/api/college'
import type { Major, College } from '@/types/models'

const auth = useAuthStore()

const searchForm = reactive({ majorName: '', majorCode: '', collegeId: '', majorStatus: '' })
const collegeOptions = ref<College[]>([])

const columns = [
  { prop: 'id', label: 'ID' },
  { prop: 'majorName', label: '专业名称' },
  { prop: 'majorCode', label: '专业编码' },
  { prop: 'collegeName', label: '所属学院', formatter: (r: Major) => r.collegeName || '-' },
  { prop: 'majorStatus', label: '状态', formatter: (r: Major) => r.majorStatus === 1 ? '正常' : '禁用' },
  { prop: 'majorRemark', label: '备注', formatter: (r: Major) => r.majorRemark || '-' }
]
const data = ref<Major[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<Major | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增专业')
const isEdit = ref(false)
const formData = reactive<Partial<Major>>({ majorName: '', majorCode: '', collegeId: undefined, majorStatus: 1, majorRemark: '' })

async function loadColleges() {
  try {
    const res = await getCollegePage({ page: 1, size: 9999 })
    if (res.data.code === 200 && res.data.data) collegeOptions.value = res.data.data.records
  } catch { /* ignore */ }
}

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.majorName) params.majorName = searchForm.majorName
    if (searchForm.majorCode) params.majorCode = searchForm.majorCode
    if (searchForm.collegeId) params.collegeId = searchForm.collegeId
    if (searchForm.majorStatus) params.majorStatus = searchForm.majorStatus
    const res = await getMajorPage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.majorName = ''; searchForm.majorCode = ''; searchForm.collegeId = ''; searchForm.majorStatus = ''; handleSearch() }
function handleRowClick(row: Major) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增专业'
  Object.assign(formData, { majorName: '', majorCode: '', collegeId: undefined, majorStatus: 1, majorRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择专业'); return }
  isEdit.value = true; dialogTitle.value = '修改专业'
  const s = selected.value
  Object.assign(formData, { majorName: s.majorName, majorCode: s.majorCode, collegeId: s.collegeId, majorStatus: s.majorStatus ?? 1, majorRemark: s.majorRemark || '' })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.majorName) { ElMessage.warning('请填写专业名称'); return }
  try {
    const res = isEdit.value ? await updateMajor({ ...formData, id: selected.value?.id }) : await addMajor(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中专业'); return }
  try {
    await ElMessageBox.confirm('确定删除该专业吗？', '确认删除', { type: 'warning' })
    const res = await deleteMajor(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

onMounted(() => { loadColleges(); loadData() })
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 专业查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.majorName" placeholder="专业名称" size="small" style="width:140px" clearable />
        <el-input v-model="searchForm.majorCode" placeholder="专业编码" size="small" style="width:140px" clearable />
        <el-select v-model="searchForm.collegeId" placeholder="全部学院" size="small" style="width:160px" clearable>
          <el-option v-for="c in collegeOptions" :key="c.id" :label="c.collegeName" :value="String(c.id)" />
        </el-select>
        <el-select v-model="searchForm.majorStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="正常" value="1" /><el-option label="禁用" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">📚 专业列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.majorName }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="专业名称"><el-input v-model="formData.majorName" placeholder="请输入" /></el-form-item>
        <el-form-item label="专业编码"><el-input v-model="formData.majorCode" placeholder="请输入" /></el-form-item>
        <el-form-item label="所属学院">
          <el-select v-model="formData.collegeId" placeholder="请选择学院" style="width:100%">
            <el-option v-for="c in collegeOptions" :key="c.id" :label="c.collegeName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="formData.majorStatus" style="width:100%"><el-option :value="1" label="正常" /><el-option :value="0" label="禁用" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="备注"><el-input v-model="formData.majorRemark" placeholder="备注" /></el-form-item></el-col>
        </el-row>
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
</style>
