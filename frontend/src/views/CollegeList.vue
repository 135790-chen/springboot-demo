<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getCollegePage, addCollege, updateCollege, deleteCollege, getCollegeMajors } from '@/api/college'
import type { College, Major } from '@/types/models'

const auth = useAuthStore()

const searchForm = reactive({ collegeName: '', collegeCode: '', collegeStatus: '' })

const columns = [
  { prop: 'id', label: 'ID' },
  { prop: 'collegeName', label: '学院名称' },
  { prop: 'collegeCode', label: '学院编码' },
  { prop: 'collegeStatus', label: '状态', formatter: (r: College) => r.collegeStatus === 1 ? '正常' : '禁用' },
  { prop: 'collegeRemark', label: '备注', formatter: (r: College) => r.collegeRemark || '-' }
]
const data = ref<College[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<College | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增学院')
const isEdit = ref(false)
const formData = reactive<Partial<College>>({ collegeName: '', collegeCode: '', collegeStatus: 1, collegeRemark: '' })

// 查看专业
const majorsDialogVisible = ref(false)
const majorsLoading = ref(false)
const majorsData = ref<Major[]>([])
const majorsColumns = [
  { prop: 'id', label: 'ID' },
  { prop: 'majorName', label: '专业名称' },
  { prop: 'majorCode', label: '专业编码' },
  { prop: 'majorStatus', label: '状态', formatter: (r: Major) => r.majorStatus === 1 ? '正常' : '禁用' },
]

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.collegeName) params.collegeName = searchForm.collegeName
    if (searchForm.collegeCode) params.collegeCode = searchForm.collegeCode
    if (searchForm.collegeStatus) params.collegeStatus = searchForm.collegeStatus
    const res = await getCollegePage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.collegeName = ''; searchForm.collegeCode = ''; searchForm.collegeStatus = ''; handleSearch() }
function handleRowClick(row: College) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增学院'
  Object.assign(formData, { collegeName: '', collegeCode: '', collegeStatus: 1, collegeRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择学院'); return }
  isEdit.value = true; dialogTitle.value = '修改学院'
  const s = selected.value
  Object.assign(formData, { collegeName: s.collegeName, collegeCode: s.collegeCode, collegeStatus: s.collegeStatus ?? 1, collegeRemark: s.collegeRemark || '' })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.collegeName) { ElMessage.warning('请填写学院名称'); return }
  try {
    const res = isEdit.value ? await updateCollege({ ...formData, id: selected.value?.id }) : await addCollege(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled by interceptor */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中学院'); return }
  try {
    await ElMessageBox.confirm('确定删除该学院吗？', '确认删除', { type: 'warning' })
    const res = await deleteCollege(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

async function openMajors() {
  if (!selected.value?.id) { ElMessage.warning('请先在表格中点击选择学院'); return }
  majorsLoading.value = true
  majorsDialogVisible.value = true
  try {
    const res = await getCollegeMajors(selected.value.id)
    if (res.data.code === 200) {
      majorsData.value = res.data.data || []
    }
  } catch { /* handled */ }
  finally { majorsLoading.value = false }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 学院查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.collegeName" placeholder="学院名称" size="small" style="width:150px" clearable />
        <el-input v-model="searchForm.collegeCode" placeholder="学院编码" size="small" style="width:150px" clearable />
        <el-select v-model="searchForm.collegeStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="正常" value="1" /><el-option label="禁用" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">🏛️ 学院列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.collegeName }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="success" size="small" @click="openMajors">📋 查看专业</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="学院名称"><el-input v-model="formData.collegeName" placeholder="请输入学院名称" /></el-form-item>
        <el-form-item label="学院编码"><el-input v-model="formData.collegeCode" placeholder="请输入学院编码" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="formData.collegeStatus" style="width:100%"><el-option :value="1" label="正常" /><el-option :value="0" label="禁用" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="formData.collegeRemark" placeholder="备注（选填）" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '新增' }}</el-button></template>
    </el-dialog>

    <!-- 查看专业弹窗 -->
    <el-dialog v-model="majorsDialogVisible" :title="`${selected?.collegeName || ''} - 专业列表`" width="600px">
      <el-table :data="majorsData" v-loading="majorsLoading" border stripe size="small" empty-text="该学院暂无专业">
        <el-table-column v-for="col in majorsColumns" :key="col.prop" :prop="col.prop" :label="col.label">
          <template #default="{ row }" v-if="col.formatter">
            {{ col.formatter(row) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { /* container */ }
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.card-header .card-title { margin-bottom: 0; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
.selected-info { font-size: 13px; color: var(--accent); font-weight: 500; }
</style>
