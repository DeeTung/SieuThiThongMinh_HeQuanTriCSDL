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

public class Token {

    private String tokenId;
    private String accountId;
    private String tokenValue;
    private Timestamp expiryDate;
    private Timestamp createdAt;
    private int isRevoked;
    private int isDeleted;

    public Token() {
    }

    public Token(String tokenId, String accountId, String tokenValue,
                 Timestamp expiryDate, Timestamp createdAt,
                 int isRevoked, int isDeleted) {
        this.tokenId = tokenId;
        this.accountId = accountId;
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.isRevoked = isRevoked;
        this.isDeleted = isDeleted;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(int isRevoked) {
        this.isRevoked = isRevoked;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
