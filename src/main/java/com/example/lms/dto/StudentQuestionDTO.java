package com.example.lms.dto;

import lombok.Data;

@Data
public class StudentQuestionDTO {

    private Integer questionNo;   // 질문 번호
    private String  questionTitle;// 질문 제목
    private String  createdate;   // 질문 작성일 (String으로 받아도 됨)
    private Boolean answered;     // 답변 여부 (true = 답변 있음)
    
    private Integer answerCount;   

    private Integer privatePost;
    public boolean isPrivatePost() {
        return privatePost == 1;
    }

    private Integer writerUserNo;    // 작성자
    private Boolean canView;         // 열람 가능 여부
}