<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { sendToKafka, checkKafkaStatus } from '@/api/kafka'

const auth = useAuthStore()

const formData = reactive({ studentName: '', birthday: '', grade: '', email: '', phone: '' })
const kafkaResult = ref('')
const loading = ref(false)

async function handleSend() {
  if (!formData.studentName) { ElMessage.warning('请填写学生姓名'); return }
  if (!formData.grade) { ElMessage.warning('请填写年级'); return }
  loading.value = true
  try {
    const res = await sendToKafka({
      studentName: formData.studentName,
      birthday: formData.birthday || null,
      grade: formData.grade,
      email: formData.email || undefined,
      phone: formData.phone || undefined
    })
    kafkaResult.value = JSON.stringify(res.data, null, 2)
    ElMessage[res.data.code === 200 ? 'success' : 'error'](res.data.message || '发送完成')
  } catch { /* handled */ }
  finally { loading.value = false }
}

async function handleCheckStatus() {
  try {
    const res = await checkKafkaStatus()
    kafkaResult.value = JSON.stringify(res.data, null, 2)
    ElMessage.info('状态查询完成')
  } catch { /* handled */ }
}
</script>

<template>
  <div class="page" v-if="auth.isAdmin">
    <div class="card">
      <div class="card-title">📨 Kafka 消息 <span class="hint">管理员专用 / 模拟外部系统推送学生数据到 Kafka</span></div>
      <div class="search-row" style="margin-top:12px">
        <el-input v-model="formData.studentName" placeholder="学生姓名 *" size="small" style="width:130px" />
        <el-input v-model="formData.birthday" placeholder="生日 (如 2002-05-15)" size="small" style="width:160px" />
        <el-input v-model="formData.grade" placeholder="年级 *" size="small" style="width:110px" />
        <el-input v-model="formData.email" placeholder="邮箱" size="small" style="width:160px" />
        <el-input v-model="formData.phone" placeholder="电话" size="small" style="width:140px" />
        <el-button type="primary" size="small" :loading="loading" @click="handleSend">发送到Kafka</el-button>
        <el-button size="small" @click="handleCheckStatus">Kafka状态</el-button>
      </div>
      <div v-if="kafkaResult" class="result-box" style="margin-top:12px">{{ kafkaResult }}</div>
    </div>
  </div>
  <div class="page" v-else>
    <div class="card"><div class="card-title">📨 Kafka 消息</div><p style="color:var(--text2)">此功能仅管理员可用</p></div>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.search-row { display: flex; gap: 9px; flex-wrap: wrap; align-items: center; }
.hint { font-size: 12px; color: var(--text3); font-weight: 400; margin-left: 4px; }
.result-box { background: #f9f9fb; padding: 12px; border-radius: 7px; font-size: 12px; max-height: 240px; overflow: auto; white-space: pre-wrap; word-break: break-all; border: 1px solid var(--border); font-family: monospace; }
</style>
