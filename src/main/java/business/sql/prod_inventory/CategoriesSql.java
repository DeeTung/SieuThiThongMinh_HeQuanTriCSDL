package business.sql.prod_inventory;

import common.db.DatabaseConnection;
import model.product.Category;
import business.sql.SqlInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesSql implements SqlInterface<Category> {

    // 1. Áp dụng Singleton chuẩn (Chỉ tồn tại 1 thực thể duy nhất)
    private static CategoriesSql instance;

    private CategoriesSql() {} // Ngăn không cho tạo đối tượng từ bên ngoài

    public static CategoriesSql getInstance() {
        if (instance == null) {
            instance = new CategoriesSql();
        }
        return instance;
    }

    @Override
    public int insert(Category t) {
        int ketQua = 0;
        String sql = "INSERT INTO CATEGORIES (category_id, category_name, description, is_deleted) VALUES (?, ?, ?, ?)";
        
        // Dùng try-with-resources: Tự động đóng connection, không cần gọi close thủ công
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, t.getCategoryId());
            pst.setString(2, t.getCategoryName());
            pst.setString(3, t.getDescription());
            pst.setInt(4, t.getIsDeleted());
            
            ketQua = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public List<Category> selectAll() {
        List<Category> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIES WHERE is_deleted = 0";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getString("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setIsDeleted(rs.getInt("is_deleted"));
                ketQua.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public int update(Category t) { 
        int ketQua = 0;
        String sql = "UPDATE CATEGORIES SET category_name = ?, description = ?, is_deleted = ? WHERE category_id = ?";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, t.getCategoryName());
            pst.setString(2, t.getDescription());
            pst.setInt(3, t.getIsDeleted());
            pst.setString(4, t.getCategoryId());
            
            ketQua = pst.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public int delete(String id) { 
        int ketQua = 0;
        String sql = "UPDATE CATEGORIES SET is_deleted = 1 WHERE category_id = ?";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, id);
            ketQua = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public Category selectById(String id) { 
        Category ketQua = null;
        String sql = "SELECT * FROM CATEGORIES WHERE category_id = ? AND is_deleted = 0";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    ketQua = new Category(
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getInt("is_deleted")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public List<Category> selectByCondition(String condition) {
        // Bonus cho bà Quỳnh: Tìm kiếm danh mục theo tên
        List<Category> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIES WHERE category_name LIKE ? AND is_deleted = 0";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, "%" + condition + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new Category(
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getInt("is_deleted")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }
}