package com.ruoyi.business.stock.service;

import java.util.List;
import com.ruoyi.business.stock.domain.BizStockOrder;

/**
 * 出入库单Service接口
 *
 * @author renovationops
 */
public interface IBizStockOrderService
{
    public BizStockOrder selectBizStockOrderById(Long orderId);

    public List<BizStockOrder> selectBizStockOrderList(BizStockOrder order);

    /** 新增草稿（含明细，自动计算合计金额） */
    public int insertBizStockOrder(BizStockOrder order);

    /** 修改草稿/驳回单（明细整体替换） */
    public int updateBizStockOrder(BizStockOrder order);

    public int deleteBizStockOrderByIds(Long[] orderIds);

    /**
     * 提交单据：
     * 入库单直接完成并按移动加权平均更新库存；
     * 出库单校验库存充足后进入审批流
     */
    public void submitStockOrder(Long orderId);

    /** 作废（草稿/已驳回可作废，已完成的不可作废） */
    public void invalidateStockOrder(Long orderId);
}
