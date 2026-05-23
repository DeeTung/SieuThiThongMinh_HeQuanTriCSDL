package business.sql.prod_inventory;

import common.db.DatabaseConnection;
import model.product.Store;
import business.sql.SqlInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoresSql implements SqlInterface<Store> {
    private static StoresSql instance;

    // Constructor private để đảm bảo Singleton
    private StoresSql() {
    }

    // Áp dụng mẫu Singleton chuẩn
    public static StoresSql getInstance() {
        if (instance == null) {
            instance = new StoresSql();
        }
        return instance;
    }

    @Override
    public int insert(Store t) {
        int ketQua = 0;
        String sql = "INSERT INTO STORES (store_id, email, address, phone_number, is_deleted) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, t.getStoreId());
            pst.setString(2, t.getEmail());
            pst.setString(3, t.getAddress());
            pst.setString(4, t.getPhoneNumber());
            pst.setInt(5, t.getIsDeleted());

            ketQua = pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi Insert Store: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public int update(Store t) {
        int ketQua = 0;
        String sql = "UPDATE STORES SET email = ?, address = ?, phone_number = ?, is_deleted = ? WHERE store_id = ?";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, t.getEmail());
            pst.setString(2, t.getAddress());
            pst.setString(3, t.getPhoneNumber());
            pst.setInt(4, t.getIsDeleted());
            pst.setString(5, t.getStoreId());

            ketQua = pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi Update Store: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public int delete(String id) {
        int ketQua = 0;
        // Dùng "Xóa mềm" (Soft Delete) theo yêu cầu
        String sql = "UPDATE STORES SET is_deleted = 1 WHERE store_id = ?";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, id);
            ketQua = pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi Delete Store: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public ArrayList<Store> selectAll() {
        ArrayList<Store> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM STORES WHERE is_deleted = 0";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Store s = new Store();
                s.setStoreId(rs.getString("store_id"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                s.setPhoneNumber(rs.getString("phone_number"));
                s.setIsDeleted(rs.getInt("is_deleted"));
                ketQua.add(s);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi Select All Store: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public Store selectById(String id) {
        Store ketQua = null;
        String sql = "SELECT * FROM STORES WHERE store_id = ?";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    ketQua = new Store(
                            rs.getString("store_id"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getInt("is_deleted")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi Select By ID Store: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public List<Store> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}