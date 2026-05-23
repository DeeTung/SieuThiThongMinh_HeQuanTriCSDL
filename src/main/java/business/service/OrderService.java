package business.service;

import business.sql.rbac.AuditLogSql;

public class OrderService {

    /**
     * Ghi audit log cho hành động hủy đơn. Gọi hàm này sau khi hủy đơn thành
     * công trong OrderSql.
     * @param orderId
     * @param oldStatus
     * @param newStatus
     * @param reason
     */
    public static void logCancelOrder(String orderId, String oldStatus, String newStatus, String reason) {
        String actorId = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getAccountId()
                : null;

        AuditLogSql.getInstance().log(
                actorId,
                "CANCEL_ORDER",
                "ORDER",
                orderId,
                "status=" + (oldStatus != null ? oldStatus : "ACTIVE"),
                "status=" + (newStatus != null ? newStatus : "CANCELED"),
                reason != null ? reason : "Huy don hang",
                localIp(),
                deviceInfo()
        );
    }

    private static String localIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String deviceInfo() {
        return System.getProperty("os.name") + " | Java " + System.getProperty("java.version");
    }
}
