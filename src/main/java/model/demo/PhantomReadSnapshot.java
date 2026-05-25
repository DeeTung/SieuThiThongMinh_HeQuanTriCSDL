package model.demo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhantomReadSnapshot {

    private final int readNo;
    private final int totalOrders;
    private final BigDecimal totalRevenue;
    private final List<OrderRow> orders;
    private final boolean phantomDetected;
    private final String conclusionMessage;
    private final String isolationLevel;

    public PhantomReadSnapshot(
            int readNo,
            int totalOrders,
            BigDecimal totalRevenue,
            List<OrderRow> orders,
            boolean phantomDetected,
            String conclusionMessage,
            String isolationLevel
    ) {
        this.readNo = readNo;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
        this.orders = Collections.unmodifiableList(new ArrayList<>(orders == null ? List.of() : orders));
        this.phantomDetected = phantomDetected;
        this.conclusionMessage = conclusionMessage;
        this.isolationLevel = isolationLevel;
    }

    public int getReadNo() {
        return readNo;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public List<OrderRow> getOrders() {
        return orders;
    }

    public boolean isPhantomDetected() {
        return phantomDetected;
    }

    public String getConclusionMessage() {
        return conclusionMessage;
    }

    public String getIsolationLevel() {
        return isolationLevel;
    }

    public static class OrderRow {

        private final String orderId;
        private final Timestamp orderDate;
        private final String employeeId;
        private final String paymentMethodId;
        private final BigDecimal totalAmount;
        private final String status;

        public OrderRow(
                String orderId,
                Timestamp orderDate,
                String employeeId,
                String paymentMethodId,
                BigDecimal totalAmount,
                String status
        ) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.employeeId = employeeId;
            this.paymentMethodId = paymentMethodId;
            this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public Timestamp getOrderDate() {
            return orderDate;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getPaymentMethodId() {
            return paymentMethodId;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public String getStatus() {
            return status;
        }
    }
}
