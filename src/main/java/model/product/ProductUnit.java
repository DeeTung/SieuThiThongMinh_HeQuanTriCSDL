package model.product;

import java.math.BigDecimal;

public class ProductUnit {
    private String productId;
    private String unitId;
    private BigDecimal conversionRateToBase;
    private int isBaseUnit;
    private int isDeleted;

    public ProductUnit() {
    }

    public ProductUnit(String productId, String unitId, BigDecimal conversionRateToBase,
            int isBaseUnit, int isDeleted) {
        this.productId = productId;
        this.unitId = unitId;
        this.conversionRateToBase = conversionRateToBase;
        this.isBaseUnit = isBaseUnit;
        this.isDeleted = isDeleted;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public BigDecimal getConversionRateToBase() {
        return conversionRateToBase;
    }

    public void setConversionRateToBase(BigDecimal conversionRateToBase) {
        this.conversionRateToBase = conversionRateToBase;
    }

    public int getIsBaseUnit() {
        return isBaseUnit;
    }

    public void setIsBaseUnit(int isBaseUnit) {
        this.isBaseUnit = isBaseUnit;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
