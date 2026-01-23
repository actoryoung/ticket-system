package com.ticketsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticketsystem.entity.TicketComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单评论Mapper接口
 */
@Mapper
public interface TicketCommentMapper extends BaseMapper<TicketComment> {
}
