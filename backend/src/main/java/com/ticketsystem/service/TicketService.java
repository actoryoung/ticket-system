package com.ticketsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketComment;
import com.ticketsystem.vo.TicketVO;

import java.util.List;

/**
 * 工单服务接口
 */
public interface TicketService extends IService<Ticket> {

    /**
     * 分页查询工单列表
     *
     * @param queryDTO 查询条件
     * @return 工单分页列表
     */
    IPage<Ticket> getTicketPage(TicketQueryDTO queryDTO);

    /**
     * 获取工单详情
     *
     * @param ticketId 工单ID
     * @return 工单详情
     */
    TicketVO getTicketDetail(Long ticketId);

    /**
     * 创建工单
     *
     * @param createDTO 创建工单请求
     * @param creatorId 创建人ID
     * @return 工单ID
     */
    Long createTicket(CreateTicketDTO createDTO, Long creatorId);

    /**
     * 更新工单
     *
     * @param updateDTO 更新工单请求
     * @param userId 当前用户ID
     */
    void updateTicket(UpdateTicketDTO updateDTO, Long userId);

    /**
     * 分配工单
     *
     * @param ticketId 工单ID
     * @param assignDTO 分配请求
     * @param operatorId 操作人ID
     */
    void assignTicket(Long ticketId, AssignTicketDTO assignDTO, Long operatorId);

    /**
     * 更新工单状态
     *
     * @param ticketId 工单ID
     * @param updateDTO 状态更新请求
     * @param operatorId 操作人ID
     */
    void updateTicketStatus(Long ticketId, UpdateStatusDTO updateDTO, Long operatorId);

    /**
     * 添加工单评论
     *
     * @param ticketId 工单ID
     * @param commentDTO 评论请求
     * @param userId 评论人ID
     * @return 评论ID
     */
    Long addComment(Long ticketId, AddCommentDTO commentDTO, Long userId);

    /**
     * 获取工单评论列表
     *
     * @param ticketId 工单ID
     * @return 评论列表
     */
    List<TicketComment> getTicketComments(Long ticketId);

    /**
     * 获取工单状态历史
     *
     * @param ticketId 工单ID
     * @return 状态历史列表
     */
    List<Object> getTicketStatusHistory(Long ticketId);
}
