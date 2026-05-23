package model.employee;

import java.sql.Date;
import java.sql.Timestamp;

public class Attendance {
    private String employeeId;
    private String shiftId;
    private Date workDate;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private double attendanceCoefficient;
    private int isDeleted;

    public Attendance() {}

    public Attendance(String employeeId, String shiftId, Date workDate, Timestamp checkInTime, 
                      Timestamp checkOutTime, double attendanceCoefficient, int isDeleted) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.workDate = workDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.attendanceCoefficient = attendanceCoefficient;
        this.isDeleted = isDeleted;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getShiftId() { return shiftId; }
    public void setShiftId(String shiftId) { this.shiftId = shiftId; }

    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }

    public Timestamp getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Timestamp checkInTime) { this.checkInTime = checkInTime; }

    public Timestamp getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Timestamp checkOutTime) { this.checkOutTime = checkOutTime; }

    public double getAttendanceCoefficient() { return attendanceCoefficient; }
    public void setAttendanceCoefficient(double attendanceCoefficient) { this.attendanceCoefficient = attendanceCoefficient; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}