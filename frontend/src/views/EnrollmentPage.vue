<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import DataTable from '@/components/DataTable.vue'
import { getSeckillList, doSeckill, doEnroll, doDrop, doScore } from '@/api/enrollment'
import { getStudentCourses } from '@/api/student'
import type { SeckillCourse, Enrollment } from '@/types/models'

const auth = useAuthStore()
const app = useAppStore()

// ---- 当前操作的学生 ----
const opStudentId = ref<number | null>(null)
const opStudentName = ref('')

// 从 JWT 或跨页面状态初始化
function initStudent() {
  if (!auth.isAdmin && auth.userInfo?.studentId) {
    opStudentId.value = auth.userInfo.studentId
    opStudentName.value = auth.userInfo.username || '当前用户'
  } else if (app.selectedStudentId) {
    opStudentId.value = app.selectedStudentId
    opStudentName.value = app.selectedStudentName
  }
}

// ---- 秒杀课程列表 ----
const seckillList = ref<SeckillCourse[]>([])
const seckillLoading = ref(false)
const selectedCourse = ref<SeckillCourse | null>(null)
const seckillBtnLoading = ref(false)
const canSeckill = computed(() => opStudentId.value != null && selectedCourse.value != null)

async function loadSeckillList() {
  seckillLoading.value = true
  try {
    const res = await getSeckillList()
    if (res.data.code === 200) {
      seckillList.value = res.data.data || []
    }
  } catch { /* handled */ }
  finally { seckillLoading.value = false }
}

function handleCourseClick(row: SeckillCourse) {
  selectedCourse.value = row
}

async function handleSeckill() {
  if (!opStudentId.value) { ElMessage.warning('请先指定操作的学生'); return }
  if (!selectedCourse.value) { ElMessage.warning('请先点击上方课程行选中一门课'); return }
  seckillBtnLoading.value = true
  try {
    const res = await doSeckill(selectedCourse.value.courseId)
    if (res.data.code === 200) {
      ElMessage.success('抢课成功！')
      loadSeckillList()
    } else {
      ElMessage.error(res.data.message || '抢课失败')
    }
  } catch { /* handled */ }
  finally { seckillBtnLoading.value = false }
}

// ---- 管理员操作 ----
const enrollForm = reactive({ studentId: '', courseId: '' })
const scoreForm = reactive({ relId: '', score: '' })
const dropRelId = ref('')
const enrollLoading = ref(false)
const scoreLoading = ref(false)
const dropLoading = ref(false)

async function handleEnroll() {
  const sid = parseInt(enrollForm.studentId)
  const cid = parseInt(enrollForm.courseId)
  if (!sid || !cid) { ElMessage.warning('请填写学生ID和课程ID'); return }
  enrollLoading.value = true
  try {
    const res = await doEnroll(sid, cid)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || '选课完成')
  } catch { /* handled */ }
  finally { enrollLoading.value = false }
}

async function handleScore() {
  const rid = parseInt(scoreForm.relId)
  const sc = parseFloat(scoreForm.score)
  if (!rid) { ElMessage.warning('请填写选课记录ID'); return }
  if (isNaN(sc)) { ElMessage.warning('请填写成绩'); return }
  scoreLoading.value = true
  try {
    const res = await doScore(rid, sc)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || '成绩录入完成')
  } catch { /* handled */ }
  finally { scoreLoading.value = false }
}

async function handleDrop() {
  const rid = parseInt(dropRelId.value)
  if (!rid) { ElMessage.warning('请填写选课记录ID'); return }
  dropLoading.value = true
  try {
    const res = await doDrop(rid)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || '退课完成')
  } catch { /* handled */ }
  finally { dropLoading.value = false }
}

// ---- 选课查询 ----
const lookupStudentId = ref('')
const enrollmentColumns = [
  { prop: 'relId', label: 'relId' },
  { prop: 'courseCode', label: '课程编码' },
  { prop: 'courseName', label: '课程名称' },
  { prop: 'credit', label: '学分' },
  { prop: 'courseType', label: '类型', formatter: (r: Enrollment) => r.courseType === 'required' ? '必修' : '选修' },
  { prop: 'teacherName', label: '教师' },
  { prop: 'semester', label: '学期' },
  { prop: 'score', label: '成绩', formatter: (r: Enrollment) => r.score != null ? String(r.score) : '-' },
  { prop: 'relStatus', label: '状态', formatter: (r: Enrollment) => r.relStatus === 1 ? '在读' : r.relStatus === 2 ? '已修完' : r.relStatus === 3 ? '退课' : '' }
]
const enrollmentData = ref<Enrollment[]>([])
const enrollmentLoading = ref(false)
const enrollmentPage = ref(1)
const enrollmentTotalPages = ref(0)

async function loadEnrollments(page: number = 1) {
  const sid = parseInt(lookupStudentId.value)
  if (!sid) { ElMessage.warning('请输入学生ID'); return }
  enrollmentLoading.value = true
  try {
    const res = await getStudentCourses(sid, { page, size: 10 })
    if (res.data.code === 200 && res.data.data) {
      enrollmentData.value = res.data.data.records
      enrollmentTotalPages.value = Math.ceil(res.data.data.total / 10)
      enrollmentPage.value = page
      if (res.data.data.total === 0) {
        ElMessage.info('该学生暂无选课记录')
      }
    } else if (res.data.code === 401) {
      ElMessage.warning('登录已过期，请重新登录')
    } else {
      ElMessage.error(res.data.message || '查询选课失败')
    }
  } catch {
    ElMessage.error('查询选课失败，请检查网络或稍后重试')
  } finally { enrollmentLoading.value = false }
}

// ---- 跨页面联动：从学生管理同步 ----
function syncFromAppStore() {
  if (app.selectedStudentId) {
    opStudentId.value = app.selectedStudentId
    opStudentName.value = app.selectedStudentName
    lookupStudentId.value = String(app.selectedStudentId)
  }
}

onMounted(() => {
  initStudent()
  syncFromAppStore()
  loadSeckillList()
})
</script>

<template>
  <div class="page">
    <!-- 当前操作学生信息 -->
    <div class="card">
      <div class="card-title">⚡ 秒杀抢课 <span class="hint">Redis Lua 原子扣库存，防超卖</span></div>
      <div style="margin-top:12px">
        <div class="selected-info" v-if="opStudentId">
          当前学生: {{ opStudentName }} (ID: {{ opStudentId }})
          <span v-if="!auth.isAdmin && auth.userInfo?.studentId" style="color:var(--text3)"> — 自动从登录信息获取</span>
          <span v-else-if="app.selectedStudentId" style="color:var(--text3)"> — 从学生管理页同步</span>
        </div>
        <div style="color:var(--text3);font-size:13px" v-else>
          <span v-if="auth.isAdmin">请先前往「学生管理」点击学生行，再回到此页；或直接使用下方管理员选课功能</span>
          <span v-else>正在加载学生信息...</span>
        </div>
      </div>
      <div class="search-row" style="margin-top:12px">
        <el-button type="danger" size="small" :loading="seckillBtnLoading" :disabled="!canSeckill" @click="handleSeckill">
          ⚡ {{ canSeckill ? '抢课' : '请先选中学生和课程' }}
        </el-button>
        <el-button type="primary" size="small" @click="loadSeckillList">刷新课程</el-button>
        <el-button size="small" @click="syncFromAppStore" v-if="auth.isAdmin">同步学生管理选中</el-button>
        <span class="hint" v-if="!selectedCourse">点击下方课程行选中，再点抢课</span>
      </div>
      <div style="margin-top:12px;max-height:300px;overflow:auto">
        <el-table :data="seckillList" v-loading="seckillLoading" highlight-current-row @row-click="handleCourseClick"
          :row-class-name="({ row }: any) => (selectedCourse?.courseId === row.courseId) ? 'selected-row' : ''"
          border stripe size="small" empty-text="点击「刷新课程」加载">
          <el-table-column prop="courseCode" label="课程编码" />
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="credit" label="学分" width="60" />
          <el-table-column prop="courseType" label="类型" width="60">
            <template #default="{ row }">{{ row.courseType === 'required' ? '必修' : '选修' }}</template>
          </el-table-column>
          <el-table-column prop="semester" label="学期" />
          <el-table-column prop="maxStudents" label="容量" width="60" />
          <el-table-column prop="remaining" label="剩余" width="80">
            <template #default="{ row }">
              <span :style="{ color: row.remaining === 0 ? '#e5484d' : '#2ea87a', fontWeight: 600 }">
                {{ row.remaining }}{{ row.remaining === 0 ? ' (满)' : '' }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 管理员专区 -->
    <template v-if="auth.isAdmin">
      <!-- 选课操作 -->
      <div class="card">
        <div class="card-title">📝 选课操作 <span class="hint">管理员专用</span></div>
        <div class="search-row" style="margin-top:12px">
          <el-input v-model="enrollForm.studentId" placeholder="学生ID" size="small" style="width:120px" />
          <el-input v-model="enrollForm.courseId" placeholder="课程ID" size="small" style="width:120px" />
          <el-button type="success" size="small" :loading="enrollLoading" @click="handleEnroll">选课</el-button>
        </div>
      </div>

      <!-- 成绩录入 / 退课 -->
      <div class="card">
        <div class="card-title">📝 成绩录入 / 退课 <span class="hint">管理员专用</span></div>
        <div class="search-row" style="margin-top:12px">
          <el-input v-model="scoreForm.relId" placeholder="选课记录ID (relId)" size="small" style="width:160px" />
          <el-input v-model="scoreForm.score" placeholder="成绩" size="small" style="width:100px" />
          <el-button type="success" size="small" :loading="scoreLoading" @click="handleScore">录入成绩</el-button>
        </div>
        <div class="search-row" style="margin-top:8px">
          <el-input v-model="dropRelId" placeholder="选课记录ID (relId)" size="small" style="width:160px" />
          <el-button type="danger" size="small" :loading="dropLoading" @click="handleDrop">退课</el-button>
        </div>
      </div>
    </template>

    <!-- 选课查询 -->
    <div class="card">
      <div class="card-title">📋 学生选课查询</div>
      <div class="search-row" style="margin-top:12px">
        <el-input v-model="lookupStudentId" placeholder="输入学生ID" size="small" style="width:140px" />
        <el-button type="primary" size="small" @click="loadEnrollments(1)">查询选课</el-button>
        <span class="selected-info" v-if="opStudentId">当前学生: {{ opStudentName }} (ID: {{ opStudentId }})</span>
      </div>
      <div style="margin-top:12px">
        <DataTable :columns="enrollmentColumns" :data="enrollmentData" :loading="enrollmentLoading"
          :current-page="enrollmentPage" :total-pages="enrollmentTotalPages"
          empty-text="输入学生ID，点击「查询选课」" @page-change="loadEnrollments" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
.selected-info { font-size: 13px; color: var(--accent); font-weight: 500; }
.hint { font-size: 12px; color: var(--text3); font-weight: 400; margin-left: 4px; }
</style>

<style>
.selected-row { background-color: #eeeffc !important; }
</style>
