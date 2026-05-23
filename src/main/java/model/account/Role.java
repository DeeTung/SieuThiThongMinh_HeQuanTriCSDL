/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.account;

/**
 *
 * @author nguye
 */
import java.sql.Timestamp;

public class Role {
    private String roleId;
    private String functionId;
    private int canView;
    private int canAdd;
    private int canEdit;
    private int canDelete;
    private int canExport;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int isDeleted;

    public Role() {
    }

    public Role(String roleId, String functionId, int canView, int canAdd, int canEdit,
                int canDelete, int canExport, Timestamp createdAt, Timestamp updatedAt, int isDeleted) {
        this.roleId = roleId;
        this.functionId = functionId;
        this.canView = canView;
        this.canAdd = canAdd;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.canExport = canExport;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public int getCanView() {
        return canView;
    }

    public void setCanView(int canView) {
        this.canView = canView;
    }

    public int getCanAdd() {
        return canAdd;
    }

    public void setCanAdd(int canAdd) {
        this.canAdd = canAdd;
    }

    public int getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(int canEdit) {
        this.canEdit = canEdit;
    }

    public int getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(int canDelete) {
        this.canDelete = canDelete;
    }

    public int getCanExport() {
        return canExport;
    }

    public void setCanExport(int canExport) {
        this.canExport = canExport;
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