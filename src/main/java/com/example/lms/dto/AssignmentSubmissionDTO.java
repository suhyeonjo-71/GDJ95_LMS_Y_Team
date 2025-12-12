package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 25.
 * Author - yj
 * TB_ASSIGNMENT_SUBMISSION TABLE DTO
 * 학생 과제 제출 정보
 */
@Data
public class AssignmentSubmissionDTO {

    private int assignmentSubmissionNo;        // 제출번호 PK
    private int assignmentNo;                  // 과제번호 FK
    private int writerUserNo;              // 학생번호 FK
    private String assignmentSubmissionFileUrl;// 제출 파일 URL
    private String assignmentSubmissionContent;// 제출 내용
    private Integer assignmentScore;           // 채점 점수
    private int assignmentSubmissionStatus;    // 0=제출, 1=미제출
    private String createdate;                 // 제출일
    private String updatedate;                 // 수정일
}