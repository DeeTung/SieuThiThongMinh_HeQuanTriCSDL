package business.sql.rbac;

import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.account.Role;
import java.util.ArrayList;
import java.util.List;


public class RoleSql implements SqlInterface<Role> {
    public static RoleSql getInstance() {
        return new RoleSql();
    }

    @Override public int insert(Role t) { return 0; }
    @Override public int update(Role t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public Role selectById(String id) { return null; }

    @Override
    public List<Role> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public ArrayList<Role> selectAll() {
        ArrayList<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM ROLES WHERE is_deleted = 0";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Role r = new Role(); // Sử dụng Constructor rỗng

                // Gán từng giá trị từ database vào object
                r.setRoleId(rs.getString("role_id"));
                r.setFunctionId(rs.getString("function_id"));
                r.setCanView(rs.getInt("can_view"));
                r.setCanAdd(rs.getInt("can_add"));
                r.setCanEdit(rs.getInt("can_edit"));
                r.setCanDelete(rs.getInt("can_delete"));
                r.setCanExport(rs.getInt("can_export"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUpdatedAt(rs.getTimestamp("updated_at"));
                r.setIsDeleted(rs.getInt("is_deleted"));

                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * HÀM MỚI: Lấy danh sách các Role hiện có để đổ vào Combobox tạo tài khoản
     */
    public List<String> getAvailableRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT role_id FROM ROLES WHERE is_deleted = 0";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
             
            while (rs.next()) {
                roles.add(rs.getString("role_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Cầu chì an toàn: Nếu Database ROLES đang trống, trả về danh sách cứng tạm thời
        if (roles.isEmpty()) {
            roles.add("R_ADMIN_ALL");
            roles.add("R_STORE_MNG");
            roles.add("R_STAFF_SALE");
            roles.add("R_STAFF_STOCK");
        }
        return roles;
    }
}

