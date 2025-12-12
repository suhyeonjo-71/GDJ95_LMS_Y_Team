package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT COURSE NOTICE DTO
 * 학생 과목 공지사항 조회 정보
 */
@Data
public class StudentCourseNoticeDTO {

    // 목록 출력용 index (1, 2, 3...)
    private Integer index;

    // 공지 기본 정보
    private Integer courseNoticeNo;
    private Integer courseNo;

    private String courseNoticeTitle;
    private String courseNoticeContent;

    // 작성자명
    private String writerUserName;

    // 등록일
    private String createdate;
}
