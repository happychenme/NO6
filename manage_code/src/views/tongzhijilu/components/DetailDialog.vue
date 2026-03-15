<template>
  <el-dialog
    v-model="dialogVisible"
    title="通知详情"
    width="700px"
    destroy-on-close
    class="detail-dialog"
  >
    <div v-if="data" class="detail-content">
      <!-- 基本信息 -->
      <el-descriptions :column="2" border title="基本信息">
        <el-descriptions-item label="预约编号">
          {{ data.yuyuebianhao || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="通知类型">
          <el-tag :type="NotificationTypeMap[data.tongzhileixing]?.type || 'info'">
            {{ NotificationTypeMap[data.tongzhileixing]?.label || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="医生账号">
          {{ data.yishengzhanghao || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="用户账号">
          {{ data.zhanghao || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="用户类型">
          <el-tag :type="UserTypeMap[data.yonghuleixing]?.type || ''">
            {{ UserTypeMap[data.yonghuleixing]?.label || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收手机号">
          {{ data.jieshourenshouji || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 发送信息 -->
      <el-descriptions :column="2" border title="发送信息" class="mt-20">
        <el-descriptions-item label="发送状态">
          <el-tag :type="SendStatusMap[data.fasongzhuangtai]?.type || 'info'" size="large">
            {{ SendStatusMap[data.fasongzhuangtai]?.label || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="重试次数">
          <el-tag v-if="data.chongshicishu > 0" type="warning">{{ data.chongshicishu }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="计划发送时间">
          {{ formatDateTime(data.jihuafasongshijian) }}
        </el-descriptions-item>
        <el-descriptions-item label="实际发送时间">
          {{ formatDateTime(data.shijifasongshijian) }}
        </el-descriptions-item>
        <el-descriptions-item label="就诊时间">
          {{ formatDateTime(data.jiuzhenshijian) }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(data.addtime) }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 失败信息 -->
      <el-descriptions
        v-if="data.fasongzhuangtai === 2"
        :column="1"
        border
        title="失败信息"
        class="mt-20"
      >
        <el-descriptions-item label="失败原因">
          <div class="error-reason">{{ data.shibaiyuanyin || '未知错误' }}</div>
        </el-descriptions-item>
        <el-descriptions-item v-if="data.xiacichongshishijian" label="下次重试时间">
          {{ formatDateTime(data.xiacichongshishijian) }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 处理信息 -->
      <el-descriptions
        v-if="data.chulizhuangtai !== 0"
        :column="2"
        border
        title="处理信息"
        class="mt-20"
      >
        <el-descriptions-item label="处理状态">
          <el-tag :type="ProcessStatusMap[data.chulizhuangtai]?.type || 'info'">
            {{ ProcessStatusMap[data.chulizhuangtai]?.label || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">
          {{ data.chuliren || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">
          {{ formatDateTime(data.chulishijian) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理备注">
          {{ data.chulibeizhu || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 通知内容 -->
      <div class="notification-content mt-20">
        <div class="content-title">通知内容</div>
        <div class="content-box">{{ data.tongzhineirong || '-' }}</div>
      </div>

      <!-- 时间线 -->
      <div class="timeline-section mt-20">
        <div class="content-title">处理时间线</div>
        <el-timeline>
          <el-timeline-item
            :type="'primary'"
            :hollow="true"
            :timestamp="formatDateTime(data.addtime)"
          >
            通知创建
          </el-timeline-item>
          <el-timeline-item
            v-if="data.shijifasongshijian"
            :type="data.fasongzhuangtai === 1 ? 'success' : 'danger'"
            :timestamp="formatDateTime(data.shijifasongshijian)"
          >
            {{ data.fasongzhuangtai === 1 ? '发送成功' : '发送失败' }}
          </el-timeline-item>
          <el-timeline-item
            v-for="(retry, index) in retryHistory"
            :key="index"
            type="warning"
            :timestamp="retry.time"
          >
            第 {{ index + 1 }} 次重试{{ retry.success ? '成功' : '失败' }}
          </el-timeline-item>
          <el-timeline-item
            v-if="data.chulizhuangtai !== 0 && data.chulishijian"
            type="info"
            :timestamp="formatDateTime(data.chulishijian)"
          >
            标记为{{ data.chulizhuangtai === 1 ? '已处理' : '已忽略' }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button
          v-if="data?.fasongzhuangtai === 2"
          type="success"
          @click="handleRetry"
          :icon="Refresh"
        >
          重新发送
        </el-button>
        <el-button
          v-if="data?.fasongzhuangtai === 2 && data?.chulizhuangtai === 0"
          type="warning"
          @click="handleMarkProcessed"
          :icon="Check"
        >
          标记已处理
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Refresh, Check } from '@element-plus/icons-vue'
import {
  NotificationTypeMap,
  SendStatusMap,
  UserTypeMap,
  ProcessStatusMap
} from '@/api/tongzhijilu.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'retry', 'markProcessed'])

// 对话框可见性
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 重试历史（模拟数据，实际可从后端获取）
const retryHistory = computed(() => {
  if (!props.data || props.data.chongshicishu === 0) return []
  
  const history = []
  for (let i = 0; i < props.data.chongshicishu; i++) {
    history.push({
      time: formatDateTime(new Date(Date.now() - (props.data.chongshicishu - i) * 60000)),
      success: false
    })
  }
  return history
})

/**
 * 格式化日期时间
 */
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

/**
 * 重新发送
 */
const handleRetry = async () => {
  try {
    await ElMessageBox.confirm('确定要重新发送该通知吗？', '提示', {
      type: 'warning'
    })
    emit('retry', props.data)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重试失败:', error)
    }
  }
}

/**
 * 标记为已处理
 */
const handleMarkProcessed = async () => {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入处理备注', '标记为已处理', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入处理备注（可选）'
    })
    
    emit('markProcessed', props.data, remark)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
    }
  }
}
</script>

<style scoped lang="scss">
.detail-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
    max-height: 60vh;
    overflow-y: auto;
  }

  .detail-content {
    .mt-20 {
      margin-top: 20px;
    }

    .error-reason {
      color: #f56c6c;
      padding: 10px;
      background-color: #fef0f0;
      border-radius: 4px;
      line-height: 1.6;
    }

    .notification-content {
      .content-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 10px;
        padding-left: 10px;
        border-left: 4px solid #409eff;
      }

      .content-box {
        padding: 15px;
        background-color: #f5f7fa;
        border-radius: 4px;
        line-height: 1.8;
        color: #606266;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }

    .timeline-section {
      .content-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 15px;
        padding-left: 10px;
        border-left: 4px solid #409eff;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}

// 响应式适配
@media screen and (max-width: 768px) {
  .detail-dialog {
    :deep(.el-dialog) {
      width: 90% !important;
    }

    :deep(.el-dialog__body) {
      padding: 15px;
    }

    .detail-content {
      .notification-content {
        .content-box {
          padding: 10px;
          font-size: 14px;
        }
      }
    }

    .dialog-footer {
      flex-wrap: wrap;

      .el-button {
        flex: 1;
        min-width: 100px;
      }
    }
  }
}
</style>
