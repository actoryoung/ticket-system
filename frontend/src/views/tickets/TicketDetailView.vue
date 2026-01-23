<template>
  <div class="ticket-detail-container" v-loading="loading">
    <div v-if="ticket">
      <!-- 工单头部 -->
      <el-card class="mb-4">
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <el-tag>{{ getCategoryLabel(ticket.category) }}</el-tag>
              <el-tag :type="getPriorityType(ticket.priority)">
                {{ getPriorityLabel(ticket.priority) }}
              </el-tag>
              <el-tag :type="getStatusType(ticket.status)">
                {{ getStatusLabel(ticket.status) }}
              </el-tag>
            </div>
            <h2 class="text-2xl font-bold text-gray-800">{{ ticket.title }}</h2>
            <div class="text-gray-600 mt-2">
              <span class="mr-4">工单编号: {{ ticket.ticketNo }}</span>
              <span>创建时间: {{ ticket.createTime }}</span>
            </div>
          </div>
          <div class="flex gap-2">
            <el-button
              v-if="canEdit"
              type="warning"
              @click="handleEdit"
            >
              编辑
            </el-button>
            <el-button
              v-if="canAssign"
              type="primary"
              @click="showAssignDialog = true"
            >
              分配工单
            </el-button>
            <el-button
              v-if="canUpdateStatus"
              type="success"
              @click="showStatusDialog = true"
            >
              更新状态
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 工单信息 -->
      <el-row :gutter="20" class="mb-4">
        <el-col :span="16">
          <!-- 详细描述 -->
          <el-card class="mb-4">
            <template #header>
              <span class="font-bold">详细描述</span>
            </template>
            <div class="whitespace-pre-wrap">{{ ticket.description || '暂无描述' }}</div>
          </el-card>

          <!-- 附件 -->
          <el-card v-if="ticket.attachmentUrls && ticket.attachmentUrls.length > 0" class="mb-4">
            <template #header>
              <span class="font-bold">附件</span>
            </template>
            <div class="flex flex-wrap gap-2">
              <el-tag
                v-for="(url, index) in ticket.attachmentUrls"
                :key="index"
                closable
                @click="openAttachment(url)"
              >
                <a :href="url" target="_blank" class="text-blue-600 hover:underline">
                  附件{{ index + 1 }}
                </a>
              </el-tag>
            </div>
          </el-card>

          <!-- 评论区 -->
          <el-card>
            <template #header>
              <div class="flex justify-between items-center">
                <span class="font-bold">评论 ({{ comments.length }})</span>
              </div>
            </template>

            <!-- 添加评论 -->
            <div class="mb-4">
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="3"
                placeholder="输入评论内容..."
              />
              <div class="mt-2 flex justify-between items-center">
                <el-checkbox v-model="isInternalComment">内部评论</el-checkbox>
                <el-button type="primary" @click="handleAddComment">
                  发表评论
                </el-button>
              </div>
            </div>

            <!-- 评论列表 -->
            <div class="comment-list">
              <div
                v-for="comment in comments"
                :key="comment.id"
                class="border-b py-4 last:border-b-0"
              >
                <div class="flex items-start gap-3">
                  <el-avatar>{{ comment.userName?.charAt(0) }}</el-avatar>
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="font-semibold">{{ comment.userName }}</span>
                      <el-tag v-if="comment.isInternal" size="small" type="warning">内部</el-tag>
                      <span class="text-gray-500 text-sm">{{ comment.createTime }}</span>
                    </div>
                    <div class="text-gray-700">{{ comment.content }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <!-- 基本信息 -->
          <el-card class="mb-4">
            <template #header>
              <span class="font-bold">基本信息</span>
            </template>
            <div class="space-y-3">
              <div class="flex justify-between">
                <span class="text-gray-600">创建人:</span>
                <span>{{ ticket.creatorName }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">处理人:</span>
                <span>{{ ticket.handlerName || '-' }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">状态:</span>
                <el-tag :type="getStatusType(ticket.status)" size="small">
                  {{ getStatusLabel(ticket.status) }}
                </el-tag>
              </div>
              <div v-if="ticket.resolution" class="mt-4">
                <div class="text-gray-600 mb-2">解决方案:</div>
                <div class="text-gray-800">{{ ticket.resolution }}</div>
              </div>
            </div>
          </el-card>

          <!-- 状态历史 -->
          <el-card>
            <template #header>
              <span class="font-bold">状态历史</span>
            </template>
            <el-timeline>
              <el-timeline-item
                v-for="item in history"
                :key="item.id"
                :timestamp="item.createTime"
                placement="top"
              >
                <div>
                  <div class="font-semibold">{{ item.operatorName }}</div>
                  <div class="text-sm text-gray-600">
                    {{ getStatusLabel(item.oldStatus) }} →
                    {{ getStatusLabel(item.newStatus) }}
                  </div>
                  <div v-if="item.remark" class="text-sm text-gray-500 mt-1">
                    {{ item.remark }}
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分配工单对话框 -->
    <el-dialog v-model="showAssignDialog" title="分配工单" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.handlerId" placeholder="请选择处理人">
            <el-option
              v-for="handler in handlers"
              :key="handler.id"
              :label="handler.realName"
              :value="handler.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 更新状态对话框 -->
    <el-dialog v-model="showStatusDialog" title="更新状态" width="500px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态">
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input v-model="statusForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStatusDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getTicketDetail,
  getTicketComments,
  getTicketHistory,
  addComment,
  assignTicket,
  updateTicketStatus
} from '@/api/ticket'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const ticket = ref(null)
const comments = ref([])
const history = ref([])
const newComment = ref('')
const isInternalComment = ref(false)
const showAssignDialog = ref(false)
const showStatusDialog = ref(false)

const assignForm = reactive({
  handlerId: null
})

const statusForm = reactive({
  status: '',
  remark: ''
})

// 模拟处理人员列表
const handlers = ref([
  { id: 2, realName: '张处理' },
  { id: 3, realName: '李工程师' }
])

// 权限判断
const canEdit = computed(() => {
  return ticket.value?.creatorId === userStore.user?.id && ticket.value?.status === 'PENDING'
})

const canAssign = computed(() => {
  return userStore.isHandler && ticket.value?.status === 'PENDING'
})

const canUpdateStatus = computed(() => {
  return userStore.isHandler && ticket.value?.status !== 'CLOSED'
})

// 获取分类标签
const getCategoryLabel = (category) => {
  const labels = { BUG: '缺陷', FEATURE: '功能需求', SUPPORT: '技术支持', OTHER: '其他' }
  return labels[category] || category
}

// 获取优先级类型和标签
const getPriorityType = (priority) => {
  const types = { URGENT: 'danger', HIGH: 'warning', MEDIUM: '', LOW: 'info' }
  return types[priority] || ''
}

const getPriorityLabel = (priority) => {
  const labels = { URGENT: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低' }
  return labels[priority] || priority
}

// 获取状态类型和标签
const getStatusType = (status) => {
  const types = { PENDING: 'info', PROCESSING: 'warning', RESOLVED: 'success', CLOSED: '' }
  return types[status] || ''
}

const getStatusLabel = (status) => {
  const labels = { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已完成', CLOSED: '已关闭' }
  return labels[status] || status
}

// 加载工单详情
const loadTicketDetail = async () => {
  loading.value = true
  try {
    const ticketId = route.params.id
    const [ticketRes, commentsRes, historyRes] = await Promise.all([
      getTicketDetail(ticketId),
      getTicketComments(ticketId),
      getTicketHistory(ticketId)
    ])

    if (ticketRes.code === 200) {
      ticket.value = ticketRes.data
    }
    if (commentsRes.code === 200) {
      comments.value = commentsRes.data
    }
    if (historyRes.code === 200) {
      history.value = historyRes.data
    }
  } catch (error) {
    ElMessage.error('加载工单详情失败')
  } finally {
    loading.value = false
  }
}

// 添加评论
const handleAddComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    const ticketId = route.params.id
    const response = await addComment(ticketId, {
      content: newComment.value,
      isInternal: isInternalComment.value ? 1 : 0
    })

    if (response.code === 200) {
      ElMessage.success('评论添加成功')
      newComment.value = ''
      isInternalComment.value = false
      // 重新加载评论
      const commentsRes = await getTicketComments(ticketId)
      if (commentsRes.code === 200) {
        comments.value = commentsRes.data
      }
    }
  } catch (error) {
    ElMessage.error('评论添加失败')
  }
}

// 分配工单
const handleAssign = async () => {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择处理人')
    return
  }

  try {
    const ticketId = route.params.id
    const response = await assignTicket(ticketId, assignForm.handlerId)

    if (response.code === 200) {
      ElMessage.success('工单分配成功')
      showAssignDialog.value = false
      loadTicketDetail()
    }
  } catch (error) {
    ElMessage.error('工单分配失败')
  }
}

// 更新状态
const handleUpdateStatus = async () => {
  if (!statusForm.status) {
    ElMessage.warning('请选择新状态')
    return
  }

  try {
    const ticketId = route.params.id
    const response = await updateTicketStatus(ticketId, statusForm.status, statusForm.remark)

    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      showStatusDialog.value = false
      loadTicketDetail()
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 打开附件
const openAttachment = (url) => {
  window.open(url, '_blank')
}

// 编辑工单
const handleEdit = () => {
  router.push(`/tickets/${ticket.value.id}/edit`)
}

onMounted(() => {
  loadTicketDetail()
})
</script>

<style scoped>
.ticket-detail-container {
  padding: 20px;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
}
</style>
