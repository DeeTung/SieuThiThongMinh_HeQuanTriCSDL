package business.sql.hr_kpi;

import common.db.DatabaseConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.employee.Attendance;

public class AttendanceSql {
    public static AttendanceSql getInstance() {
        return new AttendanceSql();
    }

    public int insertAttendance(Object t) { return 0; }
    
    public ArrayList<Attendance> selectAll() {
        ArrayList<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM ATTENDANCE WHERE is_deleted = 0 ORDER BY work_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Attendance a = new Attendance();
                a.setEmployeeId(rs.getString("employee_id"));
                a.setShiftId(rs.getString("shift_id"));
                a.setWorkDate(rs.getDate("work_date"));
                a.setCheckInTime(rs.getTimestamp("check_in_time"));
                a.setCheckOutTime(rs.getTimestamp("check_out_time"));
                a.setAttendanceCoefficient(rs.getDouble("attendance_coefficient"));
                a.setIsDeleted(rs.getInt("is_deleted"));
                
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Tìm kiếm dựa trên cặp khóa chính
    public Object selectByCompositeKey(String employeeId, String shiftId, Date workDate) {
        return null;
    }

    public int updateAttendance(Object t) { return 0; }
    
    // Xóa mềm
    public int deleteAttendance(String employeeId, String shiftId) { return 0; }
}