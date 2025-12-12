package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT GRADE DTO
 * 학생 최종 성적 조회 정보
 */
@Data
public class StudentGradeDTO {

    // 강의 번호 (FK)
    private int courseNo;

    // 학생 사용자 번호 (FK)
    private int studentUserNo;

    // 최종 성적 점수 (예: 95.0)
    private Double gradeScore;

    // 최종 등급 (예: A+, A0)
    private String gradeValue;
}
