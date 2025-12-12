package com.example.lms.dto;

import lombok.Data;
/**
 * 2025. 11. 24.
 * Author - yj
 * TB_COURSE_QUESTION_ANSWER TABLE DTO
 * 강의 문의사항 답변 정보
 */
@Data
public class CourseQuestionAnswerDTO {

    // 댓글 PK
    private int answerNo;

    // 어떤 질문의 댓글인지 FK
    private int courseQuestionNo;

    // 작성자 정보
    private int writerUserNo;
    private String writerName;
    private String writerRole; // PROFESSOR만 댓글 작성 가능

    // 댓글 내용
    private String answerContent;
    private String createdate;

    // 교수 본인 댓글인지
    private boolean answerOwner;
}
