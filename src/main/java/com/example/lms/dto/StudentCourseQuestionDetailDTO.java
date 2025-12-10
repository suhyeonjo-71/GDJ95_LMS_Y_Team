package com.example.lms.dto;

import java.util.List;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - yj
 * STUDENT COURSE QUESTION DETAIL DTO
 * 학생 강의 문의사항 상세 조회 정보
 */
@Data
public class StudentCourseQuestionDetailDTO {

    // 기본 질문 정보
    private int courseQuestionNo;      // 질문 번호 PK
    private int courseNo;              // 강의 번호 FK

    private int writerUserNo;          // 작성자 번호
    private String writerName;         // 작성자 이름

    private String questionTitle;      // 질문 제목
    private String questionContent;    // 질문 내용

    private boolean privatePost;       // 비밀글 여부
    private String createdate;         // 작성일

    // 화면(UI)용 필드
    private int index;                 // 목록 번호 (1~n)
    private boolean canView;           // 학생이 해당 글 열람 가능 여부

    // 답변 목록 (조회용)
    private List<CourseQuestionAnswerDTO> answerList;
}
