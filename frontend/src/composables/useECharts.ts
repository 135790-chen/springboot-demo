import { ref, onMounted, onUnmounted, watch, type Ref } from 'vue'

export function useECharts(domRef: Ref<HTMLElement | null>) {
  const chartInstance = ref<any>(null)

  function initChart() {
    if (!domRef.value) return
    // 按需加载，避免 SSR 报错
    import('echarts').then((echarts) => {
      if (chartInstance.value) chartInstance.value.dispose()
      chartInstance.value = echarts.init(domRef.value!)
    })
  }

  function setOption(option: any) {
    if (!chartInstance.value) {
      // 尚未 init，延迟再试
      setTimeout(() => setOption(option), 100)
      return
    }
    chartInstance.value.setOption(option)
  }

  function resize() {
    chartInstance.value?.resize()
  }

  onMounted(() => {
    initChart()
    window.addEventListener('resize', resize)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', resize)
    chartInstance.value?.dispose()
  })

  return { chartInstance, setOption, resize, initChart }
}
