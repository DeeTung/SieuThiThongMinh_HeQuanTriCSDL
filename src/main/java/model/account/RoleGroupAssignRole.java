package model.account;

import java.sql.Timestamp;

public class RoleGroupAssignRole {
    private String roleGroupId;
    private String roleId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int isDeleted;

    public RoleGroupAssignRole() {}

    public RoleGroupAssignRole(String roleGroupId, String roleId, Timestamp createdAt, 
                               Timestamp updatedAt, int isDeleted) {
        this.roleGroupId = roleGroupId;
        this.roleId = roleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...

    public String getRoleGroupId() {
        return roleGroupId;
    }

    public void setRoleGroupId(String roleGroupId) {
        this.roleGroupId = roleGroupId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}