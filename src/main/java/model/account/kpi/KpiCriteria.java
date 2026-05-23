/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.account.kpi;

/**
 *
 * @author nguye
 */
import java.sql.Timestamp;
import java.math.BigDecimal;

public class KpiCriteria {

    private String kpiId;
    private String criteriaName;
    private String criteriaType;
    private BigDecimal weight;
    private Timestamp recordedTime;
    private BigDecimal minimumTarget;
    private int isDeleted;

    public KpiCriteria() {
    }

    public KpiCriteria(String kpiId, String criteriaName, String criteriaType,
                       BigDecimal weight, Timestamp recordedTime,
                       BigDecimal minimumTarget, int isDeleted) {
        this.kpiId = kpiId;
        this.criteriaName = criteriaName;
        this.criteriaType = criteriaType;
        this.weight = weight;
        this.recordedTime = recordedTime;
        this.minimumTarget = minimumTarget;
        this.isDeleted = isDeleted;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Timestamp getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(Timestamp recordedTime) {
        this.recordedTime = recordedTime;
    }

    public BigDecimal getMinimumTarget() {
        return minimumTarget;
    }

    public void setMinimumTarget(BigDecimal minimumTarget) {
        this.minimumTarget = minimumTarget;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}