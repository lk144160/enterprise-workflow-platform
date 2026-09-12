package com.ruoyi.business.design.service;

import java.util.List;
import com.ruoyi.business.design.domain.BizDrawing;

/**
 * 图纸服务接口
 *
 * @author renovationops
 */
public interface IBizDrawingService
{
    public BizDrawing selectBizDrawingById(Long drawingId);

    public List<BizDrawing> selectBizDrawingList(BizDrawing drawing);

    /** 上传新版本图纸（同客户/合同维度下同名自动版本+1） */
    public int uploadDrawing(BizDrawing drawing);

    /** 确认图纸（总监/分配人） */
    public void confirmDrawing(Long drawingId);

    public int deleteBizDrawingByIds(Long[] drawingIds);
}
