package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT COURSE QUESTION LIST DTO
 * 학생 문의사항 목록 조회 정보
 */
@Data
public class StudentQuestionDTO {

    // 기본 질문 정보
    private Integer questionNo;        // 질문 번호
    private String  questionTitle;     // 질문 제목
    private String  createdate;        // 작성일

    // 답변 관련 정보
    private Boolean answered;          // 답변 여부 (true = 답변 있음)

    // 비밀글 여부 (0=공개,1=비공개)
    private Integer privatePost;
    public boolean isPrivatePost() {
        return privatePost != null && privatePost == 1;
    }
    
    // 비밀글 여부 Boolean (템플릿용)
    private Boolean privatePostFlag;
    
    // 작성자 및 권한
    private Integer writerUserNo;      // 작성자 번호
    private String writerName;
    private Boolean canView;           // 학생이 열람 가능한지 여부
}
