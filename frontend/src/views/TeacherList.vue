<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getTeacherPage, addTeacher, updateTeacher, deleteTeacher } from '@/api/teacher'
import type { Teacher } from '@/types/models'
import { GenderLabels } from '@/types/enums'

const auth = useAuthStore()

const searchForm = reactive({ teacherName: '', teacherNo: '', title: '', teacherStatus: '' })

const columns = [
  { prop: 'teacherNo', label: '工号' },
  { prop: 'teacherName', label: '姓名' },
  { prop: 'gender', label: '性别', formatter: (r: Teacher) => GenderLabels[r.gender ?? 0] },
  { prop: 'title', label: '职称', formatter: (r: Teacher) => r.title || '-' },
  { prop: 'phone', label: '电话', formatter: (r: Teacher) => r.phone || '-' },
  { prop: 'email', label: '邮箱', formatter: (r: Teacher) => r.email || '-' },
  { prop: 'teacherStatus', label: '状态', formatter: (r: Teacher) => r.teacherStatus === 1 ? '在职' : '离职' }
]
const data = ref<Teacher[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<Teacher | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增教师')
const isEdit = ref(false)
const formData = reactive<Partial<Teacher>>({ teacherName: '', teacherNo: '', gender: 0, title: '讲师', phone: '', email: '', teacherStatus: 1, teacherRemark: '' })

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.teacherName) params.teacherName = searchForm.teacherName
    if (searchForm.teacherNo) params.teacherNo = searchForm.teacherNo
    if (searchForm.title) params.title = searchForm.title
    if (searchForm.teacherStatus) params.teacherStatus = searchForm.teacherStatus
    const res = await getTeacherPage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.teacherName = ''; searchForm.teacherNo = ''; searchForm.title = ''; searchForm.teacherStatus = ''; handleSearch() }
function handleRowClick(row: Teacher) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增教师'
  Object.assign(formData, { teacherName: '', teacherNo: '', gender: 0, title: '讲师', phone: '', email: '', teacherStatus: 1, teacherRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先在表格中点击选择教师'); return }
  isEdit.value = true; dialogTitle.value = '修改教师'
  const s = selected.value
  Object.assign(formData, { teacherName: s.teacherName, teacherNo: s.teacherNo || '', gender: s.gender ?? 0, title: s.title || '讲师', phone: s.phone || '', email: s.email || '', teacherStatus: s.teacherStatus ?? 1, teacherRemark: s.teacherRemark || '' })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.teacherName) { ElMessage.warning('请填写教师姓名'); return }
  try {
    const res = isEdit.value ? await updateTeacher({ ...formData, id: selected.value?.id }) : await addTeacher(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中教师'); return }
  try {
    await ElMessageBox.confirm('确定删除该教师吗？', '确认删除', { type: 'warning' })
    const res = await deleteTeacher(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 教师查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.teacherName" placeholder="姓名" size="small" style="width:120px" clearable />
        <el-input v-model="searchForm.teacherNo" placeholder="工号" size="small" style="width:120px" clearable />
        <el-select v-model="searchForm.title" placeholder="全部职称" size="small" style="width:120px" clearable>
          <el-option label="教授" value="教授" /><el-option label="副教授" value="副教授" /><el-option label="讲师" value="讲师" /><el-option label="助教" value="助教" />
        </el-select>
        <el-select v-model="searchForm.teacherStatus" placeholder="全部状态" size="small" style="width:120px" clearable>
          <el-option label="在职" value="1" /><el-option label="离职" value="0" />
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">查全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">👨‍🏫 教师列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.teacherName }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="点击「查询」加载数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="540px">
      <el-form :model="formData" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="formData.teacherName" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="工号"><el-input v-model="formData.teacherNo" placeholder="请输入" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="formData.gender" style="width:100%"><el-option :value="0" label="未知" /><el-option :value="1" label="男" /><el-option :value="2" label="女" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="职称"><el-select v-model="formData.title" style="width:100%"><el-option value="教授" /><el-option value="副教授" /><el-option value="讲师" /><el-option value="助教" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="formData.phone" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="formData.email" placeholder="请输入" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="formData.teacherStatus" style="width:100%"><el-option :value="1" label="在职" /><el-option :value="0" label="离职" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="备注"><el-input v-model="formData.teacherRemark" placeholder="备注" /></el-form-item></el-col>
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
