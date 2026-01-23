<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-800 mb-6">仪表盘</h2>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="bg-blue-50 rounded-lg p-4 border border-blue-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-blue-600">待处理</p>
            <p class="text-2xl font-bold text-blue-700 mt-1">{{ statistics.pending || 0 }}</p>
          </div>
          <el-icon class="text-3xl text-blue-400"><Clock /></el-icon>
        </div>
      </div>

      <div class="bg-yellow-50 rounded-lg p-4 border border-yellow-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-yellow-600">处理中</p>
            <p class="text-2xl font-bold text-yellow-700 mt-1">{{ statistics.processing || 0 }}</p>
          </div>
          <el-icon class="text-3xl text-yellow-400"><Loading /></el-icon>
        </div>
      </div>

      <div class="bg-green-50 rounded-lg p-4 border border-green-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-green-600">已完成</p>
            <p class="text-2xl font-bold text-green-700 mt-1">{{ statistics.completed || 0 }}</p>
          </div>
          <el-icon class="text-3xl text-green-400"><CircleCheck /></el-icon>
        </div>
      </div>

      <div class="bg-purple-50 rounded-lg p-4 border border-purple-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-purple-600">总计</p>
            <p class="text-2xl font-bold text-purple-700 mt-1">{{ statistics.total || 0 }}</p>
          </div>
          <el-icon class="text-3xl text-purple-400"><Tickets /></el-icon>
        </div>
      </div>
    </div>

    <!-- 最近工单 -->
    <div class="mt-6">
      <h3 class="text-lg font-semibold text-gray-700 mb-4">最近工单</h3>
      <el-table :data="recentTickets" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ getPriorityLabel(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="viewTicket(row.id)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Clock, Loading, CircleCheck, Tickets
} from '@element-plus/icons-vue'
import { getTicketList, getStatistics } from '@/api/ticket'

const router = useRouter()
const statistics = ref({})
const recentTickets = ref([])

const getPriorityType = (priority) => {
  const types = { high: 'danger', medium: 'warning', low: 'info' }
  return types[priority] || 'info'
}

const getPriorityLabel = (priority) => {
  const labels = { high: '高', medium: '中', low: '低' }
  return labels[priority] || priority
}

const getStatusType = (status) => {
  const types = {
    pending: 'info',
    processing: 'warning',
    completed: 'success'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const labels = {
    pending: '待处理',
    processing: '处理中',
    completed: '已完成'
  }
  return labels[status] || status
}

const viewTicket = (id) => {
  router.push(`/tickets/${id}`)
}

const loadDashboard = async () => {
  try {
    // 加载统计数据
    const statsRes = await getStatistics()
    if (statsRes.code === 200) {
      statistics.value = statsRes.data
    }

    // 加载最近工单
    const ticketsRes = await getTicketList({ page: 1, pageSize: 5 })
    if (ticketsRes.code === 200) {
      recentTickets.value = ticketsRes.data.list || []
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

onMounted(() => {
  loadDashboard()
})
</script>
