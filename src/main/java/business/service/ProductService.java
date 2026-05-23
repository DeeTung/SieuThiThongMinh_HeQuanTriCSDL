package business.service;

import business.sql.rbac.AuditLogSql;

public class ProductService {

    /**
     * Ghi audit log cho hành động sửa giá sản phẩm. Gọi hàm này sau khi update
     * giá thành công trong ProductSql.
     * @param productId
     * @param oldPrice
     * @param newPrice
     * @param reason
     */
    public static void logUpdatePrice(String productId, double oldPrice, double newPrice, String reason) {
        String actorId = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getAccountId()
                : null;

        AuditLogSql.getInstance().log(
                actorId,
                "UPDATE_PRICE",
                "PRODUCT",
                productId,
                "price=" + oldPrice,
                "price=" + newPrice,
                reason != null ? reason : "Cap nhat gia san pham",
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
