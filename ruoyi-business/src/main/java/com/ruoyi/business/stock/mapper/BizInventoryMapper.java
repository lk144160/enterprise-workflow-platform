package com.ruoyi.business.stock.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.stock.domain.BizInventory;

/**
 * 库存 Mapper 接口
 *
 * @author renovationops
 */
public interface BizInventoryMapper
{
    public BizInventory selectByMaterialId(Long materialId);

    public List<BizInventory> selectBizInventoryList(BizInventory inventory);

    /** 低于安全库存列表 */
    public List<BizInventory> selectBelowSafetyList();

    public int insertBizInventory(BizInventory inventory);

    /** 乐观锁式扣减/回加数量与均价更新 */
    public int updateBizInventory(BizInventory inventory);

    /** 原子扣减库存（数量不足返回0，防超扣） */
    public int deductQuantity(@Param("materialId") Long materialId, @Param("quantity") java.math.BigDecimal quantity);
}
