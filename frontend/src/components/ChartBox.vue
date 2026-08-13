<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps<{
  option: any
  height?: string
}>()

const chartRef = ref<HTMLElement | null>(null)
let chartInstance: any = null
let ready = false

async function initChart() {
  if (!chartRef.value) return
  const echarts = await import('echarts')
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(chartRef.value)
  ready = true
  if (props.option) {
    chartInstance.setOption(props.option)
  }
}

function setOption(opt: any) {
  if (!ready || !chartInstance) {
    // 尚未初始化，等 init 完成后自动应用 watch 的 option
    return
  }
  chartInstance.setOption(opt, true)
}

function resize() {
  chartInstance?.resize()
}

// 监听 option 变化，自动更新
watch(() => props.option, (opt) => {
  if (opt && ready && chartInstance) {
    chartInstance.setOption(opt, true)
  }
})

onMounted(() => {
  initChart()
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  window.removeEventListener('resize', resize)
  chartInstance?.dispose()
})

defineExpose({ setOption, resize, initChart })
</script>

<template>
  <div
    ref="chartRef"
    class="chart-box"
    :style="{ height: height || '300px' }"
  ></div>
</template>

<style scoped>
.chart-box {
  width: 100%;
}
</style>
