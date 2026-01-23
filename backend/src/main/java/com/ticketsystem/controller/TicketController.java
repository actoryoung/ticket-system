package com.ticketsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticketsystem.common.Result;
import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketComment;
import com.ticketsystem.service.TicketService;
import com.ticketsystem.util.SecurityUtil;
import com.ticketsystem.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 工单控制器
 */
@Slf4j
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final TicketService ticketService;

    /**
     * 分页查询工单列表
     */
    @GetMapping("/list")
    public Result<IPage<Ticket>> getTicketList(@Valid TicketQueryDTO queryDTO) {
        log.info("查询工单列表, queryDTO={}", queryDTO);

        // 如果不是管理员或处理人员，只能查看自己创建的工单
        Long currentUserId = SecurityUtil.getUserId();
        String currentRole = SecurityUtil.getRole();

        if (!("ADMIN".equals(currentRole) || "HANDLER".equals(currentRole))) {
            queryDTO.setCreatorId(currentUserId);
        }

        IPage<Ticket> page = ticketService.getTicketPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取工单详情
     */
    @GetMapping("/{id}")
    public Result<TicketVO> getTicketDetail(@PathVariable("id") Long id) {
        log.info("查询工单详情, ticketId={}", id);
        TicketVO ticketVO = ticketService.getTicketDetail(id);
        return Result.success(ticketVO);
    }

    /**
     * 创建工单
     */
    @PostMapping("/create")
    public Result<Long> createTicket(@Valid @RequestBody CreateTicketDTO createDTO) {
        log.info("创建工单, title={}, category={}", createDTO.getTitle(), createDTO.getCategory());
        Long ticketId = ticketService.createTicket(createDTO, SecurityUtil.getUserId());
        return Result.success("工单创建成功", ticketId);
    }

    /**
     * 更新工单
     */
    @PutMapping("/{id}")
    public Result<Void> updateTicket(@PathVariable("id") Long id,
                                     @Valid @RequestBody UpdateTicketDTO updateDTO) {
        log.info("更新工单, ticketId={}", id);
        updateDTO.setId(id);
        ticketService.updateTicket(updateDTO, SecurityUtil.getUserId());
        return Result.success("工单更新成功");
    }

    /**
     * 分配工单
     */
    @PutMapping("/{id}/assign")
    public Result<Void> assignTicket(@PathVariable("id") Long id,
                                     @Valid @RequestBody AssignTicketDTO assignDTO) {
        log.info("分配工单, ticketId={}, handlerId={}", id, assignDTO.getHandlerId());
        ticketService.assignTicket(id, assignDTO, SecurityUtil.getUserId());
        return Result.success("工单分配成功");
    }

    /**
     * 更新工单状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateTicketStatus(@PathVariable("id") Long id,
                                           @Valid @RequestBody UpdateStatusDTO updateDTO) {
        log.info("更新工单状态, ticketId={}, status={}", id, updateDTO.getStatus());
        ticketService.updateTicketStatus(id, updateDTO, SecurityUtil.getUserId());
        return Result.success("工单状态更新成功");
    }

    /**
     * 添加工单评论
     */
    @PostMapping("/{id}/comment")
    public Result<Long> addComment(@PathVariable("id") Long id,
                                   @Valid @RequestBody AddCommentDTO commentDTO) {
        log.info("添加工单评论, ticketId={}", id);
        Long commentId = ticketService.addComment(id, commentDTO, SecurityUtil.getUserId());
        return Result.success("评论添加成功", commentId);
    }

    /**
     * 获取工单评论列表
     */
    @GetMapping("/{id}/comments")
    public Result<List<TicketComment>> getTicketComments(@PathVariable("id") Long id) {
        log.info("查询工单评论, ticketId={}", id);
        List<TicketComment> comments = ticketService.getTicketComments(id);
        return Result.success(comments);
    }

    /**
     * 获取工单状态历史
     */
    @GetMapping("/{id}/history")
    public Result<List<Object>> getTicketStatusHistory(@PathVariable("id") Long id) {
        log.info("查询工单状态历史, ticketId={}", id);
        List<Object> history = ticketService.getTicketStatusHistory(id);
        return Result.success(history);
    }
}
