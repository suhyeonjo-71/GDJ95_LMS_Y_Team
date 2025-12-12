package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * TB_ATTENDANCE TABLE DTO
 * 출석 정보
 */
@Data
public class AttendanceDTO {

    private int attendanceNo;     // 출석번호 PK
    private int studentUserNo;    // 학생번호 FK
    private int courseNo;         // 강의번호 FK
    private int weekNo;        // 강의영상번호 FK
    private int attendanceStatus; // 0=출석, 1=결석, 2=지각
    private String createdate;    // 생성일시
    private String updatedate;    // 수정일시
}