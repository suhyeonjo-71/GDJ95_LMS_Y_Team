package com.example.lms.dto;

import lombok.Data;
import java.time.LocalDateTime;
/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT ASSIGNMENT DETAIL DTO
 * 학생 과제 상세 조회 정보
 */
@Data
public class StudentAssignmentDetailDTO {

    // 과제 기본 정보
    private int assignmentNo;
    private String assignmentTitle;
    private String assignmentDescription;

    private String assignmentDeadline;

    private Integer courseNo;

    // 제출 정보
    private Integer assignmentSubmissionNo;
    private String assignmentSubmissionContent;
    private String assignmentSubmissionFileUrl;
    private Integer assignmentScore;

    private String submittedDate;

    // 상태 값
    private Boolean submitted;       // 제출 여부
    private Boolean deadlinePassed;  // 마감일 지났는지 여부
}
