package com.ruoyi.business.stock.mapper;

import java.util.List;
import com.ruoyi.business.stock.domain.BizStockOrderItem;

/**
 * 出入库明细 Mapper 接口
 *
 * @author renovationops
 */
public interface BizStockOrderItemMapper
{
    public List<BizStockOrderItem> selectItemsByOrderId(Long orderId);

    public int batchInsert(List<BizStockOrderItem> items);

    public int deleteByOrderId(Long orderId);
}
