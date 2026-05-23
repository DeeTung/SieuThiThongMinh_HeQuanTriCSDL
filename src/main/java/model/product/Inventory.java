package model.product;

import java.sql.Date; // Nhớ import đúng java.sql.Date

public class Inventory {
    private String productId;
    private String storeId;
    private int quantity;
    private String unit;
    private Date lastUpdated;
    private int isDeleted;

    // Constructor rỗng
    public Inventory() {
    }

    // Constructor đầy đủ
    public Inventory(String productId, String storeId, int quantity, String unit, Date lastUpdated, int isDeleted) {
        this.productId = productId;
        this.storeId = storeId;
        this.quantity = quantity;
        this.unit = unit;
        this.lastUpdated = lastUpdated;
        this.isDeleted = isDeleted;
    }

    // Getter và Setter
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}