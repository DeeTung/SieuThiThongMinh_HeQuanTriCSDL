package model.delivery;

public class HomeDelivery {
    private String deliveryId;
    private String deliveryAddress;
    private double shippingFee;
    private String recipientPhone;
    private int isDeleted;

    public HomeDelivery() {}

    public HomeDelivery(String deliveryId, String deliveryAddress, double shippingFee, String recipientPhone, int isDeleted) {
        this.deliveryId = deliveryId;
        this.deliveryAddress = deliveryAddress;
        this.shippingFee = shippingFee;
        this.recipientPhone = recipientPhone;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}