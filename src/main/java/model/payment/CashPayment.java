package model.payment;

public class CashPayment {
    private String paymentMethodId;
    private int isDeleted;

    public CashPayment() {}

    public CashPayment(String paymentMethodId, int isDeleted) {
        this.paymentMethodId = paymentMethodId;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}