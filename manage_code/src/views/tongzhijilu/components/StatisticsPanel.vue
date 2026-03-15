<template>
  <div class="statistics-panel">
    <el-row :gutter="20">
      <!-- 总通知数 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total || 0 }}</div>
              <div class="stat-label">总通知数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待发送 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon pending">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待发送</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 发送成功 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.success || 0 }}</div>
              <div class="stat-label">发送成功</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 发送失败 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card" @click="handleFilterByStatus(2)">
          <div class="stat-item clickable">
            <div class="stat-icon failed">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.failed || 0 }}</div>
              <div class="stat-label">发送失败</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 发送中 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon sending">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.sending || 0 }}</div>
              <div class="stat-label">发送中</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 成功率 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon rate">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ successRate }}%</div>
              <div class="stat-label">成功率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>发送状态分布</span>
              <el-button link @click="refreshData" :icon="Refresh">刷新</el-button>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>通知类型分布</span>
              <el-button link @click="refreshData" :icon="Refresh">刷新</el-button>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  Timer,
  CircleCheck,
  CircleClose,
  Loading,
  TrendCharts,
  Refresh
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStatistics } from '@/api/tongzhijilu.js'

const emit = defineEmits(['filterByStatus', 'refresh'])

// 统计数据
const statistics = ref({
  total: 0,
  pending: 0,
  success: 0,
  failed: 0,
  sending: 0
})

// 图表实例
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

// 计算成功率
const successRate = computed(() => {
  const total = statistics.value.total
  const success = statistics.value.success
  if (total === 0) return 0
  return ((success / total) * 100).toFixed(1)
})

/**
 * 获取统计数据
 */
const fetchStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.data.code === 0) {
      const data = res.data.data || []
      
      // 重置统计
      statistics.value = {
        total: 0,
        pending: 0,
        success: 0,
        failed: 0,
        sending: 0
      }
      
      // 解析统计数据
      data.forEach(item => {
        const status = parseInt(item.status)
        const count = parseInt(item.count)
        statistics.value.total += count
        
        switch (status) {
          case 0:
            statistics.value.pending = count
            break
          case 1:
            statistics.value.success = count
            break
          case 2:
            statistics.value.failed = count
            break
          case 3:
            statistics.value.sending = count
            break
        }
      })
      
      // 更新图表
      nextTick(() => {
        updateCharts()
      })
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

/**
 * 更新图表
 */
const updateCharts = () => {
  initPieChart()
  initBarChart()
}

/**
 * 初始化饼图
 */
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  if (pieChart) {
    pieChart.dispose()
  }
  
  pieChart = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['待发送', '发送成功', '发送失败', '发送中']
    },
    series: [
      {
        name: '发送状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: statistics.value.pending, name: '待发送', itemStyle: { color: '#909399' } },
          { value: statistics.value.success, name: '发送成功', itemStyle: { color: '#67C23A' } },
          { value: statistics.value.failed, name: '发送失败', itemStyle: { color: '#F56C6C' } },
          { value: statistics.value.sending, name: '发送中', itemStyle: { color: '#E6A23C' } }
        ]
      }
    ]
  }
  
  pieChart.setOption(option)
}

/**
 * 初始化柱状图
 */
const initBarChart = () => {
  if (!barChartRef.value) return
  
  if (barChart) {
    barChart.dispose()
  }
  
  barChart = echarts.init(barChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['预约成功', '就诊前1天', '就诊前2小时', '就诊前15分钟'],
      axisLabel: {
        interval: 0,
        rotate: 15
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '通知数量',
        type: 'bar',
        barWidth: '60%',
        data: [
          { value: 0, itemStyle: { color: '#67C23A' } },
          { value: 0, itemStyle: { color: '#E6A23C' } },
          { value: 0, itemStyle: { color: '#E6A23C' } },
          { value: 0, itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  
  barChart.setOption(option)
}

/**
 * 按状态筛选
 */
const handleFilterByStatus = (status) => {
  emit('filterByStatus', status)
}

/**
 * 刷新数据
 */
const refreshData = () => {
  fetchStatistics()
  emit('refresh')
}

/**
 * 窗口大小变化处理
 */
const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
}

// 生命周期
onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
})

// 暴露方法
defineExpose({
  fetchStatistics
})
</script>

<style scoped lang="scss">
.statistics-panel {
  margin-bottom: 20px;

  .stat-card {
    margin-bottom: 20px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    .stat-item {
      display: flex;
      align-items: center;
      padding: 10px;

      &.clickable {
        cursor: pointer;
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        margin-right: 15px;

        &.total {
          background-color: #ecf5ff;
          color: #409eff;
        }

        &.pending {
          background-color: #f4f4f5;
          color: #909399;
        }

        &.success {
          background-color: #f0f9eb;
          color: #67c23a;
        }

        &.failed {
          background-color: #fef0f0;
          color: #f56c6c;
        }

        &.sending {
          background-color: #fdf6ec;
          color: #e6a23c;
        }

        &.rate {
          background-color: #f0f9ff;
          color: #409eff;
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .chart-row {
    margin-top: 10px;

    .chart-card {
      margin-bottom: 20px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: bold;
      }

      .chart-container {
        height: 300px;
      }
    }
  }
}

// 响应式适配
@media screen and (max-width: 768px) {
  .statistics-panel {
    .stat-card {
      .stat-item {
        padding: 5px;

        .stat-icon {
          width: 50px;
          height: 50px;
          font-size: 24px;
          margin-right: 10px;
        }

        .stat-info {
          .stat-value {
            font-size: 20px;
          }

          .stat-label {
            font-size: 12px;
          }
        }
      }
    }

    .chart-card {
      .chart-container {
        height: 250px;
      }
    }
  }
}
</style>
