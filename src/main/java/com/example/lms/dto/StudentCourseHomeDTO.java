package com.example.lms.dto;

import java.util.List;
import lombok.Data;

@Data
public class StudentCourseHomeDTO {

    private Integer courseNo;
    private String courseName;
    private String professorName;
    private Integer courseScore;
    private String classroom;

    private Integer courseTimeYoil;
    private Integer courseTimeStart;
    private Integer courseTimeEnd;

    // -----------------------------
    // 요일명 반환
    // -----------------------------
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

    // -----------------------------
    // 요약 정보
    // -----------------------------
    private List<StudentCourseNoticeDTO> noticeList;
    private List<StudentAssignmentListDTO> assignmentList;
    private AttendanceSummaryDTO attendanceSummary;
    private StudentGradeDTO gradeSummary;
    private List<StudentQuestionDTO> questionList;

    // -----------------------------
    // 서브 네비 (학생 페이지) 활성화 여부
    // -----------------------------
    private boolean navHome;
    private boolean navNotice;
    private boolean navAssignment;
    private boolean navAttendance;
    private boolean navGrade;
    private boolean navQuestion;
}
