package business.sql.demo;

import common.db.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public class LostUpdateDemoSql {

    private static LostUpdateDemoSql instance;

    private LostUpdateDemoSql() {
    }

    public static LostUpdateDemoSql getInstance() {
        if (instance == null) {
            instance = new LostUpdateDemoSql();
        }
        return instance;
    }

    public LostUpdateResult sellProductByProcedure(
            String employeeId,
            String storeId,
            String productId,
            int sellQty,
            int sleepSeconds
    ) {
        String sql = "{ call PROC_DEMO_SELL_PRODUCT_LOST_UPDATE(?, ?, ?, ?, ?, ?, ?) }";

        try (
                Connection con = DatabaseConnection.getConnection();
                CallableStatement cs = con.prepareCall(sql)
        ) {
            /*
             * QUAN TRỌNG:
             * Không setAutoCommit(false) ở Java.
             * Không SELECT FOR UPDATE ở Java.
             * Không kiểm tra tồn kho ở Java.
             *
             * Toàn bộ logic Lost Update nằm trong procedure Oracle.
             */
            cs.setString(1, employeeId);
            cs.setString(2, storeId);
            cs.setString(3, productId);
            cs.setInt(4, sellQty);
            cs.setInt(5, sleepSeconds);

            cs.registerOutParameter(6, Types.NUMERIC);
            cs.registerOutParameter(7, Types.VARCHAR);

            cs.execute();

            int code = cs.getInt(6);
            String message = cs.getString(7);

            return new LostUpdateResult(code, message);

        } catch (Exception ex) {
            return new LostUpdateResult(
                    -999,
                    "Lỗi Java khi gọi PROC_DEMO_SELL_PRODUCT_LOST_UPDATE: " + ex.getMessage()
            );
        }
    }

    public static class LostUpdateResult {
        private final int code;
        private final String message;

        public LostUpdateResult(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return code == 1;
        }

        @Override
        public String toString() {
            return "CODE=" + code + " | " + message;
        }
    }
}