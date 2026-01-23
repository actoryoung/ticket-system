import request from '@/utils/request'

/**
 * 获取工单列表
 */
export function getTicketList(params) {
  return request({
    url: '/ticket/list',
    method: 'get',
    params
  })
}

/**
 * 获取工单详情
 */
export function getTicketDetail(id) {
  return request({
    url: `/ticket/${id}`,
    method: 'get'
  })
}

/**
 * 创建工单
 */
export function createTicket(data) {
  return request({
    url: '/ticket/create',
    method: 'post',
    data
  })
}

/**
 * 更新工单
 */
export function updateTicket(id, data) {
  return request({
    url: `/ticket/${id}`,
    method: 'put',
    data
  })
}

/**
 * 分配工单
 */
export function assignTicket(id, handlerId) {
  return request({
    url: `/ticket/${id}/assign`,
    method: 'put',
    data: { handlerId }
  })
}

/**
 * 更新工单状态
 */
export function updateTicketStatus(id, status, remark) {
  return request({
    url: `/ticket/${id}/status`,
    method: 'put',
    data: { status, remark }
  })
}

/**
 * 添加工单评论
 */
export function addComment(id, data) {
  return request({
    url: `/ticket/${id}/comment`,
    method: 'post',
    data
  })
}

/**
 * 获取工单评论列表
 */
export function getTicketComments(id) {
  return request({
    url: `/ticket/${id}/comments`,
    method: 'get'
  })
}

/**
 * 获取工单状态历史
 */
export function getTicketHistory(id) {
  return request({
    url: `/ticket/${id}/history`,
    method: 'get'
  })
}

/**
 * 获取统计数据（待实现）
 */
export function getStatistics() {
  // TODO: 后端需要实现统计接口
  return request({
    url: '/statistics',
    method: 'get'
  })
}

/**
 * 上传附件
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
