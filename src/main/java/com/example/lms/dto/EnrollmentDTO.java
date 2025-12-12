package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * ENROLLMENT TABLE DTO
 * 학생 수강신청 정보
 */
@Data
public class EnrollmentDTO {

    private Integer enrollmentNo;    // 수강신청번호 PK
    private Integer studentUserNo;       // 학생번호 (FK)
    private Integer courseNo;            // 강의번호 (FK)
    private Integer enrollmentStatus; // 0=신청, 1=취소
    
    private String createdate;       // 신청일시
    private String updatedate;       // 수정일시
}