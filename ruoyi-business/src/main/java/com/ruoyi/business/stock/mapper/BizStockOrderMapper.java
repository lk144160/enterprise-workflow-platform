package com.ruoyi.business.stock.mapper;

import java.util.List;
import com.ruoyi.business.stock.domain.BizStockOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 出入库单 Mapper 接口
 *
 * @author renovationops
 */
public interface BizStockOrderMapper
{
    public BizStockOrder selectBizStockOrderById(Long orderId);

    public List<BizStockOrder> selectBizStockOrderList(BizStockOrder order);

    public int insertBizStockOrder(BizStockOrder order);

    public int updateBizStockOrder(BizStockOrder order);

    public int deleteBizStockOrderByIds(Long[] orderIds);
}
