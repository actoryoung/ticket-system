<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航栏 -->
    <el-header class="bg-white shadow-sm">
      <div class="flex items-center justify-between h-full px-6">
        <div class="flex items-center space-x-4">
          <h1 class="text-xl font-bold text-gray-800">工单管理系统</h1>
        </div>
        <div class="flex items-center space-x-4">
          <el-dropdown @command="handleCommand">
            <span class="flex items-center cursor-pointer">
              <el-icon class="mr-1"><User /></el-icon>
              {{ userStore.user?.username }}
              <el-icon class="ml-1"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container class="mt-4 px-4 pb-4">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="mr-4">
        <el-menu
          :default-active="activeMenu"
          class="bg-white rounded-lg shadow-sm"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/tickets">
            <el-icon><Tickets /></el-icon>
            <span>工单列表</span>
          </el-menu-item>
          <el-menu-item index="/tickets/create">
            <el-icon><Plus /></el-icon>
            <span>创建工单</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <span>统计分析</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="bg-white rounded-lg shadow-sm p-6">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, ArrowDown, Odometer, Tickets, Plus, DataAnalysis
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消
    }
  }
}
</script>
