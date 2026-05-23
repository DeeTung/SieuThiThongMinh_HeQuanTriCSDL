package model.product;

public class Category {
    private String categoryId;
    private String categoryName;
    private String description;
    private int isDeleted;

    // Constructor rỗng
    public Category() {
    }

    // Constructor đầy đủ
    public Category(String categoryId, String categoryName, String description, int isDeleted) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.isDeleted = isDeleted;
    }

    // Các hàm Getters và Setters
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}