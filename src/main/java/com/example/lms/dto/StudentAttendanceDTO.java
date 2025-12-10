package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT ATTENDANCE DTO
 * 학생 주차별 출석 정보
 */
@Data
public class StudentAttendanceDTO {

    private int weekNo;            // 회차 (1~15)
    private int attendanceStatus;     // -1,0,1,2
    private Double attendanceRate;
    
    public boolean isAttend() { return attendanceStatus == 0; }
    public boolean isLate() { return attendanceStatus == 1; }
    public boolean isAbsent() { return attendanceStatus == 2; }
    public boolean isNone() { return attendanceStatus == -1; }
}
