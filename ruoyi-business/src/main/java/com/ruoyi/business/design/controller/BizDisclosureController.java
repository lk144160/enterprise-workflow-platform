package com.ruoyi.business.design.controller;

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
import com.ruoyi.business.design.domain.BizDisclosure;
import com.ruoyi.business.design.service.IBizDisclosureService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 技术交底管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/disclosure")
public class BizDisclosureController extends BaseController
{
    @Autowired
    private IBizDisclosureService disclosureService;

    /**
     * 交底单列表
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDisclosure disclosure)
    {
        startPage();
        List<BizDisclosure> list = disclosureService.selectBizDisclosureList(disclosure);
        return getDataTable(list);
    }

    /**
     * 交底单详情
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:query')")
    @GetMapping("/{disclosureId}")
    public AjaxResult getInfo(@PathVariable Long disclosureId)
    {
        return success(disclosureService.selectBizDisclosureById(disclosureId));
    }

    /**
     * 新增交底单
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:add')")
    @Log(title = "技术交底", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizDisclosure disclosure)
    {
        return toAjax(disclosureService.insertBizDisclosure(disclosure));
    }

    /**
     * 修改交底单
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:edit')")
    @Log(title = "技术交底", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizDisclosure disclosure)
    {
        return toAjax(disclosureService.updateBizDisclosure(disclosure));
    }

    /**
     * 删除交底单
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:remove')")
    @Log(title = "技术交底", businessType = BusinessType.DELETE)
    @DeleteMapping("/{disclosureIds}")
    public AjaxResult remove(@PathVariable Long[] disclosureIds)
    {
        return toAjax(disclosureService.deleteBizDisclosureByIds(disclosureIds));
    }

    /**
     * 提交审批
     */
    @PreAuthorize("@ss.hasPermi('biz:disclosure:submit')")
    @Log(title = "技术交底提交审批", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{disclosureId}")
    public AjaxResult submit(@PathVariable Long disclosureId)
    {
        disclosureService.submitDisclosure(disclosureId);
        return success();
    }
}
