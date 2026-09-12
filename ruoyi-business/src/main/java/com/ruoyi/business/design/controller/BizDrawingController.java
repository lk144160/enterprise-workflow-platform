package com.ruoyi.business.design.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.design.domain.BizDrawing;
import com.ruoyi.business.design.service.IBizDrawingService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 图纸管理（版本）
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/drawing")
public class BizDrawingController extends BaseController
{
    @Autowired
    private IBizDrawingService drawingService;

    /**
     * 图纸列表
     */
    @PreAuthorize("@ss.hasPermi('biz:drawing:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDrawing drawing)
    {
        startPage();
        List<BizDrawing> list = drawingService.selectBizDrawingList(drawing);
        return getDataTable(list);
    }

    /**
     * 上传图纸（同客户/合同维度下同名自动版本+1）
     */
    @PreAuthorize("@ss.hasPermi('biz:drawing:add')")
    @Log(title = "图纸上传", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult upload(@RequestBody BizDrawing drawing)
    {
        return toAjax(drawingService.uploadDrawing(drawing));
    }

    /**
     * 确认图纸
     */
    @PreAuthorize("@ss.hasPermi('biz:drawing:confirm')")
    @Log(title = "图纸确认", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm/{drawingId}")
    public AjaxResult confirm(@PathVariable Long drawingId)
    {
        drawingService.confirmDrawing(drawingId);
        return success();
    }

    /**
     * 删除图纸（已确认不可删）
     */
    @PreAuthorize("@ss.hasPermi('biz:drawing:query')")
    @Log(title = "图纸", businessType = BusinessType.DELETE)
    @DeleteMapping("/{drawingIds}")
    public AjaxResult remove(@PathVariable Long[] drawingIds)
    {
        return toAjax(drawingService.deleteBizDrawingByIds(drawingIds));
    }
}
