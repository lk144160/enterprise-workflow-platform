package com.ruoyi.business.stock.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.stock.domain.BizStockOrder;
import com.ruoyi.business.stock.service.IBizStockOrderService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 出入库管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/stock")
public class BizStockOrderController extends BaseController
{
    @Autowired
    private IBizStockOrderService stockOrderService;

    /**
     * 出入库单列表
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizStockOrder order)
    {
        startPage();
        List<BizStockOrder> list = stockOrderService.selectBizStockOrderList(order);
        return getDataTable(list);
    }

    /**
     * 出入库单详情（含明细）
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(stockOrderService.selectBizStockOrderById(orderId));
    }

    /**
     * 新增出入库单（入库登记/出库申请，保存为草稿）
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:in')")
    @Log(title = "出入库单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizStockOrder order)
    {
        return toAjax(stockOrderService.insertBizStockOrder(order));
    }

    /**
     * 修改出入库单（仅草稿/已驳回）
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:in')")
    @Log(title = "出入库单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizStockOrder order)
    {
        return toAjax(stockOrderService.updateBizStockOrder(order));
    }

    /**
     * 删除出入库单（仅草稿/已驳回）
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:in')")
    @Log(title = "出入库单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(stockOrderService.deleteBizStockOrderByIds(orderIds));
    }

    /**
     * 提交：入库直接完成并更新库存；出库进入审批流
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:apply')")
    @Log(title = "出入库单提交", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{orderId}")
    public AjaxResult submit(@PathVariable Long orderId)
    {
        stockOrderService.submitStockOrder(orderId);
        return success();
    }

    /**
     * 作废
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:in')")
    @Log(title = "出入库单作废", businessType = BusinessType.UPDATE)
    @PostMapping("/invalidate/{orderId}")
    public AjaxResult invalidate(@PathVariable Long orderId)
    {
        stockOrderService.invalidateStockOrder(orderId);
        return success();
    }
}
