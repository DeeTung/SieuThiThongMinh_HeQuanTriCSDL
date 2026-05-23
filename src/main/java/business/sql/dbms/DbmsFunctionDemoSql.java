package business.sql.dbms;

import common.db.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO demo Function trong Hệ quản trị CSDL.
 *
 * Mục tiêu demo:
 * 1. Chưa ứng dụng Function:
 *    - Java tự viết nhiều câu SQL để tính doanh thu.
 *    - Logic bị phân tán ở Java.
 *
 * 2. Đã ứng dụng Function:
 *    - Java gọi FUNC_GET_FINAL_SYSTEM_REVENUE trong Oracle.
 *    - Logic tính toán tập trung trong database.
 */
public class DbmsFunctionDemoSql {

    private static DbmsFunctionDemoSql instance;

    private DbmsFunctionDemoSql() {
    }

    public static DbmsFunctionDemoSql getInstance() {
        if (instance == null) {
            instance = new DbmsFunctionDemoSql();
        }
        return instance;
    }

    /**
     * MỨC 1 - CHƯA ỨNG DỤNG FUNCTION.
     *
     * Java tự tính:
     * Doanh thu cuối cùng = Tổng bán hàng - Tổng nhập hàng sau thuế.
     *
     * Đây là cách "chưa tối ưu" để demo vấn đề:
     * - SQL bị viết trực tiếp ở Java.
     * - Điều kiện nghiệp vụ bị lặp lại ở tầng ứng dụng.
     * - Nếu sau này đổi trạng thái đơn hàng hoặc đổi công thức, phải sửa Java.
     */
    public BigDecimal calculateFinalRevenueWithoutFunction(Date fromDate, Date toDate) throws SQLException {
        BigDecimal totalSales;
        BigDecimal totalImport;

        try (Connection con = DatabaseConnection.getConnection()) {
            totalSales = queryTotalSales(con, fromDate, toDate);
            totalImport = queryTotalImport(con, fromDate, toDate);
        }

        return totalSales.subtract(totalImport);
    }

    /**
     * MỨC 2 - ĐÃ ỨNG DỤNG FUNCTION.
     *
     * Java chỉ gọi function trong Oracle:
     * FUNC_GET_FINAL_SYSTEM_REVENUE(p_from_date, p_to_date)
     *
     * Logic nghiệp vụ nằm trong database, Java không cần tự tính lại.
     */
    public BigDecimal calculateFinalRevenueWithFunction(Date fromDate, Date toDate) throws SQLException {
        String sql = "{ ? = call FUNC_GET_FINAL_SYSTEM_REVENUE(?, ?) }";

        try (
                Connection con = DatabaseConnection.getConnection();
                CallableStatement cs = con.prepareCall(sql)
        ) {
            cs.registerOutParameter(1, java.sql.Types.NUMERIC);

            if (fromDate == null) {
                cs.setNull(2, java.sql.Types.DATE);
            } else {
                cs.setDate(2, fromDate);
            }

            if (toDate == null) {
                cs.setNull(3, java.sql.Types.DATE);
            } else {
                cs.setDate(3, toDate);
            }

            cs.execute();

            BigDecimal result = cs.getBigDecimal(1);
            return result == null ? BigDecimal.ZERO : result;
        }
    }

    /**
     * Kiểm tra function có tồn tại và đang VALID không.
     */
    public String getFunctionStatus() throws SQLException {
        String sql = """
            SELECT status
            FROM user_objects
            WHERE object_name = 'FUNC_GET_FINAL_SYSTEM_REVENUE'
              AND object_type = 'FUNCTION'
        """;

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("status");
            }
            return "NOT_FOUND";
        }
    }

    /**
     * Tổng bán hàng từ ORDERS.
     *
     * Bán hàng không có VAT riêng.
     * ORDERS.total_amount là số tiền cuối cùng sau giảm giá thành viên/khuyến mãi.
     */
    private BigDecimal queryTotalSales(Connection con, Date fromDate, Date toDate) throws SQLException {
        String sql = """
            SELECT NVL(SUM(o.total_amount), 0) AS total_sales
            FROM ORDERS o
            WHERE NVL(o.is_deleted, 0) = 0
              AND (
                    UPPER(NVL(o.status, '')) = 'COMPLETED'
                    OR UPPER(NVL(o.status, '')) = 'SUCCESS'
                    OR UPPER(NVL(o.status, '')) = 'PAID'
                    OR UPPER(NVL(o.status, '')) LIKE '%HOÀN THÀNH%'
                    OR UPPER(NVL(o.status, '')) LIKE '%HOAN THANH%'
                    OR UPPER(NVL(o.status, '')) LIKE '%ĐÃ THANH TOÁN%'
                    OR UPPER(NVL(o.status, '')) LIKE '%DA THANH TOAN%'
                  )
              AND (? IS NULL OR o.order_date >= ?)
              AND (? IS NULL OR o.order_date < ? + 1)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bindDateRange(ps, fromDate, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal("total_sales");
                    return value == null ? BigDecimal.ZERO : value;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Tổng nhập hàng sau thuế từ PURCHASE_RECEIPTS.
     *
     * PURCHASE_RECEIPTS.total_after_tax đã gồm VAT.
     */
    private BigDecimal queryTotalImport(Connection con, Date fromDate, Date toDate) throws SQLException {
        String sql = """
            SELECT NVL(SUM(pr.total_after_tax), 0) AS total_import
            FROM PURCHASE_RECEIPTS pr
            WHERE NVL(pr.is_deleted, 0) = 0
              AND (? IS NULL OR pr.created_at >= CAST(? AS TIMESTAMP))
              AND (? IS NULL OR pr.created_at < CAST(? + 1 AS TIMESTAMP))
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bindDateRange(ps, fromDate, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal("total_import");
                    return value == null ? BigDecimal.ZERO : value;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private void bindDateRange(PreparedStatement ps, Date fromDate, Date toDate) throws SQLException {
        if (fromDate == null) {
            ps.setNull(1, java.sql.Types.DATE);
            ps.setNull(2, java.sql.Types.DATE);
        } else {
            ps.setDate(1, fromDate);
            ps.setDate(2, fromDate);
        }

        if (toDate == null) {
            ps.setNull(3, java.sql.Types.DATE);
            ps.setNull(4, java.sql.Types.DATE);
        } else {
            ps.setDate(3, toDate);
            ps.setDate(4, toDate);
        }
    }
}