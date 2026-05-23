package model.product;

public class Store {
    private String storeId;
    private String email;
    private String address;
    private String phoneNumber;
    private int isDeleted;

    public Store() {
    }

    public Store(String storeId, String email, String address, String phoneNumber, int isDeleted) {
        this.storeId = storeId;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isDeleted = isDeleted;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getStoreName() {
    return this.address; 
}
    
    
}