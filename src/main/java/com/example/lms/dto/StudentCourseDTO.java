package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 26.
 * Author - yj
 * 학생 수강신청 화면용 강의 정보 DTO
 */
@Data
public class StudentCourseDTO {

    // TB_COURSE
    private Integer courseNo;            // 강의 번호 (TB_COURSE.course_no)
    private String  courseName;          // 강의명
    private Integer professorUserNo;     // 담당 교수 사용자번호 (TB_SYSUSER.user_no)
    private String  professorName;       // 담당 교수명 (JOIN 필드)
    private Integer courseYear;          // 개설 연도
    private Integer courseSemester;      // 학기 (1=1학기, 2=2학기)
    private Integer courseCapacity;      // 정원
    private Integer courseScore;         // 학점
    private String deptName;			 // 학과
    private String classroom;
    
    // 강의 시간
    private Integer courseTimeYoil;      // 요일 코드
    private Integer courseTimeStart;     // 시작 교시
    private Integer courseTimeEnd;       // 종료 교시

    // 수강 현황
    private Integer currentCount;        // 현재 신청 인원
    private Boolean isFull;              // 정원 초과 여부

    // 학생 개인 수강 상태
    // null → 한번도 신청하지 않음
    // 0    → 현재 신청중
    // 1    → 신청했다가 취소함
    private Integer myStatus;

    // 화면 편의를 위한 요일명
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
    
    // Mustache 버튼 렌더링용 상태값

    // 신청 완료한 상태? (myStatus = 0)
    public boolean getZeroStatus() {
        return myStatus != null && myStatus == 0;
    }

    // 취소했던 상태? (myStatus = 1)
    public boolean getCanceledStatus() {
        return myStatus != null && myStatus == 1;
    }

    // 신청한 적 없는 상태? (myStatus = null)
    public boolean getNeverApplied() {
        return myStatus == null;
    }
}