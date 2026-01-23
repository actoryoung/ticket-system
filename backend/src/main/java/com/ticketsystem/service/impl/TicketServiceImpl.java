package com.ticketsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticketsystem.common.ResultCode;
import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketComment;
import com.ticketsystem.entity.TicketStatusHistory;
import com.ticketsystem.entity.User;
import com.ticketsystem.enums.TicketStatus;
import com.ticketsystem.exception.BusinessException;
import com.ticketsystem.mapper.TicketCommentMapper;
import com.ticketsystem.mapper.TicketMapper;
import com.ticketsystem.mapper.TicketStatusHistoryMapper;
import com.ticketsystem.mapper.UserMapper;
import com.ticketsystem.service.TicketService;
import com.ticketsystem.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 工单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    private final TicketMapper ticketMapper;
    private final TicketStatusHistoryMapper statusHistoryMapper;
    private final TicketCommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<Ticket> getTicketPage(TicketQueryDTO queryDTO) {
        Page<Ticket> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        return ticketMapper.selectTicketPageWithUserInfo(
                page,
                queryDTO.getKeyword(),
                queryDTO.getStatus(),
                queryDTO.getPriority(),
                queryDTO.getCategory(),
                queryDTO.getCreatorId(),
                queryDTO.getHandlerId()
        );
    }

    @Override
    public TicketVO getTicketDetail(Long ticketId) {
        Ticket ticket = getById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_FOUND);
        }

        // 查询创建人和处理人信息
        User creator = userMapper.selectById(ticket.getCreatorId());
        User handler = ticket.getHandlerId() != null ? userMapper.selectById(ticket.getHandlerId()) : null;

        // 转换为VO
        TicketVO vo = new TicketVO();
        BeanUtil.copyProperties(ticket, vo);
        if (creator != null) {
            vo.setCreatorName(creator.getRealName());
        }
        if (handler != null) {
            vo.setHandlerName(handler.getRealName());
        }

        // 解析附件URL
        if (StrUtil.isNotBlank(ticket.getAttachmentUrls())) {
            vo.setAttachmentUrls(JSONUtil.toList(ticket.getAttachmentUrls(), String.class));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTicket(CreateTicketDTO createDTO, Long creatorId) {
        // 1. 查询创建人信息
        User creator = userMapper.selectById(creatorId);
        if (creator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 构建工单实体
        Ticket ticket = new Ticket();
        BeanUtil.copyProperties(createDTO, ticket);

        // 生成工单编号
        String ticketNo = generateTicketNo();
        ticket.setTicketNo(ticketNo);

        ticket.setCreatorId(creatorId);
        ticket.setStatus(TicketStatus.PENDING.getCode());

        // 转换附件URL为JSON字符串
        if (createDTO.getAttachmentUrls() != null && !createDTO.getAttachmentUrls().isEmpty()) {
            ticket.setAttachmentUrls(JSONUtil.toJsonStr(createDTO.getAttachmentUrls()));
        }

        // 3. 保存工单
        save(ticket);

        // 4. 记录状态历史
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticket.getId());
        history.setNewStatus(TicketStatus.PENDING.getCode());
        history.setOperatorId(creatorId);
        history.setOperatorName(creator.getRealName());
        history.setRemark("创建工单");
        statusHistoryMapper.insert(history);

        log.info("创建工单成功, ticketNo={}, creatorId={}", ticketNo, creatorId);
        return ticket.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTicket(UpdateTicketDTO updateDTO, Long userId) {
        // 1. 查询工单
        Ticket ticket = getById(updateDTO.getId());
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_FOUND);
        }

        // 2. 权限检查：只有创建人可以修改
        if (!ticket.getCreatorId().equals(userId)) {
            throw new BusinessException(ResultCode.TICKET_PERMISSION_DENIED);
        }

        // 3. 只有待处理状态可以修改
        if (!TicketStatus.PENDING.getCode().equals(ticket.getStatus())) {
            throw new BusinessException(ResultCode.TICKET_STATUS_INVALID);
        }

        // 4. 更新工单
        Ticket updateEntity = new Ticket();
        BeanUtil.copyProperties(updateDTO, updateEntity);
        updateEntity.setId(updateDTO.getId());
        updateById(updateEntity);

        log.info("更新工单成功, ticketId={}, operatorId={}", updateDTO.getId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTicket(Long ticketId, AssignTicketDTO assignDTO, Long operatorId) {
        // 1. 查询工单
        Ticket ticket = getById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_FOUND);
        }

        // 2. 验证处理人是否存在
        User handler = userMapper.selectById(assignDTO.getHandlerId());
        if (handler == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 3. 检查状态
        if (!TicketStatus.PENDING.getCode().equals(ticket.getStatus())) {
            throw new BusinessException(ResultCode.TICKET_STATUS_INVALID);
        }

        // 4. 更新工单处理人
        ticket.setHandlerId(assignDTO.getHandlerId());
        ticket.setStatus(TicketStatus.PROCESSING.getCode());
        updateById(ticket);

        // 5. 记录状态历史
        User operator = userMapper.selectById(operatorId);
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(ticket.getStatus());
        history.setNewStatus(TicketStatus.PROCESSING.getCode());
        history.setOperatorId(operatorId);
        history.setOperatorName(operator != null ? operator.getRealName() : "");
        history.setRemark(String.format("分配给 %s", handler.getRealName()));
        statusHistoryMapper.insert(history);

        log.info("分配工单成功, ticketId={}, handlerId={}", ticketId, assignDTO.getHandlerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTicketStatus(Long ticketId, UpdateStatusDTO updateDTO, Long operatorId) {
        // 1. 查询工单
        Ticket ticket = getById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_FOUND);
        }

        // 2. 验证状态转换是否合法
        String oldStatus = ticket.getStatus();
        String newStatus = updateDTO.getStatus();

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new BusinessException(ResultCode.TICKET_STATUS_INVALID);
        }

        // 3. 更新工单状态
        ticket.setStatus(newStatus);

        // 根据状态更新时间
        if (TicketStatus.RESOLVED.getCode().equals(newStatus)) {
            ticket.setResolvedTime(LocalDateTime.now());
        } else if (TicketStatus.CLOSED.getCode().equals(newStatus)) {
            ticket.setClosedTime(LocalDateTime.now());
        }

        updateById(ticket);

        // 4. 记录状态历史
        User operator = userMapper.selectById(operatorId);
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setOperatorId(operatorId);
        history.setOperatorName(operator != null ? operator.getRealName() : "");
        history.setRemark(updateDTO.getRemark());
        statusHistoryMapper.insert(history);

        log.info("更新工单状态成功, ticketId={}, oldStatus={}, newStatus={}", ticketId, oldStatus, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(Long ticketId, AddCommentDTO commentDTO, Long userId) {
        // 1. 查询工单
        Ticket ticket = getById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_FOUND);
        }

        // 2. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 3. 构建评论
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setUserId(userId);
        comment.setUserName(user.getRealName());
        comment.setContent(commentDTO.getContent());
        comment.setIsInternal(commentDTO.getIsInternal() != null ? commentDTO.getIsInternal() : 0);

        // 转换附件URL为JSON字符串
        if (commentDTO.getAttachmentUrls() != null && !commentDTO.getAttachmentUrls().isEmpty()) {
            comment.setAttachmentUrls(JSONUtil.toJsonStr(commentDTO.getAttachmentUrls()));
        }

        // 4. 保存评论
        commentMapper.insert(comment);

        log.info("添加工单评论成功, ticketId={}, commentId={}", ticketId, comment.getId());
        return comment.getId();
    }

    @Override
    public List<TicketComment> getTicketComments(Long ticketId) {
        LambdaQueryWrapper<TicketComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketComment::getTicketId, ticketId)
                .orderByAsc(TicketComment::getCreateTime);

        // 如果不是管理员或处理人，只返回非内部评论
        // TODO: 根据当前用户角色过滤内部评论

        return commentMapper.selectList(wrapper);
    }

    @Override
    public List<Object> getTicketStatusHistory(Long ticketId) {
        LambdaQueryWrapper<TicketStatusHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketStatusHistory::getTicketId, ticketId)
                .orderByAsc(TicketStatusHistory::getCreateTime);
        return statusHistoryMapper.selectList(wrapper);
    }

    /**
     * 生成工单编号
     * 格式: T + yyyyMMdd + 6位序号
     */
    private String generateTicketNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "T" + dateStr;

        // 查询当天最大的工单号
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Ticket::getTicketNo, prefix)
                .orderByDesc(Ticket::getTicketNo)
                .last("LIMIT 1");

        Ticket lastTicket = getOne(wrapper);

        int seq = 1;
        if (lastTicket != null) {
            String lastTicketNo = lastTicket.getTicketNo();
            String lastSeq = lastTicketNo.substring(prefix.length());
            seq = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("%s%06d", prefix, seq);
    }

    /**
     * 验证状态转换是否合法
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus.equals(newStatus)) {
            return false;
        }

        TicketStatus old = TicketStatus.fromCode(oldStatus);
        TicketStatus newStat = TicketStatus.fromCode(newStatus);

        // 待处理 -> 处理中
        if (old == TicketStatus.PENDING && newStat == TicketStatus.PROCESSING) {
            return true;
        }

        // 处理中 -> 已完成
        if (old == TicketStatus.PROCESSING && newStat == TicketStatus.RESOLVED) {
            return true;
        }

        // 已完成 -> 已关闭
        if (old == TicketStatus.RESOLVED && newStat == TicketStatus.CLOSED) {
            return true;
        }

        // 处理中 -> 待处理 (重新分配)
        if (old == TicketStatus.PROCESSING && newStat == TicketStatus.PENDING) {
            return true;
        }

        return false;
    }
}
