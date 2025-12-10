package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 28.
 * Author - yj
 * STUDENT TIMETABLE DTO
 * 학생 시간표 조회 정보 (TB_ENROLLMENT + TB_COURSE + TB_COURSE_TIME JOIN 결과)
 */
@Data
public class StudentTimetableDTO {

    private Integer courseNo;        // 강의 번호
    private String  courseName;      // 강의명
    private String  courseClassroom; // 강의실

    private Integer courseTimeYoil;  // 요일 코드 (1=월~5=금)
    private Integer courseTimeStart; // 시작 교시
    private Integer courseTimeEnd;   // 종료 교시

    // 화면용 : 요일명 변환
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
}
