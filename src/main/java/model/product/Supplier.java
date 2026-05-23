package model.product;

public class Supplier {
    private String supplierId;
    private String supplierName;
    private String email;
    private String address;
    private String phoneNumber;
    private int isDeleted;

    // Constructor rỗng
    public Supplier() {
    }

    // Constructor đầy đủ tham số
    public Supplier(String supplierId, String supplierName, String email, String address, String phoneNumber, int isDeleted) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isDeleted = isDeleted;
    }

    // Getter và Setter
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}