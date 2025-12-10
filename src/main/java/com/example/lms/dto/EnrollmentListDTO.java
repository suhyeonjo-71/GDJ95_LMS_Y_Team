package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 27.
 * Author - yj
 * 학생 수강신청 내역 DTO
 * TB_ENROLLMENT + TB_COURSE + TB_SYSUSER + TB_COURSE_TIME JOIN 결과
 */
@Data
public class EnrollmentListDTO {

    // TB_ENROLLMENT
    private Integer enrollmentNo;
    private Integer enrollmentStatus; // 0=신청완료, 1=취소됨
    private String createdate;

    // TB_COURSE
    private Integer courseNo;
    private String  courseName;
    private Integer courseScore;
    private Integer courseCapacity;

    // 교수명
    private String professorName;

    // TB_COURSE_TIME
    private Integer courseTimeYoil;
    private Integer courseTimeStart;
    private Integer courseTimeEnd;
    
    public String getYoilName() {
        if (courseTimeYoil == null) return "";
        return switch (courseTimeYoil) {
            case 1 -> "월";
            case 2 -> "화";
            case 3 -> "수";
            case 4 -> "목";
            case 5 -> "금";
            default -> "";
        };
    }

    // 화면 전용 (Mustache 사용)
    private Boolean active;    // status=0
    private Boolean canceled;  // status=1
}
