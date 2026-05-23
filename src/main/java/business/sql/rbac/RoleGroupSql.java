package business.sql.rbac;

import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.account.RoleGroup;
import java.util.ArrayList;
import java.util.List;

public class RoleGroupSql implements SqlInterface<RoleGroup> {
    public static RoleGroupSql getInstance() {
        return new RoleGroupSql();
    }

    @Override public int insert(RoleGroup t) { return 0; }
    @Override public int update(RoleGroup t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public RoleGroup selectById(String id) { return null; }

    @Override
    public List<RoleGroup> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public ArrayList<RoleGroup> selectAll() {
        ArrayList<RoleGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM ROLE_GROUPS WHERE is_deleted = 0";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                RoleGroup rg = new RoleGroup();
                rg.setRoleGroupId(rs.getString("role_group_id"));
                rg.setGroupName(rs.getString("group_name"));
                rg.setCreatedAt(rs.getTimestamp("created_at"));
                rg.setUpdatedAt(rs.getTimestamp("updated_at"));
                rg.setIsDeleted(rs.getInt("is_deleted"));
                list.add(rg);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}