package com.ruoyi.business.stock.service;

import java.util.List;
import com.ruoyi.business.stock.domain.BizInventory;
import com.ruoyi.business.stock.domain.BizMaterial;

/**
 * 物资档案Service接口
 *
 * @author renovationops
 */
public interface IBizMaterialService
{
    public BizMaterial selectBizMaterialById(Long materialId);

    public List<BizMaterial> selectBizMaterialList(BizMaterial material);

    public int insertBizMaterial(BizMaterial material);

    public int updateBizMaterial(BizMaterial material);

    public int deleteBizMaterialByIds(Long[] materialIds);

    /** 库存台账列表 */
    public List<BizInventory> selectInventoryList(BizInventory inventory);

    /** 低于安全库存预警列表 */
    public List<BizInventory> selectBelowSafetyList();
}
