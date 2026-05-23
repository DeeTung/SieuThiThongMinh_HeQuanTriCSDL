package business.sql.rbac;

import business.sql.SqlInterface;
import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.account.User; // Đảm bảo bạn có class User trong package model
import java.util.ArrayList;
import java.util.List;

public class UserSql implements SqlInterface<User> {
    public static UserSql getInstance() {
        return new UserSql();
    }

    @Override public int insert(User t) { return 0; }
    @Override public int update(User t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public User selectById(String id) { return null; }

    @Override
    public List<User> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public ArrayList<User> selectAll() {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE is_deleted = 0";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getString("user_id"));
                u.setFullName(rs.getString("full_name"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}