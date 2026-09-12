package com.ruoyi.business.stock.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.business.stock.domain.BizInventory;
import com.ruoyi.business.stock.domain.BizMaterial;
import com.ruoyi.business.stock.domain.BizStockOrder;
import com.ruoyi.business.stock.domain.BizStockOrderItem;
import com.ruoyi.business.stock.mapper.BizInventoryMapper;
import com.ruoyi.business.stock.mapper.BizMaterialMapper;
import com.ruoyi.business.stock.mapper.BizStockOrderItemMapper;
import com.ruoyi.business.stock.mapper.BizStockOrderMapper;
import com.ruoyi.business.stock.service.IBizStockOrderService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 出入库单服务实现
 * 入库：提交即完成，按移动加权平均更新库存；
 * 出库：提交进入审批流，审批通过后原子扣减库存
 *
 * @author renovationops
 */
@Service
public class BizStockOrderServiceImpl implements IBizStockOrderService, FlowCallback
{
    /** 单据状态 */
    public static final String STATUS_DRAFT = "0";
    public static final String STATUS_AUDITING = "1";
    public static final String STATUS_FINISHED = "2";
    public static final String STATUS_REJECTED = "3";
    public static final String STATUS_INVALID = "4";

    /** 单据类型 */
    public static final String TYPE_IN = "1";
    public static final String TYPE_OUT = "2";

    @Autowired
    private BizStockOrderMapper orderMapper;

    @Autowired
    private BizStockOrderItemMapper itemMapper;

    @Autowired
    private BizInventoryMapper inventoryMapper;

    @Autowired
    private BizMaterialMapper materialMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Override
    public BizStockOrder selectBizStockOrderById(Long orderId)
    {
        BizStockOrder order = orderMapper.selectBizStockOrderById(orderId);
        if (order != null)
        {
            order.setItems(itemMapper.selectItemsByOrderId(orderId));
        }
        return order;
    }

    @Override
    public List<BizStockOrder> selectBizStockOrderList(BizStockOrder order)
    {
        return orderMapper.selectBizStockOrderList(order);
    }

    @Override
    @Transactional
    public int insertBizStockOrder(BizStockOrder order)
    {
        checkOrder(order);
        order.setOrderNo(businessNoService.nextNo(TYPE_IN.equals(order.getOrderType()) ? "RK" : "CK"));
        order.setApplicantId(SecurityUtils.getUserId());
        order.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        order.setStatus(STATUS_DRAFT);
        order.setCreateBy(SecurityUtils.getUsername());
        order.setTotalAmount(sumAmount(order.getItems()));
        orderMapper.insertBizStockOrder(order);
        saveItems(order);
        return 1;
    }

    @Override
    @Transactional
    public int updateBizStockOrder(BizStockOrder order)
    {
        BizStockOrder exist = mustGet(order.getOrderId());
        checkOwner(exist);
        if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的出入库单可修改");
        }
        checkOrder(order);
        order.setOrderType(exist.getOrderType());
        order.setTotalAmount(sumAmount(order.getItems()));
        order.setUpdateBy(SecurityUtils.getUsername());
        orderMapper.updateBizStockOrder(order);
        itemMapper.deleteByOrderId(order.getOrderId());
        saveItems(order);
        return 1;
    }

    @Override
    @Transactional
    public int deleteBizStockOrderByIds(Long[] orderIds)
    {
        for (Long orderId : orderIds)
        {
            BizStockOrder exist = mustGet(orderId);
            checkOwner(exist);
            if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
            {
                throw new ServiceException("出入库单【" + exist.getOrderNo() + "】已进入流程，不允许删除");
            }
            itemMapper.deleteByOrderId(orderId);
        }
        return orderMapper.deleteBizStockOrderByIds(orderIds);
    }

    @Override
    @Transactional
    public void submitStockOrder(Long orderId)
    {
        BizStockOrder order = mustGet(orderId);
        checkOwner(order);
        if (!STATUS_DRAFT.equals(order.getStatus()) && !STATUS_REJECTED.equals(order.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的出入库单可提交");
        }
        List<BizStockOrderItem> items = itemMapper.selectItemsByOrderId(orderId);
        if (items.isEmpty())
        {
            throw new ServiceException("出入库单明细不能为空");
        }
        if (TYPE_IN.equals(order.getOrderType()))
        {
            // 入库：提交即完成，逐条按移动加权平均更新库存
            for (BizStockOrderItem item : items)
            {
                stockIn(item);
            }
            BizStockOrder update = new BizStockOrder();
            update.setOrderId(orderId);
            update.setStatus(STATUS_FINISHED);
            update.setCompleteTime(new Date());
            update.setHandlerId(SecurityUtils.getUserId());
            update.setUpdateBy(SecurityUtils.getUsername());
            orderMapper.updateBizStockOrder(update);
        }
        else
        {
            // 出库：先校验库存充足，再进入审批流
            for (BizStockOrderItem item : items)
            {
                checkStockEnough(item);
            }
            BizStockOrder update = new BizStockOrder();
            update.setOrderId(orderId);
            update.setStatus(STATUS_AUDITING);
            update.setApplyTime(new Date());
            update.setUpdateBy(SecurityUtils.getUsername());
            orderMapper.updateBizStockOrder(update);
            flowEngineService.startFlow(FlowConstants.STOCK_OUT_APPROVAL, FlowConstants.BIZ_TYPE_STOCK_OUT, orderId,
                    "出库单 " + order.getOrderNo() + " "
                            + (order.getTotalAmount() == null ? "" : order.getTotalAmount() + "元"),
                    order.getTotalAmount());
        }
    }

    @Override
    @Transactional
    public void invalidateStockOrder(Long orderId)
    {
        BizStockOrder order = mustGet(orderId);
        if (STATUS_AUDITING.equals(order.getStatus()))
        {
            throw new ServiceException("审批中的出入库单不可作废，请先撤销审批");
        }
        if (STATUS_FINISHED.equals(order.getStatus()))
        {
            throw new ServiceException("已完成的出入库单不可作废");
        }
        BizStockOrder update = new BizStockOrder();
        update.setOrderId(orderId);
        update.setStatus(STATUS_INVALID);
        update.setUpdateBy(SecurityUtils.getUsername());
        orderMapper.updateBizStockOrder(update);
    }

    // ==================== 审批流回调（出库） ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_STOCK_OUT;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        List<BizStockOrderItem> items = itemMapper.selectItemsByOrderId(bizId);
        for (BizStockOrderItem item : items)
        {
            // 原子扣减，数量不足则回滚整个审批通过事务
            if (inventoryMapper.deductQuantity(item.getMaterialId(), item.getQuantity()) == 0)
            {
                BizMaterial material = materialMapper.selectBizMaterialById(item.getMaterialId());
                throw new ServiceException("物资【" + (material == null ? item.getMaterialId() : material.getMaterialName())
                        + "】库存不足，无法出库");
            }
        }
        BizStockOrder update = new BizStockOrder();
        update.setOrderId(bizId);
        update.setStatus(STATUS_FINISHED);
        update.setCompleteTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        orderMapper.updateBizStockOrder(update);
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        updateStatus(bizId, STATUS_REJECTED);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        updateStatus(bizId, STATUS_DRAFT);
    }

    // ==================== 私有方法 ====================

    /** 入库：移动加权平均更新库存 */
    private void stockIn(BizStockOrderItem item)
    {
        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("入库明细单价不能为空");
        }
        BizInventory inventory = inventoryMapper.selectByMaterialId(item.getMaterialId());
        if (inventory == null)
        {
            inventory = new BizInventory();
            inventory.setMaterialId(item.getMaterialId());
            inventory.setQuantity(item.getQuantity());
            inventory.setAvgPrice(item.getUnitPrice());
            inventoryMapper.insertBizInventory(inventory);
        }
        else
        {
            BigDecimal oldQty = inventory.getQuantity() == null ? BigDecimal.ZERO : inventory.getQuantity();
            BigDecimal oldAvg = inventory.getAvgPrice() == null ? BigDecimal.ZERO : inventory.getAvgPrice();
            BigDecimal newQty = oldQty.add(item.getQuantity());
            // 移动加权平均价 = (原数量*原均价 + 入库数量*入库单价) / 新数量
            BigDecimal newAvg = oldQty.multiply(oldAvg).add(item.getQuantity().multiply(item.getUnitPrice()))
                    .divide(newQty, 4, BigDecimal.ROUND_HALF_UP);
            inventory.setQuantity(newQty);
            inventory.setAvgPrice(newAvg);
            inventoryMapper.updateBizInventory(inventory);
        }
    }

    /** 出库前库存充足性校验 */
    private void checkStockEnough(BizStockOrderItem item)
    {
        BizInventory inventory = inventoryMapper.selectByMaterialId(item.getMaterialId());
        BigDecimal stock = inventory == null || inventory.getQuantity() == null ? BigDecimal.ZERO : inventory.getQuantity();
        if (stock.compareTo(item.getQuantity()) < 0)
        {
            BizMaterial material = materialMapper.selectBizMaterialById(item.getMaterialId());
            throw new ServiceException("物资【" + (material == null ? item.getMaterialId() : material.getMaterialName())
                    + "】库存不足（当前库存：" + stock + "，需出库：" + item.getQuantity() + "）");
        }
    }

    private void saveItems(BizStockOrder order)
    {
        if (order.getItems() == null || order.getItems().isEmpty())
        {
            return;
        }
        for (BizStockOrderItem item : order.getItems())
        {
            item.setItemId(null);
            item.setOrderId(order.getOrderId());
            if (item.getAmount() == null)
            {
                item.setAmount(item.getQuantity().multiply(
                        item.getUnitPrice() == null ? BigDecimal.ZERO : item.getUnitPrice()));
            }
        }
        itemMapper.batchInsert(order.getItems());
    }

    private BigDecimal sumAmount(List<BizStockOrderItem> items)
    {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null)
        {
            for (BizStockOrderItem item : items)
            {
                total = total.add(item.getAmount() == null ? BigDecimal.ZERO : item.getAmount());
            }
        }
        return total;
    }

    private void checkOrder(BizStockOrder order)
    {
        if (StringUtils.isEmpty(order.getOrderType()))
        {
            throw new ServiceException("单据类型不能为空");
        }
        if (order.getItems() == null || order.getItems().isEmpty())
        {
            throw new ServiceException("出入库明细不能为空");
        }
        boolean projectUse = "2".equals(order.getSourceType());
        if (TYPE_OUT.equals(order.getOrderType()) && projectUse && order.getContractId() == null)
        {
            throw new ServiceException("项目领用出库必须关联合同");
        }
        for (BizStockOrderItem item : order.getItems())
        {
            if (item.getMaterialId() == null)
            {
                throw new ServiceException("明细物资不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("明细数量必须大于0");
            }
        }
    }

    private BizStockOrder mustGet(Long orderId)
    {
        BizStockOrder order = orderMapper.selectBizStockOrderById(orderId);
        if (order == null)
        {
            throw new ServiceException("出入库单不存在");
        }
        return order;
    }

    private void checkOwner(BizStockOrder order)
    {
        if (!order.getApplicantId().equals(SecurityUtils.getUserId()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            throw new ServiceException("仅申请人本人可操作该出入库单");
        }
    }

    private void updateStatus(Long orderId, String status)
    {
        BizStockOrder update = new BizStockOrder();
        update.setOrderId(orderId);
        update.setStatus(status);
        update.setUpdateBy(SecurityUtils.getUsername());
        orderMapper.updateBizStockOrder(update);
    }
}
