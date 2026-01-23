<template>
  <div class="ticket-list-container">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">工单列表</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon class="mr-1"><Plus /></el-icon>
        创建工单
      </el-button>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="mb-4">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="搜索工单标题、编号"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="queryForm.priority" placeholder="全部" clearable>
            <el-option label="紧急" value="URGENT" />
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.category" placeholder="全部" clearable>
            <el-option label="缺陷" value="BUG" />
            <el-option label="功能需求" value="FEATURE" />
            <el-option label="技术支持" value="SUPPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon class="mr-1"><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工单列表 -->
    <el-card>
      <el-table :data="ticketList" stripe v-loading="loading">
        <el-table-column prop="ticketNo" label="工单编号" width="150" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">
              {{ getPriorityLabel(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="handlerName" label="处理人" width="100">
          <template #default="{ row }">
            {{ row.handlerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="canEdit(row)"
              type="warning"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="queryForm.current"
          v-model:page-size="queryForm.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getTicketList } from '@/api/ticket'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const ticketList = ref([])
const total = ref(0)

const queryForm = reactive({
  current: 1,
  size: 10,
  keyword: '',
  status: '',
  priority: '',
  category: ''
})

// 判断是否可以编辑工单
const canEdit = (ticket) => {
  return userStore.user?.id === ticket.creatorId && ticket.status === 'PENDING'
}

// 获取分类标签
const getCategoryLabel = (category) => {
  const labels = {
    BUG: '缺陷',
    FEATURE: '功能需求',
    SUPPORT: '技术支持',
    OTHER: '其他'
  }
  return labels[category] || category
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = {
    URGENT: 'danger',
    HIGH: 'warning',
    MEDIUM: '',
    LOW: 'info'
  }
  return types[priority] || ''
}

// 获取优先级标签
const getPriorityLabel = (priority) => {
  const labels = {
    URGENT: '紧急',
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return labels[priority] || priority
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'info',
    PROCESSING: 'warning',
    RESOLVED: 'success',
    CLOSED: ''
  }
  return types[status] || ''
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labels = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    RESOLVED: '已完成',
    CLOSED: '已关闭'
  }
  return labels[status] || status
}

// 加载工单列表
const loadTicketList = async () => {
  loading.value = true
  try {
    const response = await getTicketList(queryForm)
    if (response.code === 200) {
      ticketList.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载工单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryForm.current = 1
  loadTicketList()
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    current: 1,
    size: 10,
    keyword: '',
    status: '',
    priority: '',
    category: ''
  })
  loadTicketList()
}

// 创建工单
const handleCreate = () => {
  router.push('/tickets/create')
}

// 查看工单
const handleView = (row) => {
  router.push(`/tickets/${row.id}`)
}

// 编辑工单
const handleEdit = (row) => {
  router.push(`/tickets/${row.id}/edit`)
}

onMounted(() => {
  loadTicketList()
})
</script>

<style scoped>
.ticket-list-container {
  padding: 20px;
}
</style>
