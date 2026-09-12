package com.ruoyi.business.stock.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物资档案对象 biz_material
 *
 * @author renovationops
 */
public class BizMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 物资ID */
    private Long materialId;

    /** 物资编号 */
    private String materialNo;

    /** 物资名称 */
    private String materialName;

    /** 规格型号 */
    private String spec;

    /** 计量单位（字典 biz_measure_unit） */
    private String unit;

    /** 分类（字典 biz_material_category） */
    private String category;

    /** 安全库存 */
    private BigDecimal safetyStock;

    /** 参考单价 */
    private BigDecimal refPrice;

    /** 状态（0正常 1停用） */
    private String status;

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialNo() { return materialNo; }
    public void setMaterialNo(String materialNo) { this.materialNo = materialNo; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public BigDecimal getRefPrice() { return refPrice; }
    public void setRefPrice(BigDecimal refPrice) { this.refPrice = refPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
