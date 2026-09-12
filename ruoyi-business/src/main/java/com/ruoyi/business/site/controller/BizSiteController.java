package com.ruoyi.business.site.controller;

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
import com.ruoyi.business.site.domain.BizProgressStage;
import com.ruoyi.business.site.domain.BizSite;
import com.ruoyi.business.site.service.IBizSiteService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 施工进度管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/site")
public class BizSiteController extends BaseController
{
    @Autowired
    private IBizSiteService siteService;

    /**
     * 工地列表
     */
    @PreAuthorize("@ss.hasPermi('biz:site:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizSite site)
    {
        startPage();
        List<BizSite> list = siteService.selectBizSiteList(site);
        return getDataTable(list);
    }

    /**
     * 可开工的合同（已生效且未建工地）
     */
    @PreAuthorize("@ss.hasPermi('biz:site:query')")
    @GetMapping("/contracts")
    public AjaxResult contracts()
    {
        return success(siteService.selectAvailableContracts());
    }

    /**
     * 工地详情（含阶段与现场照片）
     */
    @PreAuthorize("@ss.hasPermi('biz:site:query')")
    @GetMapping("/{siteId}")
    public AjaxResult getInfo(@PathVariable Long siteId)
    {
        return success(siteService.selectBizSiteById(siteId));
    }

    /**
     * 新开工地（自动铺排标准工序）
     */
    @PreAuthorize("@ss.hasPermi('biz:site:add')")
    @Log(title = "施工进度", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizSite site)
    {
        return toAjax(siteService.insertBizSite(site));
    }

    /**
     * 修改工地信息
     */
    @PreAuthorize("@ss.hasPermi('biz:site:edit')")
    @Log(title = "施工进度", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizSite site)
    {
        return toAjax(siteService.updateBizSite(site));
    }

    /**
     * 删除工地
     */
    @PreAuthorize("@ss.hasPermi('biz:site:remove')")
    @Log(title = "施工进度", businessType = BusinessType.DELETE)
    @DeleteMapping("/{siteIds}")
    public AjaxResult remove(@PathVariable Long[] siteIds)
    {
        return toAjax(siteService.deleteBizSiteByIds(siteIds));
    }

    /**
     * 阶段开始施工
     */
    @PreAuthorize("@ss.hasPermi('biz:site:advance')")
    @Log(title = "施工阶段开始", businessType = BusinessType.UPDATE)
    @PostMapping("/stage/{stageId}/start")
    public AjaxResult startStage(@PathVariable Long stageId)
    {
        return toAjax(siteService.startStage(stageId));
    }

    /**
     * 阶段完工
     */
    @PreAuthorize("@ss.hasPermi('biz:site:advance')")
    @Log(title = "施工阶段完工", businessType = BusinessType.UPDATE)
    @PostMapping("/stage/{stageId}/complete")
    public AjaxResult completeStage(@PathVariable Long stageId)
    {
        return toAjax(siteService.completeStage(stageId));
    }

    /**
     * 调整阶段计划（步骤名称/计划起止/备注）
     */
    @PreAuthorize("@ss.hasPermi('biz:site:edit')")
    @Log(title = "施工阶段计划调整", businessType = BusinessType.UPDATE)
    @PutMapping("/stage")
    public AjaxResult updateStage(@RequestBody BizProgressStage stage)
    {
        return toAjax(siteService.updateStage(stage));
    }

    /**
     * 新增自定义施工步骤
     */
    @PreAuthorize("@ss.hasPermi('biz:site:edit')")
    @Log(title = "新增施工步骤", businessType = BusinessType.INSERT)
    @PostMapping("/stage")
    public AjaxResult addStage(@RequestBody BizProgressStage stage)
    {
        return toAjax(siteService.addStage(stage));
    }

    /**
     * 删除施工步骤（仅未开始的可删）
     */
    @PreAuthorize("@ss.hasPermi('biz:site:edit')")
    @Log(title = "删除施工步骤", businessType = BusinessType.DELETE)
    @DeleteMapping("/stage/{stageId}")
    public AjaxResult deleteStage(@PathVariable Long stageId)
    {
        return toAjax(siteService.deleteStage(stageId));
    }
}
