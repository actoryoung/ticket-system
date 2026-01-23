<template>
  <div class="statistics-container">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">统计分析</h2>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="6">
        <el-card class="stat-card blue">
          <div class="stat-content">
            <el-icon class="stat-icon"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-label">总工单数</div>
              <div class="stat-value">{{ statistics.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card yellow">
          <div class="stat-content">
            <el-icon class="stat-icon"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-label">待处理</div>
              <div class="stat-value">{{ statistics.pending || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card orange">
          <div class="stat-content">
            <el-icon class="stat-icon"><Loading /></el-icon>
            <div class="stat-info">
              <div class="stat-label">处理中</div>
              <div class="stat-value">{{ statistics.processing || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card green">
          <div class="stat-content">
            <el-icon class="stat-icon"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-label">已完成</div>
              <div class="stat-value">{{ statistics.resolved || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-bold">工单分类统计</span>
          </template>
          <div ref="categoryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-bold">工单优先级统计</span>
          </template>
          <div ref="priorityChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span class="font-bold">工单趋势（最近7天）</span>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  Document, Clock, Loading, CircleCheck
} from '@element-plus/icons-vue'

const categoryChartRef = ref(null)
const priorityChartRef = ref(null)
const trendChartRef = ref(null)

let categoryChart = null
let priorityChart = null
let trendChart = null

// 模拟统计数据
const statistics = ref({
  total: 156,
  pending: 23,
  processing: 45,
  resolved: 88
})

// 初始化分类图表
const initCategoryChart = () => {
  if (!categoryChartRef.value) return

  categoryChart = echarts.init(categoryChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '工单分类',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 48, name: '缺陷' },
          { value: 38, name: '功能需求' },
          { value: 42, name: '技术支持' },
          { value: 28, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  categoryChart.setOption(option)
}

// 初始化优先级图表
const initPriorityChart = () => {
  if (!priorityChartRef.value) return

  priorityChart = echarts.init(priorityChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ['紧急', '高', '中', '低']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '工单数量',
        type: 'bar',
        data: [
          { value: 12, itemStyle: { color: '#f56c6c' } },
          { value: 28, itemStyle: { color: '#e6a23c' } },
          { value: 68, itemStyle: { color: '#409eff' } },
          { value: 48, itemStyle: { color: '#909399' } }
        ],
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }

  priorityChart.setOption(option)
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增', '完成']
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1/17', '1/18', '1/19', '1/20', '1/21', '1/22', '1/23']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增',
        type: 'line',
        data: [12, 18, 15, 22, 16, 25, 20],
        smooth: true,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '完成',
        type: 'line',
        data: [8, 15, 12, 18, 14, 20, 16],
        smooth: true,
        itemStyle: { color: '#67c23a' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  categoryChart?.resize()
  priorityChart?.resize()
  trendChart?.resize()
}

onMounted(() => {
  // 延迟初始化图表，确保DOM已渲染
  setTimeout(() => {
    initCategoryChart()
    initPriorityChart()
    initTrendChart()
  }, 100)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  categoryChart?.dispose()
  priorityChart?.dispose()
  trendChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.stat-card {
  overflow: hidden;
}

.stat-card.blue :deep(.el-card__body) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.yellow :deep(.el-card__body) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.orange :deep(.el-card__body) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-card.green :deep(.el-card__body) {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.stat-content {
  display: flex;
  align-items: center;
  color: white;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
}
</style>
