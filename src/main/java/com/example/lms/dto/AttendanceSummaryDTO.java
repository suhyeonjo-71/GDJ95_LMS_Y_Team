package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 25.
 * Author - yj
 * 학생 출석 요약 DTO
 * TB_ATTENDANCE 집계 결과용
 */
@Data
public class AttendanceSummaryDTO {

    private Integer attendanceCount;  // 출석 수
    private Integer absentCount;      // 결석 수
    private Integer lateCount;        // 지각 수
    private Double  attendanceRate;   // 출석률 (%)
    
    private Double presentRate;
    private Double lateRate;
    private Double absentRate;
}
