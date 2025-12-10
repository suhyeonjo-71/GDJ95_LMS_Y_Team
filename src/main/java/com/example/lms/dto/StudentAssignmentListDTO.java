package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT ASSIGNMENT LIST DTO
 * 학생 과제 목록 조회 정보
 */
@Data
public class StudentAssignmentListDTO {

    private int assignmentNo;
    private String assignmentTitle;
    private String assignmentDeadline;

    private Boolean submitted;        // 제출 여부

    private Integer courseNo;         // 과목 번호
}