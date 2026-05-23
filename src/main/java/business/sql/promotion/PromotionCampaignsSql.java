package business.sql.promotion;

import model.promotion.PromotionCampaign; // Đảm bảo đã có model tương ứng
import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionCampaignsSql implements SqlInterface<PromotionCampaign> {

    public static PromotionCampaignsSql getInstance() {
        return new PromotionCampaignsSql();
    }

    @Override
    public int insert(PromotionCampaign t) {
        int ketQua = 0;
        String sql = "INSERT INTO PROMOTION_CAMPAIGNS (campaign_id, campaign_name, description, start_date, end_date, is_deleted) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, t.getCampaignId());
            pst.setString(2, t.getCampaignName());
            pst.setString(3, t.getDescription());
            pst.setDate(4, t.getStartDate());
            pst.setDate(5, t.getEndDate());
            pst.setInt(6, t.getIsDeleted());
            ketQua = pst.executeUpdate();
            DatabaseConnection.closeConnection(con);
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }

    @Override public int update(PromotionCampaign t) { return 0; }
    @Override public int delete(String id) { return 0; }
    @Override public PromotionCampaign selectById(String id) { return null; }

    @Override
    public List<PromotionCampaign> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public ArrayList<PromotionCampaign> selectAll() {
        ArrayList<PromotionCampaign> list = new ArrayList<>();
        String sql = "SELECT * FROM PROMOTION_CAMPAIGNS WHERE is_deleted = 0";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                PromotionCampaign pc = new PromotionCampaign();
                pc.setCampaignId(rs.getString("campaign_id"));
                pc.setCampaignName(rs.getString("campaign_name"));
                pc.setDescription(rs.getString("description"));
                pc.setStartDate(rs.getDate("start_date"));
                pc.setEndDate(rs.getDate("end_date"));
                pc.setIsDeleted(rs.getInt("is_deleted"));
                list.add(pc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}