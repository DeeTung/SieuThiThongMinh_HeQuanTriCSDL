/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.promotion;

import java.sql.Date;

/**
 *
 * @author nguye
 */
public class PromotionCampaign {
    private String campaignId, campaignName, description;
    private Date startDate, endDate;
    private int isDeleted;

    public PromotionCampaign() {}

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String id) { this.campaignId = id; }

    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String name) { this.campaignName = name; }

    public String getDescription() { return description; }
    public void setDescription(String desc) { this.description = desc; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date date) { this.startDate = date; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date date) { this.endDate = date; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}
