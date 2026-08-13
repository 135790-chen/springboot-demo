import { ref, reactive } from 'vue'

export function usePagination(loadFn: (page: number) => Promise<void>) {
  const page = ref(1)
  const total = ref(0)
  const pageSize = ref(10)

  const totalPages = () => Math.ceil(total.value / pageSize.value) || 1

  async function goTo(p: number) {
    if (p < 1 || p > totalPages()) return
    page.value = p
    await loadFn(p)
  }

  function reset() {
    page.value = 1
    total.value = 0
  }

  return { page, total, pageSize, totalPages, goTo, reset }
}
