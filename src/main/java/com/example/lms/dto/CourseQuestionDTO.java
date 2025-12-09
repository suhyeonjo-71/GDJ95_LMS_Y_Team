package com.example.lms.dto;

import java.util.List;
import lombok.Data;

@Data
public class CourseQuestionDTO {

    // 문의번호 PK
    private int courseQuestionNo;

    // 강의번호 FK
    private int courseNo;

    // 작성자 정보
    private int writerUserNo;
    private String writerName;
    private String writerRole; // STUDENT / PROFESSOR

    // 제목/내용
    private String courseQuestionTitle;
    private String courseQuestionContent;

    // 상태값
    private String courseQuestionStatus; // 0=미답변, 1=답변완료
    private String createdate;

    // ---- Boolean 관련 필드 ----

    // DB의 0/1 값을 그대로 받는 필드
    private int privatePost;

    // 실제 비밀글 여부(Boolean)
    private boolean privatePostFlag;

    // 답변 여부
    private boolean answered;

    // 열람 가능 여부
    private boolean canView;

    // 작성자 본인 여부
    private boolean owner;

    // 교수 여부
    private boolean professor;

    // 댓글 리스트
    private List<CourseQuestionAnswerDTO> answerList;

    // 화면 표시용 번호
    private int index;

    // 기존 방식 유지 (호환용)
    public boolean isPrivatePost() {
        return privatePost == 1;
    }
}
