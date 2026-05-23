package model.payment;

import java.sql.Timestamp;

public class BankTransferPayment {
    private String paymentMethodId;
    private String bankName;
    private Timestamp transactionTime;
    private String senderAccountNumber;
    private String qrCode;
    private int isDeleted;

    public BankTransferPayment() {}

    public BankTransferPayment(String paymentMethodId, String bankName, Timestamp transactionTime, 
                               String senderAccountNumber, String qrCode, int isDeleted) {
        this.paymentMethodId = paymentMethodId;
        this.bankName = bankName;
        this.transactionTime = transactionTime;
        this.senderAccountNumber = senderAccountNumber;
        this.qrCode = qrCode;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Timestamp transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}