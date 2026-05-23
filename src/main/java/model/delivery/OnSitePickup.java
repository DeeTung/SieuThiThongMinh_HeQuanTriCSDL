package model.delivery;

public class OnSitePickup {
    private String deliveryId;
    private String counterPosition;
    private int isDeleted;

    public OnSitePickup() {}

    public OnSitePickup(String deliveryId, String counterPosition, int isDeleted) {
        this.deliveryId = deliveryId;
        this.counterPosition = counterPosition;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getCounterPosition() {
        return counterPosition;
    }

    public void setCounterPosition(String counterPosition) {
        this.counterPosition = counterPosition;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}