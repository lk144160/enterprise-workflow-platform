package com.ruoyi.business.stock.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.stock.domain.BizInventory;
import com.ruoyi.business.stock.domain.BizMaterial;
import com.ruoyi.business.stock.mapper.BizInventoryMapper;
import com.ruoyi.business.stock.mapper.BizMaterialMapper;
import com.ruoyi.business.stock.service.IBizMaterialService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 物资档案服务实现
 *
 * @author renovationops
 */
@Service
public class BizMaterialServiceImpl implements IBizMaterialService
{
    @Autowired
    private BizMaterialMapper materialMapper;

    @Autowired
    private BizInventoryMapper inventoryMapper;

    @Override
    public BizMaterial selectBizMaterialById(Long materialId)
    {
        return materialMapper.selectBizMaterialById(materialId);
    }

    @Override
    public List<BizMaterial> selectBizMaterialList(BizMaterial material)
    {
        return materialMapper.selectBizMaterialList(material);
    }

    @Override
    public int insertBizMaterial(BizMaterial material)
    {
        checkMaterial(material);
        if (StringUtils.isNotEmpty(material.getMaterialNo())
                && materialMapper.selectBizMaterialByNo(material.getMaterialNo()) != null)
        {
            throw new ServiceException("物资编号【" + material.getMaterialNo() + "】已存在");
        }
        material.setCreateBy(SecurityUtils.getUsername());
        return materialMapper.insertBizMaterial(material);
    }

    @Override
    public int updateBizMaterial(BizMaterial material)
    {
        checkMaterial(material);
        BizMaterial exist = materialMapper.selectBizMaterialById(material.getMaterialId());
        if (exist == null)
        {
            throw new ServiceException("物资不存在");
        }
        if (StringUtils.isNotEmpty(material.getMaterialNo()) && !material.getMaterialNo().equals(exist.getMaterialNo())
                && materialMapper.selectBizMaterialByNo(material.getMaterialNo()) != null)
        {
            throw new ServiceException("物资编号【" + material.getMaterialNo() + "】已存在");
        }
        material.setUpdateBy(SecurityUtils.getUsername());
        return materialMapper.updateBizMaterial(material);
    }

    @Override
    public int deleteBizMaterialByIds(Long[] materialIds)
    {
        for (Long materialId : materialIds)
        {
            BizInventory inventory = inventoryMapper.selectByMaterialId(materialId);
            if (inventory != null && inventory.getQuantity() != null
                    && inventory.getQuantity().compareTo(java.math.BigDecimal.ZERO) > 0)
            {
                BizMaterial material = materialMapper.selectBizMaterialById(materialId);
                throw new ServiceException("物资【" + (material == null ? materialId : material.getMaterialName())
                        + "】仍有库存结余，不允许删除");
            }
        }
        return materialMapper.deleteBizMaterialByIds(materialIds);
    }

    @Override
    public List<BizInventory> selectInventoryList(BizInventory inventory)
    {
        return inventoryMapper.selectBizInventoryList(inventory);
    }

    @Override
    public List<BizInventory> selectBelowSafetyList()
    {
        return inventoryMapper.selectBelowSafetyList();
    }

    private void checkMaterial(BizMaterial material)
    {
        if (StringUtils.isEmpty(material.getMaterialName()))
        {
            throw new ServiceException("物资名称不能为空");
        }
        if (StringUtils.isEmpty(material.getUnit()))
        {
            throw new ServiceException("计量单位不能为空");
        }
    }
}
