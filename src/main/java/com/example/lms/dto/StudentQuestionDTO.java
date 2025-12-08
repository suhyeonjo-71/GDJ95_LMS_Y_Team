package com.example.lms.dto;

import lombok.Data;

/**
 * 학생 질문(Q&A) 요약 DTO
 * TB_COURSE_QUESTION / TB_COURSE_QUESTION_ANSWER 조회용
 */
@Data
public class StudentQuestionDTO {

    private Integer questionNo;   // 질문 번호
    private String  questionTitle;// 질문 제목
    private String  createdate;   // 질문 작성일 (String으로 받아도 됨)
    private Boolean answered;     // 답변 여부 (true = 답변 있음)
    
    private Integer answerCount;   

    private Boolean privatePost;     // 비밀글 여부
    private Integer writerUserNo;    // 작성자
    private Boolean canView;         // 열람 가능 여부
}