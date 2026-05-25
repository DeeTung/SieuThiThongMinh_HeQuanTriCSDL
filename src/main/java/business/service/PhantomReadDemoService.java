package business.service;

import business.sql.sales_order.PhantomReadDemoSql;
import common.db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.demo.PhantomReadSnapshot;

public class PhantomReadDemoService {

    private final PhantomReadDemoSql phantomReadDemoSql = new PhantomReadDemoSql();
    private static volatile boolean demoRunningInThisApp;

    private Connection connection;
    private IsolationMode isolationMode;
    private String scopedStoreId;
    private int readNo;
    private Integer baselineTotalOrders;
    private BigDecimal baselineTotalRevenue;

    public synchronized void start(IsolationMode mode) throws SQLException {
        stop();

        isolationMode = mode == null ? IsolationMode.READ_COMMITTED : mode;
        scopedStoreId = currentScopedStoreId();
        readNo = 0;
        baselineTotalOrders = null;
        baselineTotalRevenue = null;

        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(isolationMode.getJdbcLevel());
        demoRunningInThisApp = true;
    }

    public synchronized PhantomReadSnapshot readNext() throws SQLException {
        ensureStarted();

        readNo++;

        PhantomReadDemoSql.Summary summary = phantomReadDemoSql.selectSummary(connection, scopedStoreId);
        List<PhantomReadSnapshot.OrderRow> orders = phantomReadDemoSql.selectCompletedOrders(connection, scopedStoreId);

        int totalOrders = summary.getTotalOrders();
        BigDecimal totalRevenue = summary.getTotalRevenue();
        boolean phantomDetected = false;
        String conclusion;

        if (readNo == 1) {
            baselineTotalOrders = totalOrders;
            baselineTotalRevenue = totalRevenue;
            conclusion = "BASELINE: da luu snapshot lan doc dau tien.";
        } else if (isolationMode == IsolationMode.READ_COMMITTED && totalOrders > baselineTotalOrders) {
            phantomDetected = true;
            conclusion = "PHANTOM READ DETECTED: cung dieu kien loc nhung xuat hien them hoa don moi.";
        } else if (isolationMode == IsolationMode.SERIALIZABLE
                && totalOrders == baselineTotalOrders
                && totalRevenue.compareTo(baselineTotalRevenue) == 0) {
            conclusion = "SNAPSHOT STABLE: SERIALIZABLE giu nguyen du lieu theo snapshot ban dau.";
        } else {
            conclusion = "Chua co thay doi so voi baseline.";
        }

        return new PhantomReadSnapshot(
                readNo,
                totalOrders,
                totalRevenue,
                orders,
                phantomDetected,
                conclusion,
                isolationMode.name()
        );
    }

    public synchronized void stop() {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException e) {
            System.err.println("[PhantomReadDemoService] rollback error: " + e.getMessage());
        }

        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[PhantomReadDemoService] restore autocommit error: " + e.getMessage());
        }

        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("[PhantomReadDemoService] close connection error: " + e.getMessage());
        } finally {
            connection = null;
            isolationMode = null;
            scopedStoreId = null;
            readNo = 0;
            baselineTotalOrders = null;
            baselineTotalRevenue = null;
            demoRunningInThisApp = false;
        }
    }

    public synchronized boolean isRunning() {
        return connection != null;
    }

    public static boolean isDemoRunningInThisApp() {
        return demoRunningInThisApp;
    }

    private void ensureStarted() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Demo Phantom Read chua duoc bat dau.");
        }
    }

    private String currentScopedStoreId() {
        if (SessionManager.isAdmin()) {
            return null;
        }

        String storeId = SessionManager.getCurrentStoreId();
        return storeId == null || storeId.trim().isEmpty() ? null : storeId.trim();
    }

    public enum IsolationMode {
        READ_COMMITTED("READ_COMMITTED", Connection.TRANSACTION_READ_COMMITTED),
        SERIALIZABLE("SERIALIZABLE", Connection.TRANSACTION_SERIALIZABLE);

        private final String label;
        private final int jdbcLevel;

        IsolationMode(String label, int jdbcLevel) {
            this.label = label;
            this.jdbcLevel = jdbcLevel;
        }

        public int getJdbcLevel() {
            return jdbcLevel;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
