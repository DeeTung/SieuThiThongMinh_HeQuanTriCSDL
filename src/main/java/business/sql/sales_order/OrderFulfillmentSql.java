package business.sql.sales_order;

import common.db.DatabaseConnection;
import model.order.Order;
import model.order.OrderDetail;

import java.sql.*;
import java.util.List;

public class OrderFulfillmentSql {

    private static OrderFulfillmentSql instance;

    public static OrderFulfillmentSql getInstance() {
        if (instance == null) instance = new OrderFulfillmentSql();
        return instance;
    }

    public void processOrderFulfillment(Order order, List<OrderDetail> details, String accountId) {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            // Bước 1: Insert vào bảng tạm
            insertToTemp(con, order.getOrderId(), details);

            // Bước 2: Gọi stored procedure
            String sql = "{ CALL SP_PROCESS_ORDER_FULFILLMENT(?,?,?,?,?,?,?) }";
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.setString(1, order.getOrderId());
                cs.setString(2, order.getCustomerId());
                cs.setString(3, order.getEmployeeId());
                cs.setString(4, order.getPaymentMethodId());
                cs.setDouble(5, order.getTotalAmount());
                cs.setString(6, order.getStoreId());
                cs.setString(7, accountId);
                cs.execute();
            }

            // SP đã COMMIT bên trong nên không cần commit lại
        } catch (SQLException e) {
            rollbackQuietly(con);
            if (e.getMessage() != null && e.getMessage().contains("ORA-20020")) {
                String msg = e.getMessage();
                int i = msg.indexOf("Loi xu ly don hang:");
                if (i != -1) {
                    int end = msg.indexOf("\n", i);
                    msg = msg.substring(i, end == -1 ? msg.length() : end);
                }
                throw new RuntimeException(msg);
            }
            if (e.getMessage() != null && e.getMessage().contains("ORA-2002")) {
                throw new RuntimeException("Sản phẩm không đủ tồn kho hoặc đang được xử lý bởi giao dịch khác.");
            }
            throw new RuntimeException("Lỗi xử lý đơn hàng: " + e.getMessage());
        } finally {
            closeConn(con);
        }
    }

    private void insertToTemp(Connection con, String orderId, List<OrderDetail> details) throws SQLException {
        String sql = """
            INSERT INTO ORDER_DETAILS_TEMP
                (order_detail_id, order_id, product_id, quantity, unit_price)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (OrderDetail d : details) {
                ps.setString(1, "OD" + System.nanoTime());
                ps.setString(2, orderId);
                ps.setString(3, d.getProductId());
                ps.setInt(4, d.getQuantity());
                ps.setDouble(5, d.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void rollbackQuietly(Connection con) {
        try { if (con != null) con.rollback(); } catch (Exception ignored) {}
    }

    private void closeConn(Connection con) {
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (Exception ignored) {}
    }
}