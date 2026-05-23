package model.account;

import java.sql.Timestamp;

public class AccountAssignRoleGroup {
    private String accountId;
    private String roleGroupId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int isDeleted;

    public AccountAssignRoleGroup() {}

    public AccountAssignRoleGroup(String accountId, String roleGroupId, Timestamp createdAt, 
                                  Timestamp updatedAt, int isDeleted) {
        this.accountId = accountId;
        this.roleGroupId = roleGroupId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters...
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRoleGroupId() {
        return roleGroupId;
    }

    public void setRoleGroupId(String roleGroupId) {
        this.roleGroupId = roleGroupId;
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