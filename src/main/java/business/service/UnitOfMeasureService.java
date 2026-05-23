package business.service;

import business.sql.prod_inventory.ProductUnitsSql;
import business.sql.prod_inventory.UnitsSql;
import common.db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class UnitOfMeasureService {

    public boolean configureProductUnit(String productId, String unitName,
            BigDecimal conversionRateToBase, boolean isBaseUnit) {
        if (isBlank(productId) || isBlank(unitName) || conversionRateToBase == null
                || conversionRateToBase.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String unitId = UnitsSql.getInstance().ensureUnitWithConn(con, unitName);
                ProductUnitsSql.getInstance()
                        .upsertProductUnitWithConn(con, productId, unitId, conversionRateToBase, isBaseUnit);
                if (isBaseUnit) {
                    ProductUnitsSql.getInstance().setBaseUnitWithConn(con, productId, unitId);
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                System.err.println("Loi UnitOfMeasureService.configureProductUnit: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi ket noi UnitOfMeasureService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int convertToBaseQuantity(String productId, String unitId, int quantity) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            return ProductUnitsSql.getInstance()
                    .convertToBaseQuantityWithConn(con, productId, unitId, quantity);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
