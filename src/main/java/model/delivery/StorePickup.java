package model.delivery;

import java.sql.Timestamp;

public class StorePickup {
    private String deliveryId;
    private String lockerId;
    private Timestamp pickupAppointment;
    private int isDeleted;

    public StorePickup() {}

    public StorePickup(String deliveryId, String lockerId, Timestamp pickupAppointment, int isDeleted) {
        this.deliveryId = deliveryId;
        this.lockerId = lockerId;
        this.pickupAppointment = pickupAppointment;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getLockerId() {
        return lockerId;
    }

    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }

    public Timestamp getPickupAppointment() {
        return pickupAppointment;
    }

    public void setPickupAppointment(Timestamp pickupAppointment) {
        this.pickupAppointment = pickupAppointment;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}