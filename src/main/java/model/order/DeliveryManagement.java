package model.order;

import java.sql.Date;

public class DeliveryManagement {

    public static Object getInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private String deliveryId;
    private String orderId;
    private String employeeId;
    private Date executionDate;
    private String status;
    private int isDeleted;

    public DeliveryManagement() {
    }

    public DeliveryManagement(String deliveryId, String orderId, String employeeId, Date executionDate, String status, int isDeleted) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.executionDate = executionDate;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public Date getExecutionDate() { return executionDate; }
    public void setExecutionDate(Date executionDate) { this.executionDate = executionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

public int insert(DeliveryManagement giaoHang) {
        int ketQua = 0;
        try {
            // 1. Tạo kết nối đến Oracle Database
            java.sql.Connection con = common.db.DatabaseConnection.getConnection();
            
            // 2. Tạo câu lệnh SQL 
            String sql = "INSERT INTO DELIVERY_MANAGEMENT (delivery_id, order_id, employee_id, execution_date, status, is_deleted) VALUES (?, ?, ?, ?, ?, ?)";
            
            // 3. Đưa câu lệnh vào PreparedStatement
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            
            // 4. Gắn các giá trị từ đối tượng giaoHang vào các dấu chấm hỏi (?)
            pst.setString(1, giaoHang.getDeliveryId());
            pst.setString(2, giaoHang.getOrderId());
            pst.setString(3, giaoHang.getEmployeeId());
            pst.setDate(4, giaoHang.getExecutionDate());
            pst.setString(5, giaoHang.getStatus());
            pst.setInt(6, giaoHang.getIsDeleted());
            
            // 5. Thực thi lệnh Insert và lấy số dòng bị thay đổi
            ketQua = pst.executeUpdate();
            
            // 6. Đóng kết nối để giải phóng bộ nhớ
            common.db.DatabaseConnection.closeConnection(con);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    public String getDeliveryStatus() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}