package com.ruoyi.business.stock.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存对象 biz_inventory
 *
 * @author renovationops
 */
public class BizInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 物资ID */
    private Long materialId;

    /** 库存数量 */
    private BigDecimal quantity;

    /** 移动加权平均价 */
    private BigDecimal avgPrice;

    /** 物资编号（展示） */
    private String materialNo;

    /** 物资名称（展示） */
    private String materialName;

    /** 规格型号（展示） */
    private String spec;

    /** 计量单位（展示） */
    private String unit;

    /** 安全库存（展示） */
    private BigDecimal safetyStock;

    /** 库存金额（展示，数量*均价） */
    private BigDecimal stockAmount;

    /** 是否低于安全库存（展示：1是 0否） */
    private String belowSafety;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAvgPrice() { return avgPrice; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
    public String getMaterialNo() { return materialNo; }
    public void setMaterialNo(String materialNo) { this.materialNo = materialNo; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public BigDecimal getStockAmount() { return stockAmount; }
    public void setStockAmount(BigDecimal stockAmount) { this.stockAmount = stockAmount; }
    public String getBelowSafety() { return belowSafety; }
    public void setBelowSafety(String belowSafety) { this.belowSafety = belowSafety; }
}
