package model.product;

public class Unit {
    private String unitId;
    private String unitName;
    private int isDeleted;

    public Unit() {
    }

    public Unit(String unitId, String unitName, int isDeleted) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.isDeleted = isDeleted;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
