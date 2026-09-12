package com.ruoyi.business.stock.mapper;

import java.util.List;
import com.ruoyi.business.stock.domain.BizMaterial;

/**
 * 物资档案 Mapper 接口
 *
 * @author renovationops
 */
public interface BizMaterialMapper
{
    public BizMaterial selectBizMaterialById(Long materialId);

    public BizMaterial selectBizMaterialByNo(String materialNo);

    public List<BizMaterial> selectBizMaterialList(BizMaterial material);

    public int insertBizMaterial(BizMaterial material);

    public int updateBizMaterial(BizMaterial material);

    public int deleteBizMaterialByIds(Long[] materialIds);
}
