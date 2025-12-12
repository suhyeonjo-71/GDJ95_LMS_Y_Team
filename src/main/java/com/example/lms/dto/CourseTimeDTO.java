package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * TB_COURSE_TIME TABLE DTO
 * 강의 요일/교시 정보 관리 DTO
 */
@Data
public class CourseTimeDTO {

    // PK
    private int courseTimeNo;

    // FK : 교수
    private int professorUserNo;

    // FK : 강의
    private int courseNo;

    // 요일 코드 (1=월,2=화,3=수,4=목,5=금)
    private int courseTimeYoil;

    // 교시 (예: 2교시, 4교시 등)
    private int courseTimeStart; // 시작 교시
    private int courseTimeEnd;   // 종료 교시

    private String createdate;
    private String updatedate;

    // 화면용: 요일 한글 반환
    public String getYoilName() {
        return switch (courseTimeYoil) {
            case 1 -> "월";
            case 2 -> "화";
            case 3 -> "수";
            case 4 -> "목";
            case 5 -> "금";
            default -> "";
        };
    }

    // 화면용: 교시 문자열 변환 ("2 ~ 4교시")
    public String getPeriodRange() {
        return courseTimeStart + " ~ " + courseTimeEnd + "교시";
    }
    
    private boolean isFirst;
}