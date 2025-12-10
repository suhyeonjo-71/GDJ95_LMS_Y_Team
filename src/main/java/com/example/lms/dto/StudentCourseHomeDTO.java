package com.example.lms.dto;

import java.util.List;
import lombok.Data;
/**
 * 2025. 12. 02.
 * Author - yj
 * 학생 강의 홈 화면 DTO (Dashboard) — 리팩토링 버전
 */
@Data
public class StudentCourseHomeDTO {

    // -----------------------------
    // 강의 기본 정보
    // -----------------------------
    private Integer courseNo;
    private String courseName;
    private String professorName;
    private Integer courseScore;
    private String classroom;

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

    // -----------------------------
    // 공지사항 최신 N개 (보통 3개)
    // -----------------------------
    private List<StudentCourseNoticeDTO> noticeList;

    // -----------------------------
    // 과제 요약 (최신 or 미제출 1개)
    // -----------------------------
    private Integer assignmentNo;
    private String assignmentTitle;
    private String assignmentDeadline;
    private Boolean submitted;  
    private Double assignmentScore;

    // -----------------------------
    // 출석 요약
    // -----------------------------
    private Integer attendanceCount;
    private Integer lateCount;
    private Integer absentCount;
    private Double attendanceRate;

    // -----------------------------
    // 성적 요약
    // -----------------------------
    private Double gradeScore;
    private String gradeValue;

    // -----------------------------
    // 질문(Q&A) 최신 리스트 (3개든 5개든 자유)
    // -----------------------------
    private List<StudentQuestionDTO> questionList;
}
