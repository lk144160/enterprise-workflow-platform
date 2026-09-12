package com.ruoyi.business.design.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.design.domain.BizDrawing;
import com.ruoyi.business.design.mapper.BizDrawingMapper;
import com.ruoyi.business.design.service.IBizDrawingService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 图纸服务实现（版本管理）
 *
 * @author renovationops
 */
@Service
public class BizDrawingServiceImpl implements IBizDrawingService
{
    /** 图纸状态 */
    public static final String STATUS_WAIT_CONFIRM = "0";
    public static final String STATUS_CONFIRMED = "1";
    public static final String STATUS_INVALID = "2";

    @Autowired
    private BizDrawingMapper drawingMapper;

    @Override
    public BizDrawing selectBizDrawingById(Long drawingId)
    {
        return drawingMapper.selectBizDrawingById(drawingId);
    }

    @Override
    public List<BizDrawing> selectBizDrawingList(BizDrawing drawing)
    {
        return drawingMapper.selectBizDrawingList(drawing);
    }

    @Override
    @Transactional
    public int uploadDrawing(BizDrawing drawing)
    {
        if (StringUtils.isEmpty(drawing.getDrawingName()))
        {
            throw new ServiceException("图纸名称不能为空");
        }
        if (StringUtils.isEmpty(drawing.getFileUrl()))
        {
            throw new ServiceException("图纸文件地址不能为空");
        }
        // 同客户/合同维度下同名图纸版本递增，旧版本置为非当前
        Integer maxVersion = drawingMapper.selectMaxVersion(drawing);
        int nextVersion = (maxVersion == null ? 0 : maxVersion) + 1;
        if (nextVersion > 1)
        {
            drawingMapper.markOldVersionsNotCurrent(drawing);
        }
        drawing.setVersion(nextVersion);
        drawing.setIsCurrent("1");
        drawing.setStatus(STATUS_WAIT_CONFIRM);
        drawing.setCreateBy(SecurityUtils.getUsername());
        return drawingMapper.insertBizDrawing(drawing);
    }

    @Override
    @Transactional
    public void confirmDrawing(Long drawingId)
    {
        BizDrawing drawing = drawingMapper.selectBizDrawingById(drawingId);
        if (drawing == null)
        {
            throw new ServiceException("图纸不存在");
        }
        if (!"1".equals(drawing.getIsCurrent()))
        {
            throw new ServiceException("仅当前版本图纸可确认");
        }
        if (!STATUS_WAIT_CONFIRM.equals(drawing.getStatus()))
        {
            throw new ServiceException("图纸当前状态不可确认");
        }
        BizDrawing update = new BizDrawing();
        update.setDrawingId(drawingId);
        update.setStatus(STATUS_CONFIRMED);
        update.setConfirmUserId(SecurityUtils.getUserId());
        update.setConfirmTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        drawingMapper.updateBizDrawing(update);
    }

    @Override
    @Transactional
    public int deleteBizDrawingByIds(Long[] drawingIds)
    {
        for (Long drawingId : drawingIds)
        {
            BizDrawing drawing = drawingMapper.selectBizDrawingById(drawingId);
            if (drawing != null && STATUS_CONFIRMED.equals(drawing.getStatus()))
            {
                throw new ServiceException("已确认的图纸不允许删除");
            }
        }
        return drawingMapper.deleteBizDrawingByIds(drawingIds);
    }
}
