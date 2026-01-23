package com.ticketsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticketsystem.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工单Mapper接口
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {

    /**
     * 分页查询工单列表（包含用户信息）
     *
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @param status 状态
     * @param priority 优先级
     * @param category 分类
     * @param creatorId 创建人ID
     * @param handlerId 处理人ID
     * @return 工单分页列表
     */
    IPage<Ticket> selectTicketPageWithUserInfo(
            Page<Ticket> page,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("category") String category,
            @Param("creatorId") Long creatorId,
            @Param("handlerId") Long handlerId
    );
}
