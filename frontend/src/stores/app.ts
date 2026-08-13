import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 跨页面共享状态（如学生选中联动） */
export const useAppStore = defineStore('app', () => {
  const selectedStudentId = ref<number | null>(null)
  const selectedStudentName = ref('')

  function selectStudent(id: number, name: string) {
    selectedStudentId.value = id
    selectedStudentName.value = name
  }

  function clearSelection() {
    selectedStudentId.value = null
    selectedStudentName.value = ''
  }

  return { selectedStudentId, selectedStudentName, selectStudent, clearSelection }
})
