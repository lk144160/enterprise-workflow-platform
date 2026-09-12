package com.ruoyi.business.design.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.design.domain.BizDrawing;

/**
 * 图纸 Mapper 接口
 *
 * @author renovationops
 */
public interface BizDrawingMapper
{
    public BizDrawing selectBizDrawingById(Long drawingId);

    public List<BizDrawing> selectBizDrawingList(BizDrawing drawing);

    /** 查询同客户/合同维度下同名图纸当前版本号最大值（版本递增用） */
    public Integer selectMaxVersion(BizDrawing drawing);

    /** 同名图纸旧版本置为非当前 */
    public int markOldVersionsNotCurrent(BizDrawing drawing);

    public int insertBizDrawing(BizDrawing drawing);

    public int updateBizDrawing(BizDrawing drawing);

    public int deleteBizDrawingByIds(Long[] drawingIds);
}
