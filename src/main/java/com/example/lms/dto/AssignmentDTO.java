package com.example.lms.dto;

import lombok.Data;

/**
 * 2025. 11. 24.
 * Author - sh
 * tb_assignment TABLE DTO
 * 과제 정보
 */

@Data
public class AssignmentDTO {
	private int assignmentNo;   		  // 과제번호 PK
	private int courseNo;				  // 강의번호 FK
	private String assignmentTitle;       // 과제제목
	private String assignmentDescription; // 과제내용
	private String assignmentDueDate;     // 과제 마감일
	private int assignmentStatus;         // 0=비활성화, 1=활성화
	
	public boolean isOpened() {
	    return assignmentStatus == 1; 
	}

	public boolean isClosed() {
	    return assignmentStatus == 0; 
	}
	private String createdate;			  // 생성일시
	private String updatedate;			  // 수정일시
}

