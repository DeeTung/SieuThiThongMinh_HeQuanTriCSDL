package business.sql.rbac;

import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.account.Function;
import java.util.ArrayList;
import java.util.List;

public class FunctionSql implements SqlInterface<Function> {
    public static FunctionSql getInstance() {
        return new FunctionSql();
    }

    @Override public int insert(Function t) { return 0; }
    @Override public int update(Function t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public Function selectById(String id) { return null; }

    @Override
    public List<Function> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public ArrayList<Function> selectAll() {
        ArrayList<Function> list = new ArrayList<>();
        String sql = "SELECT * FROM FUNCTIONS WHERE is_deleted = 0";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Function f = new Function();
                f.setFunctionId(rs.getString("function_id"));
                f.setFunctionName(rs.getString("function_name"));
                list.add(f);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}