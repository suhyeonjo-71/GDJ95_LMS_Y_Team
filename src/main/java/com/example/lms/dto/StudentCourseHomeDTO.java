/**
 * 2025. 12. 02.
 * Author - yj
 * 학생 강의 홈 화면 DTO (Dashboard)
 */
package com.example.lms.dto;

import lombok.Data;

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

    // 편의용 요일명 Getter
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
    // 공지사항 최신 3개
    // -----------------------------
    private Integer noticeNo1;
    private String noticeTitle1;
    private String noticeDate1;

    private Integer noticeNo2;
    private String noticeTitle2;
    private String noticeDate2;

    private Integer noticeNo3;
    private String noticeTitle3;
    private String noticeDate3;

    // -----------------------------
    // 과제 요약 (미제출 1개 or 최신 과제 1개)
    // -----------------------------
    private Integer assignmentNo;
    private String assignmentTitle;
    private String assignmentDeadline;
    private Boolean submitted;  // 제출 여부
    private Double assignmentScore;       // 교수 점수

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
    // 질문 Q&A 최신 3개
    // -----------------------------
    private Integer questionNo1;
    private String questionTitle1;
    private String questionDate1;
    private Boolean questionAnswered1;

    private Integer questionNo2;
    private String questionTitle2;
    private String questionDate2;
    private Boolean questionAnswered2;

    private Integer questionNo3;
    private String questionTitle3;
    private String questionDate3;
    private Boolean questionAnswered3;
}
