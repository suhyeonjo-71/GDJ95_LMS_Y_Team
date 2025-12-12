package com.example.lms.dto;

import java.util.List;

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
    private Integer questionNo;        
    private String  questionTitle;     
    private String  questionContent;
    private String  createdate;

    // 답변 여부
    private Boolean answered;          

    // 비밀글 여부
    private Integer privatePost;
    public boolean isPrivatePost() {
        return privatePost != null && privatePost == 1;
    }

    private Boolean privatePostFlag;
    private Boolean canView;

    // 작성자 정보
    private Integer writerUserNo;
    private String writerName;

    // 상세 조회용 댓글 리스트
    private List<CourseQuestionAnswerDTO> answerList;

    // 목록 표시용 index (옵션)
    private Integer index;
}
