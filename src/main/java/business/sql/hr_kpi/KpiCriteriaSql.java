package business.sql.hr_kpi;

import model.account.kpi.KpiCriteria;
import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KpiCriteriaSql implements SqlInterface<KpiCriteria> {
    public static KpiCriteriaSql getInstance() {
        return new KpiCriteriaSql();
    }

    @Override public int insert(KpiCriteria t) { return 0; }
    @Override public int update(KpiCriteria t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public KpiCriteria selectById(String id) { return null; }

    @Override
    public List<KpiCriteria> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public ArrayList<KpiCriteria> selectAll() {
        ArrayList<KpiCriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM KPI_CRITERIA WHERE is_deleted = 0";

        try (Connection con = common.db.DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                KpiCriteria k = new KpiCriteria();

                // 1. Sửa setCriteriaId thành setKpiId (Vì model của bạn đặt tên là kpiId)
                k.setKpiId(rs.getString("kpi_id"));

                k.setCriteriaName(rs.getString("criteria_name"));
                k.setCriteriaType(rs.getString("criteria_type"));

                // 2. Dùng getBigDecimal cho weight thay vì getDouble
                k.setWeight(rs.getBigDecimal("weight"));

                // 3. Thêm các trường còn thiếu trong Model
                k.setRecordedTime(rs.getTimestamp("recorded_time"));
                k.setMinimumTarget(rs.getBigDecimal("minimum_target"));
                k.setIsDeleted(rs.getInt("is_deleted"));

                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}