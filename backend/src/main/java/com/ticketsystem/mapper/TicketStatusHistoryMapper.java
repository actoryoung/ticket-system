package com.ticketsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticketsystem.entity.TicketStatusHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单状态历史Mapper接口
 */
@Mapper
public interface TicketStatusHistoryMapper extends BaseMapper<TicketStatusHistory> {
}
