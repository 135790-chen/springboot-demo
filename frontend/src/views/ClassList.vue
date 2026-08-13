<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getClassPage, addClass, updateClass, deleteClass } from '@/api/class'
import type { ClassInfo } from '@/types/models'

const auth = useAuthStore()

const searchForm = reactive({ className: '', classCode: '', grade: '', classStatus: '' })

const columns = [
  { prop: 'classCode', label: '编码' },
  { prop: 'className', label: '名称' },
  { prop: 'grade', label: '年级' },
  { prop: 'major', label: '专业', formatter: (r: ClassInfo) => r.major || '-' },
  { prop: 'classSort', label: '排序' },
  { prop: 'classStatus', label: '状态', formatter: (r: ClassInfo) => r.classStatus === 1 ? '正常' : '禁用' },
  { prop: 'classRemark', label: '备注', formatter: (r: ClassInfo) => r.classRemark || '-' }
]
const data = ref<ClassInfo[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<ClassInfo | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const isEdit = ref(false)
const formData = reactive<Partial<ClassInfo>>({ className: '', classCode: '', grade: '', major: '', classSort: 0, classStatus: 1, classRemark: '' })

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.className) params.className = searchForm.className
    if (searchForm.classCode) params.classCode = searchForm.classCode
    if (searchForm.grade) params.grade = searchForm.grade
    if (searchForm.classStatus) params.classStatus = searchForm.classStatus
    const res = await getClassPage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.className = ''; searchForm.classCode = ''; searchForm.grade = ''; searchForm.classStatus = ''; handleSearch() }
function handleRowClick(row: ClassInfo) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增班级'
  Object.assign(formData, { className: '', classCode: '', grade: '', major: '', classSort: 0, classStatus: 1, classRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择班级'); return }
  isEdit.value = true; dialogTitle.value = '修改班级'
  const s = selected.value
  Object.assign(formData, { className: s.className, classCode: s.classCode, grade: s.grade || '', major: s.major || '', classSort: s.classSort ?? 0, classStatus: s.classStatus ?? 1, classRemark: s.classRemark || '' })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.className) { ElMessage.warning('请填写班级名称'); return }
  try {
    const res = isEdit.value ? await updateClass({ ...formData, id: selected.value?.id }) : await addClass(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中班级'); return }
  try {
    await ElMessageBox.confirm('确定删除该班级吗？', '确认删除', { type: 'warning' })
    const res = await deleteClass(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 班级查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.className" placeholder="班级名称" size="small" style="width:140px" clearable />
        <el-input v-model="searchForm.classCode" placeholder="班级编码" size="small" style="width:140px" clearable />
        <el-input v-model="searchForm.grade" placeholder="年级" size="small" style="width:110px" clearable />
        <el-select v-model="searchForm.classStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="正常" value="1" /><el-option label="禁用" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">🏫 班级列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.className }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="formData" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="班级名称"><el-input v-model="formData.className" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级编码"><el-input v-model="formData.classCode" placeholder="请输入" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="年级"><el-input v-model="formData.grade" placeholder="如 2024" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="专业"><el-input v-model="formData.major" placeholder="请输入专业" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="formData.classSort" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="formData.classStatus" style="width:100%"><el-option :value="1" label="正常" /><el-option :value="0" label="禁用" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="formData.classRemark" placeholder="备注（选填）" /></el-form-item>
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
