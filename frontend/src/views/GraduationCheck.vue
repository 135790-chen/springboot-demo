<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { checkGraduation, getGraduationHistory } from '@/api/graduation'
import type { GraduationResult, CreditDetail } from '@/types/models'

const studentId = ref('')
const checking = ref(false)
const result = ref<GraduationResult | null>(null)

const historyStudentId = ref('')
const historyData = ref<any[]>([])
const historyLoading = ref(false)

async function doCheck() {
  const id = parseInt(studentId.value)
  if (!id) { ElMessage.warning('请输入学生ID'); return }
  checking.value = true
  result.value = null
  try {
    const res = await checkGraduation(id)
    if (res.data.code === 200) {
      result.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '审核失败')
    }
  } catch { /* handled */ }
  finally { checking.value = false }
}

async function loadHistory() {
  const id = parseInt(historyStudentId.value)
  if (!id) { ElMessage.warning('请输入学生ID'); return }
  historyLoading.value = true
  try {
    const res = await getGraduationHistory(id)
    if (res.data.code === 200) {
      historyData.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || '查询失败')
    }
  } catch { /* handled */ }
  finally { historyLoading.value = false }
}
</script>

<template>
  <div class="page">
    <!-- 毕业审核 -->
    <div class="card">
      <div class="card-title">🎓 毕业资格审核</div>
      <div class="search-row" style="margin-top:12px">
        <el-input v-model="studentId" placeholder="学生ID *" size="small" style="width:180px" @keyup.enter="doCheck" />
        <el-button type="primary" size="small" :loading="checking" @click="doCheck">🔍 执行审核</el-button>
      </div>
    </div>

    <!-- 审核结果 -->
    <div class="card" v-if="result">
      <div class="card-header">
        <div class="card-title">
          <span v-if="result.passed" style="color:#2ea87a">✅ 审核通过 — 符合毕业条件</span>
          <span v-else style="color:#e5484d">❌ 审核未通过 — 不满足毕业条件</span>
        </div>
      </div>
      <div class="info-grid" style="margin-top:12px">
        <div class="info-item"><span class="info-label">学生</span><span>{{ result.studentName }} ({{ result.studentNo }})</span></div>
        <div class="info-item"><span class="info-label">学院</span><span>{{ result.collegeName || '-' }}</span></div>
        <div class="info-item"><span class="info-label">专业</span><span>{{ result.majorName || '-' }}</span></div>
        <div class="info-item"><span class="info-label">年级</span><span>{{ result.grade || '-' }}</span></div>
        <div class="info-item"><span class="info-label">培养方案</span><span>{{ result.planName || '-' }}</span></div>
        <div class="info-item"><span class="info-label">已修总学分</span><span style="font-weight:700;font-size:16px">{{ result.totalEarnedCredits ?? 0 }}</span></div>
        <div class="info-item"><span class="info-label">方案最大学分</span><span style="font-weight:700;font-size:16px">{{ result.totalMaxCredits ?? 0 }}</span></div>
      </div>
      <!-- 学分明细 -->
      <div style="margin-top:16px" v-if="result.creditDetails?.length">
        <div class="card-title" style="margin-bottom:8px">📊 学分明细</div>
        <el-table :data="result.creditDetails" border stripe size="small">
          <el-table-column prop="categoryName" label="类别" />
          <el-table-column prop="requiredCredits" label="要求学分" />
          <el-table-column prop="earnedCredits" label="已修学分" />
          <el-table-column prop="maxCredits" label="最大学分" />
          <el-table-column prop="gap" label="差距">
            <template #default="{ row }: { row: CreditDetail }">
              <span :style="{ color: row.satisfied ? '#2ea87a' : '#e5484d', fontWeight: 600 }">
                {{ row.satisfied ? '✓ 满足' : '✗ 差 ' + row.gap + ' 学分' }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 缺失项 -->
      <div style="margin-top:16px" v-if="result.missingItems?.length">
        <div class="card-title" style="margin-bottom:8px;color:#e5484d">⚠️ 未满足项</div>
        <ul style="padding-left:20px;color:var(--text2);font-size:13px">
          <li v-for="(item, i) in result.missingItems" :key="i">{{ item }}</li>
        </ul>
      </div>
    </div>

    <!-- 历史审核记录 -->
    <div class="card">
      <div class="card-title">📜 历史审核记录</div>
      <div class="search-row" style="margin-top:12px">
        <el-input v-model="historyStudentId" placeholder="学生ID" size="small" style="width:180px" />
        <el-button type="primary" size="small" :loading="historyLoading" @click="loadHistory">查询</el-button>
      </div>
      <div style="margin-top:12px" v-if="historyData.length">
        <el-table :data="historyData" border stripe size="small" max-height="300">
          <el-table-column prop="id" label="编号" width="80" />
          <el-table-column prop="studentId" label="学生ID" />
          <el-table-column prop="result" label="结果">
            <template #default="{ row }">
              <span :style="{ color: row.passed ? '#2ea87a' : '#e5484d', fontWeight: 600 }">
                {{ row.passed ? '通过' : '未通过' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="totalEarnedCredits" label="已修学分" />
          <el-table-column prop="reviewTime" label="审核时间" />
        </el-table>
      </div>
      <div v-else-if="!historyLoading && historyStudentId" style="margin-top:12px;color:var(--text3)">暂无历史记录</div>
    </div>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.card-header .card-title { margin-bottom: 0; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
.info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 8px; }
.info-item { display: flex; gap: 8px; font-size: 13px; }
.info-label { color: var(--text3); min-width: 60px; }
</style>
