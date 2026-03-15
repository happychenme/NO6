/**
 * 通知记录API服务层
 * 提供通知记录的增删改查、统计、重试等接口调用
 */

import http from '@/utils/http.js'

/**
 * 获取通知记录列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getTongzhijiluList(params) {
    return http.get('/tongzhijilu/page', { params })
}

/**
 * 获取通知记录详情
 * @param {Number} id - 记录ID
 * @returns {Promise}
 */
export function getTongzhijiluDetail(id) {
    return http.get(`/tongzhijilu/info/${id}`)
}

/**
 * 保存通知记录
 * @param {Object} data - 通知记录数据
 * @returns {Promise}
 */
export function saveTongzhijilu(data) {
    return http.post('/tongzhijilu/save', data)
}

/**
 * 更新通知记录
 * @param {Object} data - 通知记录数据
 * @returns {Promise}
 */
export function updateTongzhijilu(data) {
    return http.post('/tongzhijilu/update', data)
}

/**
 * 删除通知记录
 * @param {Array} ids - 记录ID数组
 * @returns {Promise}
 */
export function deleteTongzhijilu(ids) {
    return http.post('/tongzhijilu/delete', ids)
}

/**
 * 手动重试发送通知
 * @param {Number} id - 记录ID
 * @returns {Promise}
 */
export function manualRetry(id) {
    return http.get(`/tongzhijilu/manualRetry/${id}`)
}

/**
 * 批量手动重试
 * @param {Array} ids - 记录ID数组
 * @returns {Promise}
 */
export function batchRetry(ids) {
    return http.post('/tongzhijilu/batchRetry', ids)
}

/**
 * 标记通知为已处理
 * @param {Number} id - 记录ID
 * @param {String} remark - 处理备注
 * @returns {Promise}
 */
export function markAsProcessed(id, remark = '') {
    return http.get(`/tongzhijilu/markAsProcessed/${id}`, {
        params: { remark }
    })
}

/**
 * 获取发送状态统计
 * @returns {Promise}
 */
export function getStatistics() {
    return http.get('/tongzhijilu/statistics')
}

/**
 * 导出失败记录
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function exportFailedRecords(params) {
    return http.get('/tongzhijilu/exportFailed', { params })
}

/**
 * 触发批量发送任务
 * @returns {Promise}
 */
export function triggerBatchSend() {
    return http.get('/tongzhijilu/triggerBatchSend')
}

/**
 * 触发批量重试任务
 * @returns {Promise}
 */
export function triggerBatchRetry() {
    return http.get('/tongzhijilu/triggerBatchRetry')
}

/**
 * 通知类型映射
 */
export const NotificationTypeMap = {
    1: { label: '预约成功通知', type: 'success' },
    2: { label: '就诊前1天提醒', type: 'warning' },
    3: { label: '就诊前2小时提醒', type: 'warning' },
    4: { label: '就诊前15分钟提醒', type: 'danger' }
}

/**
 * 发送状态映射
 */
export const SendStatusMap = {
    0: { label: '待发送', type: 'info' },
    1: { label: '发送成功', type: 'success' },
    2: { label: '发送失败', type: 'danger' },
    3: { label: '发送中', type: 'warning' }
}

/**
 * 处理状态映射
 */
export const ProcessStatusMap = {
    0: { label: '未处理', type: 'info' },
    1: { label: '已处理', type: 'success' },
    2: { label: '已忽略', type: 'warning' }
}

/**
 * 用户类型映射
 */
export const UserTypeMap = {
    1: { label: '用户', type: '' },
    2: { label: '医生', type: 'primary' }
}

/**
 * 获取通知类型文本
 * @param {Number} type - 通知类型
 * @returns {String}
 */
export function getNotificationTypeText(type) {
    return NotificationTypeMap[type]?.label || '未知'
}

/**
 * 获取发送状态文本
 * @param {Number} status - 发送状态
 * @returns {String}
 */
export function getSendStatusText(status) {
    return SendStatusMap[status]?.label || '未知'
}

/**
 * 获取处理状态文本
 * @param {Number} status - 处理状态
 * @returns {String}
 */
export function getProcessStatusText(status) {
    return ProcessStatusMap[status]?.label || '未知'
}

/**
 * 获取用户类型文本
 * @param {Number} type - 用户类型
 * @returns {String}
 */
export function getUserTypeText(type) {
    return UserTypeMap[type]?.label || '未知'
}
