<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import DataTable from '@/components/DataTable.vue'
import { getClassroomPage, addClassroom, updateClassroom, deleteClassroom } from '@/api/classroom'
import type { Classroom } from '@/types/models'

const auth = useAuthStore()

const searchForm = reactive({ keyword: '' })

const columns = [
  { prop: 'id', label: 'ID' },
  { prop: 'classroomName', label: '教室名称' },
  { prop: 'classroomCode', label: '编码' },
  { prop: 'building', label: '楼栋' },
  { prop: 'floor', label: '楼层' },
  { prop: 'capacity', label: '容量' },
  { prop: 'classroomType', label: '类型', formatter: (r: Classroom) => {
    const map: Record<string, string> = { NORMAL: '普通教室', MULTIMEDIA: '多媒体教室', LAB: '实验室', LECTURE_HALL: '报告厅' }
    return map[r.classroomType] || r.classroomType
  }},
  { prop: 'classroomStatus', label: '状态', formatter: (r: Classroom) => r.classroomStatus === 1 ? '可用' : '禁用' }
]
const data = ref<Classroom[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(0)
const selected = ref<Classroom | null>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增教室')
const isEdit = ref(false)
const formData = reactive<Partial<Classroom>>({ classroomName: '', classroomCode: '', capacity: 60, classroomType: 'NORMAL', building: '', floor: 1, classroomStatus: 1, classroomRemark: '' })

async function loadData(page: number = 1) {
  loading.value = true
  try {
    const params: Record<string, string | number | undefined> = { page, size: 10 }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    const res = await getClassroomPage(params)
    if (res.data.code === 200 && res.data.data) {
      data.value = res.data.data.records
      totalPages.value = Math.ceil(res.data.data.total / 10)
      currentPage.value = page
    }
  } finally { loading.value = false }
}

function handleSearch() { selected.value = null; loadData(1) }
function handleReset() { searchForm.keyword = ''; handleSearch() }
function handleRowClick(row: Classroom) { selected.value = row }

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增教室'
  Object.assign(formData, { classroomName: '', classroomCode: '', capacity: 60, classroomType: 'NORMAL', building: '', floor: 1, classroomStatus: 1, classroomRemark: '' })
  dialogVisible.value = true
}
function openEdit() {
  if (!selected.value) { ElMessage.warning('请先选中教室'); return }
  isEdit.value = true; dialogTitle.value = '修改教室'
  const s = selected.value
  Object.assign(formData, {
    classroomName: s.classroomName, classroomCode: s.classroomCode, capacity: s.capacity, classroomType: s.classroomType,
    building: s.building, floor: s.floor, classroomStatus: s.classroomStatus, classroomRemark: s.classroomRemark || ''
  })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!formData.classroomName || !formData.classroomCode) { ElMessage.warning('请填写教室名称和编码'); return }
  try {
    const res = isEdit.value ? await updateClassroom({ ...formData, id: selected.value?.id }) : await addClassroom(formData)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || (isEdit.value ? '修改成功' : '新增成功'))
    if (res.data.code === 200) { dialogVisible.value = false; loadData(currentPage.value) }
  } catch { /* handled */ }
}
async function handleDelete() {
  if (!selected.value?.id) { ElMessage.warning('请先选中教室'); return }
  try {
    await ElMessageBox.confirm('确定删除该教室吗？', '确认删除', { type: 'warning' })
    const res = await deleteClassroom(selected.value.id)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message)
    selected.value = null; loadData(currentPage.value)
  } catch { /* cancelled */ }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">🔍 教室查询</div>
      <div class="search-row">
        <el-input v-model="searchForm.keyword" placeholder="教室名称/编码/楼栋" size="small" style="width:200px" clearable @keyup.enter="handleSearch" />
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button size="small" @click="handleReset">全部</el-button>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">🏫 教室列表</div>
        <div style="display:flex;gap:8px" v-if="auth.isAdmin">
          <span class="selected-info" v-if="selected">已选中: {{ selected.classroomName }} (ID: {{ selected.id }})</span>
          <el-button type="primary" size="small" @click="openAdd">+ 新增</el-button>
          <el-button size="small" @click="openEdit">✎ 修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete">✕ 删除</el-button>
        </div>
      </div>
      <DataTable :columns="columns" :data="data" :loading="loading" :current-page="currentPage" :total-pages="totalPages" :selected-id="selected?.id ?? null" empty-text="暂无教室数据" @row-click="handleRowClick" @page-change="loadData" />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px">
      <el-form :model="formData" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="教室名称"><el-input v-model="formData.classroomName" placeholder="请输入" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="教室编码"><el-input v-model="formData.classroomCode" placeholder="如 BLDG-A-101" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="类型"><el-select v-model="formData.classroomType" style="width:100%"><el-option value="NORMAL" label="普通教室" /><el-option value="MULTIMEDIA" label="多媒体教室" /><el-option value="LAB" label="实验室" /><el-option value="LECTURE_HALL" label="报告厅" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="容量"><el-input-number v-model="formData.capacity" :min="1" :max="500" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="楼栋"><el-input v-model="formData.building" placeholder="如 教学楼A" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="楼层"><el-input-number v-model="formData.floor" :min="1" :max="20" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="formData.classroomRemark" placeholder="备注（选填）" /></el-form-item>
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
