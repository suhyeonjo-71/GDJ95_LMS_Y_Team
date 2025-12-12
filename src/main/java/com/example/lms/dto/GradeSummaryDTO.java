package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - yj
 * TB_GRADE SUMMARY DTO
 * 학생 성적 요약 정보
 */
@Data
public class GradeSummaryDTO {

    private String gradeValue;   // 등급 (A+, B ...)
    private Double gradeScore;   // 점수 (4.5, 4.0 ...)
}
