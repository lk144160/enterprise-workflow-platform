package com.ruoyi.business.stock.controller;

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
import com.ruoyi.business.stock.domain.BizInventory;
import com.ruoyi.business.stock.domain.BizMaterial;
import com.ruoyi.business.stock.service.IBizMaterialService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 物资台账管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/material")
public class BizMaterialController extends BaseController
{
    @Autowired
    private IBizMaterialService materialService;

    /**
     * 物资档案列表
     */
    @PreAuthorize("@ss.hasPermi('biz:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMaterial material)
    {
        startPage();
        List<BizMaterial> list = materialService.selectBizMaterialList(material);
        return getDataTable(list);
    }

    /**
     * 物资详情
     */
    @PreAuthorize("@ss.hasPermi('biz:material:query')")
    @GetMapping("/{materialId}")
    public AjaxResult getInfo(@PathVariable Long materialId)
    {
        return success(materialService.selectBizMaterialById(materialId));
    }

    /**
     * 新增物资
     */
    @PreAuthorize("@ss.hasPermi('biz:material:add')")
    @Log(title = "物资档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizMaterial material)
    {
        return toAjax(materialService.insertBizMaterial(material));
    }

    /**
     * 修改物资
     */
    @PreAuthorize("@ss.hasPermi('biz:material:edit')")
    @Log(title = "物资档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizMaterial material)
    {
        return toAjax(materialService.updateBizMaterial(material));
    }

    /**
     * 删除物资（有库存结余不允许删除）
     */
    @PreAuthorize("@ss.hasPermi('biz:material:remove')")
    @Log(title = "物资档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{materialIds}")
    public AjaxResult remove(@PathVariable Long[] materialIds)
    {
        return toAjax(materialService.deleteBizMaterialByIds(materialIds));
    }

    /**
     * 导出物资档案
     */
    @PreAuthorize("@ss.hasPermi('biz:material:export')")
    @Log(title = "物资档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizMaterial material)
    {
        List<BizMaterial> list = materialService.selectBizMaterialList(material);
        ExcelUtil<BizMaterial> util = new ExcelUtil<BizMaterial>(BizMaterial.class);
        util.exportExcel(response, list, "物资档案数据");
    }

    /**
     * 库存台账列表
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:inventory')")
    @GetMapping("/inventory/list")
    public TableDataInfo inventoryList(BizInventory inventory)
    {
        startPage();
        List<BizInventory> list = materialService.selectInventoryList(inventory);
        return getDataTable(list);
    }

    /**
     * 低于安全库存预警
     */
    @PreAuthorize("@ss.hasPermi('biz:stock:inventory')")
    @GetMapping("/inventory/belowSafety")
    public TableDataInfo belowSafety()
    {
        startPage();
        List<BizInventory> list = materialService.selectBelowSafetyList();
        return getDataTable(list);
    }
}
