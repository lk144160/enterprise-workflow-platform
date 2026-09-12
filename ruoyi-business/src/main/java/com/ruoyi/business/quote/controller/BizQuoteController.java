package com.ruoyi.business.quote.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.business.quote.domain.BizQuote;
import com.ruoyi.business.quote.service.IBizQuoteService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 报价管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/quote")
public class BizQuoteController extends BaseController
{
    @Autowired
    private IBizQuoteService quoteService;

    /**
     * 报价单列表
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizQuote bizQuote)
    {
        startPage();
        List<BizQuote> list = quoteService.selectBizQuoteList(bizQuote);
        return getDataTable(list);
    }

    /**
     * 导出报价单
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:export')")
    @Log(title = "报价单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizQuote bizQuote)
    {
        List<BizQuote> list = quoteService.selectBizQuoteList(bizQuote);
        ExcelUtil<BizQuote> util = new ExcelUtil<BizQuote>(BizQuote.class);
        util.exportExcel(response, list, "报价单数据");
    }

    /**
     * 报价单详情（含明细）
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:query')")
    @GetMapping("/{quoteId}")
    public AjaxResult getInfo(@PathVariable Long quoteId)
    {
        return success(quoteService.selectBizQuoteById(quoteId));
    }

    /**
     * 新增报价单
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:add')")
    @Log(title = "报价单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizQuote bizQuote)
    {
        return toAjax(quoteService.insertBizQuote(bizQuote));
    }

    /**
     * 修改报价单
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:edit')")
    @Log(title = "报价单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizQuote bizQuote)
    {
        return toAjax(quoteService.updateBizQuote(bizQuote));
    }

    /**
     * 删除报价单
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:remove')")
    @Log(title = "报价单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{quoteIds}")
    public AjaxResult remove(@PathVariable Long[] quoteIds)
    {
        return toAjax(quoteService.deleteBizQuoteByIds(quoteIds));
    }

    /**
     * 提交审批
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:submit')")
    @Log(title = "报价单提交审批", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{quoteId}")
    public AjaxResult submit(@PathVariable Long quoteId)
    {
        quoteService.submitQuote(quoteId);
        return success();
    }

    /**
     * 撤销审批
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:cancel')")
    @Log(title = "报价单撤销审批", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{quoteId}")
    public AjaxResult cancel(@PathVariable Long quoteId)
    {
        quoteService.cancelQuote(quoteId);
        return success();
    }

    /**
     * 作废报价单
     */
    @PreAuthorize("@ss.hasPermi('biz:quote:cancel')")
    @Log(title = "报价单作废", businessType = BusinessType.UPDATE)
    @PostMapping("/invalidate/{quoteId}")
    public AjaxResult invalidate(@PathVariable Long quoteId)
    {
        quoteService.invalidateQuote(quoteId);
        return success();
    }
}
