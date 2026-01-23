<template>
  <div class="create-ticket-container">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">创建工单</h2>
      <p class="text-gray-600 mt-1">请填写工单信息，标记为必填项为必填内容</p>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        size="large"
      >
        <el-form-item label="工单标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入工单标题（简明扼要地描述问题）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="工单分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择工单分类">
            <el-option label="缺陷" value="BUG" />
            <el-option label="功能需求" value="FEATURE" />
            <el-option label="技术支持" value="SUPPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio label="LOW">低</el-radio>
            <el-radio label="MEDIUM">中</el-radio>
            <el-radio label="HIGH">高</el-radio>
            <el-radio label="URGENT">紧急</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="8"
            placeholder="请详细描述工单内容，包括：&#10;1. 问题描述&#10;2. 复现步骤（如果是缺陷）&#10;3. 预期结果&#10;4. 实际结果&#10;5. 其他相关信息"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            :limit="5"
            accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx"
            multiple
          >
            <el-button type="primary">
              <el-icon class="mr-1"><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持上传图片、PDF、Word、Excel等文件，单个文件不超过10MB，最多5个文件
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交工单
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { createTicket, uploadFile } from '@/api/ticket'

const router = useRouter()
const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)
const fileList = ref([])

const form = reactive({
  title: '',
  category: '',
  priority: 'MEDIUM',
  description: '',
  attachmentUrls: []
})

const rules = {
  title: [
    { required: true, message: '请输入工单标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择工单分类', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, message: '描述至少需要10个字符', trigger: 'blur' }
  ]
}

// 文件选择变化
const handleFileChange = (file) => {
  // 验证文件大小
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
}

// 文件移除
const handleFileRemove = (file) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

// 上传文件
const uploadFiles = async () => {
  if (fileList.value.length === 0) {
    return []
  }

  const uploadPromises = fileList.value.map(async (file) => {
    try {
      const response = await uploadFile(file.raw)
      if (response.code === 200) {
        return response.data
      }
      return null
    } catch (error) {
      console.error('文件上传失败:', error)
      return null
    }
  })

  const results = await Promise.all(uploadPromises)
  return results.filter(url => url !== null)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 先上传文件
        const uploadedUrls = await uploadFiles()

        // 提交工单
        const ticketData = {
          title: form.title,
          category: form.category,
          priority: form.priority,
          description: form.description,
          attachmentUrls: uploadedUrls
        }

        const response = await createTicket(ticketData)
        if (response.code === 200) {
          ElMessage.success('工单创建成功')
          router.push(`/tickets/${response.data}`)
        }
      } catch (error) {
        ElMessage.error('工单创建失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 取消
const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.create-ticket-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

:deep(.el-textarea__inner) {
  font-family: inherit;
}
</style>
