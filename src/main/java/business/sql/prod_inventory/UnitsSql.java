package business.sql.prod_inventory;

import common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.product.Unit;

public class UnitsSql {

    private static UnitsSql instance;

    private UnitsSql() {
    }

    public static UnitsSql getInstance() {
        if (instance == null) {
            instance = new UnitsSql();
        }
        return instance;
    }

    public String ensureUnitWithConn(Connection con, String unitName) throws SQLException {
        String safeName = (unitName == null || unitName.isBlank()) ? "Cai" : unitName.trim();
        String existingId = findUnitIdByNameWithConn(con, safeName);
        if (existingId != null) {
            return existingId;
        }

        String unitId = generateUnitId(safeName);
        String existingGeneratedId = findUnitNameByIdWithConn(con, unitId);
        if (existingGeneratedId != null) {
            return unitId;
        }

        String sql = "INSERT INTO UNITS (unit_id, unit_name, is_deleted) VALUES (?, ?, 0)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, unitId);
            pst.setString(2, safeName);
            pst.executeUpdate();
        }
        return unitId;
    }

    public String findUnitIdByNameWithConn(Connection con, String unitName) throws SQLException {
        if (unitName == null || unitName.isBlank()) {
            return null;
        }
        String sql = "SELECT unit_id FROM UNITS WHERE LOWER(unit_name) = LOWER(?) AND NVL(is_deleted, 0) = 0";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, unitName.trim());
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getString("unit_id") : null;
            }
        }
    }

    private String findUnitNameByIdWithConn(Connection con, String unitId) throws SQLException {
        String sql = "SELECT unit_name FROM UNITS WHERE unit_id = ? AND NVL(is_deleted, 0) = 0";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, unitId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getString("unit_name") : null;
            }
        }
    }

    public List<Unit> selectAll() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT unit_id, unit_name, is_deleted FROM UNITS WHERE NVL(is_deleted, 0) = 0 ORDER BY unit_name";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                units.add(new Unit(
                        rs.getString("unit_id"),
                        rs.getString("unit_name"),
                        rs.getInt("is_deleted")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi UnitsSql.selectAll: " + e.getMessage());
            e.printStackTrace();
        }
        return units;
    }

    private String generateUnitId(String unitName) {
        String normalized = java.text.Normalizer
                .normalize(unitName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toUpperCase();
        if (normalized.isBlank()) {
            normalized = "UNIT";
        }
        if (normalized.length() > 30) {
            normalized = normalized.substring(0, 30);
        }
        return "U_" + normalized;
    }
}
