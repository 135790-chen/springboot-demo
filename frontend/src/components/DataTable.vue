<script setup lang="ts">
import { ElMessage } from 'element-plus'

interface Column {
  prop: string
  label: string
  width?: string | number
  formatter?: (row: any) => string
}

const props = defineProps<{
  columns: Column[]
  data: any[]
  loading?: boolean
  emptyText?: string
  currentPage: number
  totalPages: number
  selectedId?: number | null
}>()

const emit = defineEmits<{
  'row-click': [row: any]
  'page-change': [page: number]
}>()

function handleRowClick(row: any) {
  emit('row-click', row)
}

function isSelected(row: any) {
  // 通用 ID 检测
  return props.selectedId != null && (
    row.id === props.selectedId ||
    row.studentId === props.selectedId ||
    row.courseId === props.selectedId
  )
}
</script>

<template>
  <div class="data-table-wrapper">
    <el-table
      :data="data"
      v-loading="loading"
      :empty-text="emptyText || '点击查询加载数据'"
      highlight-current-row
      @row-click="handleRowClick"
      :row-class-name="({ row }: any) => isSelected(row) ? 'selected-row' : ''"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column
        v-for="col in columns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :formatter="col.formatter ? (_r: any, _c: any, v: any) => col.formatter!(_r) : undefined"
      />
    </el-table>

    <div class="pagination-bar" v-if="totalPages > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalPages * 10"
        :page-size="10"
        :current-page="currentPage"
        @current-change="(p: number) => emit('page-change', p)"
      />
    </div>
  </div>
</template>

<style scoped>
.data-table-wrapper {
  width: 100%;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

:deep(.selected-row) {
  background-color: #eeeffc !important;
}

:deep(.el-table__header th) {
  background: #fafafc;
  font-weight: 600;
  color: var(--text3);
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}
</style>
