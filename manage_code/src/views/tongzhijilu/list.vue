<template>
  <div class="tongzhijilu-container">
    <!-- 统计面板 -->
    <StatisticsPanel ref="statisticsRef" @filterByStatus="handleFilterByStatus" @refresh="handleStatisticsRefresh" />

    <!-- 搜索和操作区域 -->
    <div class="operation-area">
      <el-card shadow="hover" class="search-card">
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="预约编号">
            <el-input v-model="searchForm.yuyuebianhao" placeholder="请输入预约编号" clearable style="width: 180px" />
          </el-form-item>
          <el-form-item label="医生账号">
            <el-input v-model="searchForm.yishengzhanghao" placeholder="请输入医生账号" clearable style="width: 180px" />
          </el-form-item>
          <el-form-item label="用户账号">
            <el-input v-model="searchForm.zhanghao" placeholder="请输入用户账号" clearable style="width: 180px" />
          </el-form-item>
          <el-form-item label="通知类型">
            <el-select v-model="searchForm.tongzhileixing" placeholder="请选择" clearable style="width: 150px">
              <el-option label="预约成功通知" :value="1" />
              <el-option label="就诊前1天提醒" :value="2" />
              <el-option label="就诊前2小时提醒" :value="3" />
              <el-option label="就诊前15分钟提醒" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="发送状态">
            <el-select v-model="searchForm.fasongzhuangtai" placeholder="请选择" clearable style="width: 150px">
              <el-option label="待发送" :value="0" />
              <el-option label="发送成功" :value="1" />
              <el-option label="发送失败" :value="2" />
              <el-option label="发送中" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户类型">
            <el-select v-model="searchForm.yonghuleixing" placeholder="请选择" clearable style="width: 150px">
              <el-option label="用户" :value="1" />
              <el-option label="医生" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker v-model="searchForm.timeRange" type="datetimerange" range-separator="至"
              start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 360px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
            <el-button @click="handleReset" :icon="RefreshRight">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 批量操作按钮 -->
    <div class="batch-operation-area">
      <el-button type="success" @click="handleBatchRetry" :disabled="selectedRows.length === 0" :icon="Refresh">
        批量重试
      </el-button>
      <el-button type="warning" @click="handleBatchMarkProcessed" :disabled="selectedRows.length === 0" :icon="Check">
        批量标记已处理
      </el-button>
      <el-button type="primary" @click="handleExportFailed" :icon="Download">
        导出失败记录
      </el-button>
      <el-button type="info" @click="handleTriggerBatchSend" :icon="VideoPlay">
        触发批量发送
      </el-button>
      <el-button type="info" @click="handleTriggerBatchRetry" :icon="RefreshLeft">
        触发批量重试
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="hover" class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe @selection-change="handleSelectionChange"
        highlight-current-row>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="yuyuebianhao" label="预约编号" min-width="140" show-overflow-tooltip />
        <el-table-column prop="yishengzhanghao" label="医生账号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="zhanghao" label="用户账号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="yonghuleixing" label="用户类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="UserTypeMap[row.yonghuleixing]?.type">
              {{ UserTypeMap[row.yonghuleixing]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tongzhileixing" label="通知类型" width="140" align="center">
          <template #default="{ row }">
            <el-tag :type="NotificationTypeMap[row.tongzhileixing]?.type">
              {{ NotificationTypeMap[row.tongzhileixing]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fasongzhuangtai" label="发送状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="SendStatusMap[row.fasongzhuangtai]?.type">
              {{ SendStatusMap[row.fasongzhuangtai]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jieshourenshouji" label="接收手机号" min-width="120" />
        <el-table-column prop="jihuafasongshijian" label="计划发送时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.jihuafasongshijian) }}
          </template>
        </el-table-column>
        <el-table-column prop="shijifasongshijian" label="实际发送时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.shijifasongshijian) }}
          </template>
        </el-table-column>
        <el-table-column prop="chongshicishu" label="重试次数" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.chongshicishu > 0" type="warning">{{ row.chongshicishu }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="chulizhuangtai" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="ProcessStatusMap[row.chulizhuangtai]?.type">
              {{ ProcessStatusMap[row.chulizhuangtai]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)" :icon="View">详情</el-button>
            <el-button v-if="row.fasongzhuangtai === 2" link type="success" @click="handleRetry(row)" :icon="Refresh">
              重试
            </el-button>
            <el-button v-if="row.fasongzhuangtai === 2 && row.chulizhuangtai === 0" link type="warning"
              @click="handleMarkProcessed(row)" :icon="Check">
              标记处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.limit"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <DetailDialog v-model:visible="detailVisible" :data="currentRow" @retry="handleRetryFromDetail"
      @markProcessed="handleMarkProcessedFromDetail" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  RefreshRight,
  Refresh,
  Check,
  Download,
  VideoPlay,
  RefreshLeft,
  View
} from '@element-plus/icons-vue'
import {
  getTongzhijiluList,
  manualRetry,
  batchRetry,
  markAsProcessed,
  exportFailedRecords,
  triggerBatchSend,
  triggerBatchRetry,
  NotificationTypeMap,
  SendStatusMap,
  ProcessStatusMap,
  UserTypeMap
} from '@/api/tongzhijilu.js'
import StatisticsPanel from './components/StatisticsPanel.vue'
import DetailDialog from './components/DetailDialog.vue'

// 搜索表单
const searchForm = reactive({
  yuyuebianhao: '',
  yishengzhanghao: '',
  zhanghao: '',
  tongzhileixing: null,
  fasongzhuangtai: null,
  yonghuleixing: null,
  timeRange: null
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

// 分页
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

// 详情对话框
const detailVisible = ref(false)
const currentRow = ref(null)

// 统计面板引用
const statisticsRef = ref(null)

/**
 * 获取列表数据
 */
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      yuyuebianhao: searchForm.yuyuebianhao,
      yishengzhanghao: searchForm.yishengzhanghao,
      zhanghao: searchForm.zhanghao,
      tongzhileixing: searchForm.tongzhileixing,
      fasongzhuangtai: searchForm.fasongzhuangtai,
      yonghuleixing: searchForm.yonghuleixing
    }

    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      params.startTime = searchForm.timeRange[0]
      params.endTime = searchForm.timeRange[1]
    }

    const res = await getTongzhijiluList(params)
    if (res.data.code === 0) {
      tableData.value = res.data.data.list
      pagination.total = res.data.data.total
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

/**
 * 重置
 */
const handleReset = () => {
  searchForm.yuyuebianhao = ''
  searchForm.yishengzhanghao = ''
  searchForm.zhanghao = ''
  searchForm.tongzhileixing = null
  searchForm.fasongzhuangtai = null
  searchForm.yonghuleixing = null
  searchForm.timeRange = null
  pagination.page = 1
  fetchList()
}

/**
 * 表格选择变化
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 分页大小变化
 */
const handleSizeChange = (val) => {
  pagination.limit = val
  fetchList()
}

/**
 * 页码变化
 */
const handleCurrentChange = (val) => {
  pagination.page = val
  fetchList()
}

/**
 * 查看详情
 */
const handleViewDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

/**
 * 重试发送
 */
const handleRetry = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重新发送该通知吗？', '提示', {
      type: 'warning'
    })
    const res = await manualRetry(row.id)
    if (res.data.code === 0) {
      ElMessage.success('重试成功')
      fetchList()
      statisticsRef.value?.fetchStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重试失败:', error)
      ElMessage.error('重试失败')
    }
  }
}

/**
 * 从详情对话框重试
 */
const handleRetryFromDetail = async (row) => {
  await handleRetry(row)
  detailVisible.value = false
}

/**
 * 标记已处理
 */
const handleMarkProcessed = async (row) => {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入处理备注', '标记为已处理', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入处理备注（可选）'
    })

    const res = await markAsProcessed(row.id, remark)
    if (res.data.code === 0) {
      ElMessage.success('标记成功')
      fetchList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
      ElMessage.error('标记失败')
    }
  }
}

/**
 * 从详情对话框标记
 */
const handleMarkProcessedFromDetail = async (row, remark) => {
  try {
    const res = await markAsProcessed(row.id, remark)
    if (res.data.code === 0) {
      ElMessage.success('标记成功')
      fetchList()
      detailVisible.value = false
    }
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

/**
 * 批量重试
 */
const handleBatchRetry = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要重试的记录')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要批量重试选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
      type: 'warning'
    })

    const ids = selectedRows.value.map(row => row.id)
    const res = await batchRetry(ids)
    if (res.data.code === 0) {
      ElMessage.success(`批量重试完成：成功 ${res.data.data.success} 条，失败 ${res.data.data.failed} 条`)
      fetchList()
      statisticsRef.value?.fetchStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量重试失败:', error)
      ElMessage.error('批量重试失败')
    }
  }
}

/**
 * 批量标记已处理
 */
const handleBatchMarkProcessed = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要标记的记录')
    return
  }

  try {
    const { value: remark } = await ElMessageBox.prompt('请输入处理备注', '批量标记为已处理', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入处理备注（可选）'
    })

    let successCount = 0
    for (const row of selectedRows.value) {
      try {
        await markAsProcessed(row.id, remark)
        successCount++
      } catch (error) {
        console.error(`标记记录 ${row.id} 失败:`, error)
      }
    }

    ElMessage.success(`成功标记 ${successCount} 条记录`)
    fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量标记失败:', error)
      ElMessage.error('批量标记失败')
    }
  }
}

/**
 * 导出失败记录
 */
const handleExportFailed = async () => {
  try {
    const params = {}
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      params.startTime = searchForm.timeRange[0]
      params.endTime = searchForm.timeRange[1]
    }

    const res = await exportFailedRecords(params)
    if (res.data.code === 0) {
      // 转换为CSV并下载
      const data = res.data.data
      if (data.length === 0) {
        ElMessage.warning('暂无失败记录可导出')
        return
      }

      let csvContent = '\uFEFF' // BOM for Excel
      const headers = Object.keys(data[0])
      csvContent += headers.join(',') + '\n'

      data.forEach(row => {
        const values = headers.map(header => {
          const value = row[header]
          // 处理包含逗号或换行符的值
          if (typeof value === 'string' && (value.includes(',') || value.includes('\n'))) {
            return `"${value.replace(/"/g, '""')}"`
          }
          return value
        })
        csvContent += values.join(',') + '\n'
      })

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `失败通知记录_${new Date().toLocaleDateString()}.csv`
      link.click()

      ElMessage.success('导出成功')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

/**
 * 触发批量发送
 */
const handleTriggerBatchSend = async () => {
  try {
    await ElMessageBox.confirm('确定要触发批量发送任务吗？', '提示', {
      type: 'info'
    })
    const res = await triggerBatchSend()
    if (res.data.code === 0) {
      ElMessage.success('批量发送任务已触发')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('触发失败:', error)
      ElMessage.error('触发失败')
    }
  }
}

/**
 * 触发批量重试
 */
const handleTriggerBatchRetry = async () => {
  try {
    await ElMessageBox.confirm('确定要触发批量重试任务吗？', '提示', {
      type: 'info'
    })
    const res = await triggerBatchRetry()
    if (res.data.code === 0) {
      ElMessage.success('批量重试任务已触发')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('触发失败:', error)
      ElMessage.error('触发失败')
    }
  }
}

/**
 * 按状态筛选
 */
const handleFilterByStatus = (status) => {
  searchForm.fasongzhuangtai = status
  handleSearch()
}

/**
 * 统计刷新
 */
const handleStatisticsRefresh = () => {
  fetchList()
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 初始化
onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.tongzhijilu-container {
  padding: 20px;

  .operation-area {
    margin-bottom: 20px;

    .search-card {
      :deep(.el-card__body) {
        padding: 20px;
      }
    }

    .search-form {
      .el-form-item {
        margin-bottom: 15px;
      }
    }
  }

  .batch-operation-area {
    margin-bottom: 20px;

    .el-button {
      margin-right: 10px;
    }
  }

  .table-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

// 响应式适配
@media screen and (max-width: 768px) {
  .tongzhijilu-container {
    padding: 10px;

    .operation-area {
      .search-form {
        .el-form-item {
          width: 100%;
          margin-right: 0;

          .el-input,
          .el-select,
          .el-date-editor {
            width: 100% !important;
          }
        }
      }
    }

    .batch-operation-area {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .el-button {
        margin-right: 0;
        flex: 1;
        min-width: 120px;
      }
    }
  }
}
</style>
