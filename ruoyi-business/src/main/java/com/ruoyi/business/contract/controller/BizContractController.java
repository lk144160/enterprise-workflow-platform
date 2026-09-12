package com.ruoyi.business.contract.controller;

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
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.contract.domain.BizPaymentRecord;
import com.ruoyi.business.contract.service.IBizContractService;
import com.ruoyi.business.contract.service.IBizPaymentService;
import com.ruoyi.business.quote.domain.BizQuote;
import com.ruoyi.business.quote.mapper.BizQuoteMapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 合同管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/contract")
public class BizContractController extends BaseController
{
    @Autowired
    private IBizContractService contractService;

    @Autowired
    private IBizPaymentService paymentService;

    @Autowired
    private BizQuoteMapper quoteMapper;

    /**
     * 合同列表
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizContract bizContract)
    {
        startPage();
        List<BizContract> list = contractService.selectBizContractList(bizContract);
        return getDataTable(list);
    }

    /**
     * 合同详情（含四期收款计划）
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:query')")
    @GetMapping("/{contractId}")
    public AjaxResult getInfo(@PathVariable Long contractId)
    {
        return success(contractService.selectBizContractById(contractId));
    }

    /**
     * 合同收款记录
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:query')")
    @GetMapping("/{contractId}/payments")
    public AjaxResult payments(@PathVariable Long contractId)
    {
        List<BizPaymentRecord> records = paymentService.selectRecordsByContractId(contractId);
        return success(records);
    }

    /**
     * 客户可选的已通过报价（合同关联合同用）
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:query')")
    @GetMapping("/quotes/{customerId}")
    public AjaxResult approvedQuotes(@PathVariable Long customerId)
    {
        List<BizQuote> quotes = quoteMapper.selectApprovedQuotesByCustomer(customerId);
        return success(quotes);
    }

    /**
     * 新增合同（自动生成四期收款计划）
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:add')")
    @Log(title = "合同", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizContract bizContract)
    {
        return toAjax(contractService.insertBizContract(bizContract));
    }

    /**
     * 修改合同
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:edit')")
    @Log(title = "合同", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizContract bizContract)
    {
        return toAjax(contractService.updateBizContract(bizContract));
    }

    /**
     * 删除合同
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:remove')")
    @Log(title = "合同", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contractIds}")
    public AjaxResult remove(@PathVariable Long[] contractIds)
    {
        return toAjax(contractService.deleteBizContractByIds(contractIds));
    }

    /**
     * 提交审批
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:submit')")
    @Log(title = "合同提交审批", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{contractId}")
    public AjaxResult submit(@PathVariable Long contractId)
    {
        contractService.submitContract(contractId);
        return success();
    }

    /**
     * 完工登记
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:finish')")
    @Log(title = "合同完工登记", businessType = BusinessType.UPDATE)
    @PostMapping("/finish/{contractId}")
    public AjaxResult finish(@PathVariable Long contractId)
    {
        contractService.finishContract(contractId);
        return success();
    }

    /**
     * 归档
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:archive')")
    @Log(title = "合同归档", businessType = BusinessType.UPDATE)
    @PostMapping("/archive/{contractId}")
    public AjaxResult archive(@PathVariable Long contractId)
    {
        contractService.archiveContract(contractId);
        return success();
    }

    /**
     * 终止 body: { contractId, reason }
     */
    @PreAuthorize("@ss.hasPermi('biz:contract:terminate')")
    @Log(title = "合同终止", businessType = BusinessType.UPDATE)
    @PostMapping("/terminate")
    public AjaxResult terminate(@RequestBody BizContract body)
    {
        contractService.terminateContract(body.getContractId(), body.getRemark());
        return success();
    }
}
