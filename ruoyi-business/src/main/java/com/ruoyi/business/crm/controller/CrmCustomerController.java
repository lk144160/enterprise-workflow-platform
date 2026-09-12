package com.ruoyi.business.crm.controller;

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
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.domain.CrmFollowRecord;
import com.ruoyi.business.crm.service.ICrmCustomerService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 客户线索管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/crm/customer")
public class CrmCustomerController extends BaseController
{
    @Autowired
    private ICrmCustomerService customerService;

    /**
     * 线索列表（数据权限：本部门/本人）
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCustomer crmCustomer)
    {
        startPage();
        List<CrmCustomer> list = customerService.selectCrmCustomerList(crmCustomer);
        return getDataTable(list);
    }

    /**
     * 线索详情（含跟进记录）
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:query')")
    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId)
    {
        return success(customerService.selectCrmCustomerById(customerId));
    }

    /**
     * 新增线索
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:add')")
    @Log(title = "客户线索", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCustomer crmCustomer)
    {
        return toAjax(customerService.insertCrmCustomer(crmCustomer));
    }

    /**
     * 修改线索
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:edit')")
    @Log(title = "客户线索", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCustomer crmCustomer)
    {
        return toAjax(customerService.updateCrmCustomer(crmCustomer));
    }

    /**
     * 删除线索
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:remove')")
    @Log(title = "客户线索", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        return toAjax(customerService.deleteCrmCustomerByIds(customerIds));
    }

    /**
     * 新增跟进记录（联动更新线索跟进信息）
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:follow')")
    @Log(title = "线索跟进", businessType = BusinessType.INSERT)
    @PostMapping("/follow")
    public AjaxResult follow(@RequestBody CrmFollowRecord record)
    {
        return toAjax(customerService.addFollowRecord(record));
    }

    /**
     * 跟进记录列表（客情-跟进动态）
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:list')")
    @GetMapping("/follow/list")
    public TableDataInfo followList(CrmFollowRecord record)
    {
        startPage();
        List<CrmFollowRecord> list = customerService.selectFollowRecordList(record);
        return getDataTable(list);
    }

    /**
     * 流失登记 body: { customerId, lossReason }
     */
    @PreAuthorize("@ss.hasPermi('crm:customer:edit')")
    @Log(title = "线索流失登记", businessType = BusinessType.UPDATE)
    @PostMapping("/loss")
    public AjaxResult loss(@RequestBody CrmCustomer body)
    {
        return toAjax(customerService.markLoss(body.getCustomerId(), body.getLossReason()));
    }
}
