package business.sql.sales_order;

import common.db.DatabaseConnection;
import model.payment.PaymentMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsSql {

    private static PaymentMethodsSql instance;

    private PaymentMethodsSql() {}

    public static PaymentMethodsSql getInstance() {
        if (instance == null) instance = new PaymentMethodsSql();
        return instance;
    }

    /** Lấy toàn bộ phương thức thanh toán chưa bị xóa */
    public List<PaymentMethod> selectAll() {
        List<PaymentMethod> list = new ArrayList<>();
        String sql = "SELECT payment_method_id, is_deleted FROM PAYMENT_METHODS WHERE is_deleted = 0";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PaymentMethod pm = new PaymentMethod();
                pm.setPaymentMethodId(rs.getString("payment_method_id"));
                pm.setIsDeleted(rs.getInt("is_deleted"));
                list.add(pm);
            }
        } catch (Exception e) {
            System.out.println("[PaymentMethodsSql] selectAll() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int insertPaymentMethod(Object t) { return 0; }
    public int insertBankTransferInfo(Object t) { return 0; } // Cho bảng BANK_TRANSFER_PAYMENT
    public int insertCashPaymentInfo(Object t) { return 0; } // Cho bảng CASH_PAYMENT
}