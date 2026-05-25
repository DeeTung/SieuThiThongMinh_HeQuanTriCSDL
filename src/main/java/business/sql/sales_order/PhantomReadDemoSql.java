package business.sql.sales_order;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.demo.PhantomReadSnapshot;

public class PhantomReadDemoSql {

    private static final String COMPLETED_OR_PAID_CONDITION = """
        (
            UPPER(NVL(status, '')) = 'PAID'
            OR UPPER(NVL(status, '')) = 'COMPLETED'
            OR UPPER(NVL(status, '')) LIKE '%HOAN THANH%'
            OR UPPER(NVL(status, '')) LIKE '%HOÀN THÀNH%'
        )
    """;

    public Summary selectSummary(Connection connection, String storeId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) AS total_orders,
                   NVL(SUM(total_amount), 0) AS total_revenue
            FROM ORDERS
            WHERE NVL(is_deleted, 0) = 0
              AND
        """);
        sql.append(COMPLETED_OR_PAID_CONDITION);

        if (hasStoreScope(storeId)) {
            sql.append(" AND store_id = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            bindStore(ps, storeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Summary(
                            rs.getInt("total_orders"),
                            nvl(rs.getBigDecimal("total_revenue"))
                    );
                }
            }
        }

        return new Summary(0, BigDecimal.ZERO);
    }

    public List<PhantomReadSnapshot.OrderRow> selectCompletedOrders(
            Connection connection,
            String storeId
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT order_id,
                   order_date,
                   employee_id,
                   payment_method_id,
                   total_amount,
                   status
            FROM ORDERS
            WHERE NVL(is_deleted, 0) = 0
              AND
        """);
        sql.append(COMPLETED_OR_PAID_CONDITION);

        if (hasStoreScope(storeId)) {
            sql.append(" AND store_id = ? ");
        }

        sql.append(" ORDER BY order_date DESC, order_id DESC ");

        List<PhantomReadSnapshot.OrderRow> rows = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            bindStore(ps, storeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new PhantomReadSnapshot.OrderRow(
                            rs.getString("order_id"),
                            rs.getTimestamp("order_date"),
                            rs.getString("employee_id"),
                            rs.getString("payment_method_id"),
                            nvl(rs.getBigDecimal("total_amount")),
                            rs.getString("status")
                    ));
                }
            }
        }

        return rows;
    }

    private boolean hasStoreScope(String storeId) {
        return storeId != null && !storeId.trim().isEmpty();
    }

    private void bindStore(PreparedStatement ps, String storeId) throws SQLException {
        if (hasStoreScope(storeId)) {
            ps.setString(1, storeId.trim());
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static class Summary {

        private final int totalOrders;
        private final BigDecimal totalRevenue;

        public Summary(int totalOrders, BigDecimal totalRevenue) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }
    }
}
