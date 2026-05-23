/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.promotion;

/**
 *
 * @author nguye
 */
public class Promotion {
    private String promotionId, promotionName, campaignId, applicationCondition, status, orderDetailId;
    private double discountAmount;
    private int isDeleted;

    public Promotion() {}

    // Getter & Setter (Thay thế các hàm throw cũ)
    public String getPromotionId() { return promotionId; }
    public void setPromotionId(String promotionId) { this.promotionId = promotionId; }

    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getApplicationCondition() { return applicationCondition; }
    public void setApplicationCondition(String cond) { this.applicationCondition = cond; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(String id) { this.orderDetailId = id; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double amount) { this.discountAmount = amount; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}