package com.example.lms.dto;

import lombok.Data;

@Data
public class ProfCourseAssignmentDTO {
	private int assignmentSubmissionNo;
    private int assignmentNo;
    private int writerUserNo;

    private String writerName;  
    private String writerId;     

    private String assignmentSubmissionFileUrl;
    private String assignmentSubmissionContent;
    private Integer assignmentScore;
    private int assignmentSubmissionStatus;
    private String createdate;
    private String updatedate;
    
    private int courseNo;
    
    public boolean getIsSubmitted() {
        return assignmentSubmissionStatus != 0;
    }

    public boolean getIsNotSubmitted() {
        return assignmentSubmissionStatus == 0;
    }
    
    // 메뉴 메인
    private String courseName;
    private int assignmentCount;
    private String lastesAssignmentTitle;
}