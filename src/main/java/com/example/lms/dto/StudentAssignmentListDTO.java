package com.example.lms.dto;

import lombok.Data;

@Data
public class StudentAssignmentListDTO {

    private int assignmentNo;
    private String assignmentTitle;
    private String assignmentDeadline;

    private Boolean submitted;        // 제출 여부

    private Integer courseNo;         // 과목 번호
}