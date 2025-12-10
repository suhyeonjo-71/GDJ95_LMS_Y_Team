package com.example.lms.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * 
 * 2025. 12. 02. 
 * Author - jm
 * 내정보 - 학점관리 통합 DTO
 */
@Data
public class CourseManagementDTO {
	
		//TB_COURSE
		private int courseNo;            // 강의번호 PK
		private int professorUserNo;     // 교수번호 FK
		private int courseYear;          // 개설년도
		private int courseSemester;      // 학기
		private String courseName;       // 강의명
		private String courseDescription;// 강의 설명
		private int courseCapacity;      // 최대 수강 가능 인원
		private int courseScore;         // 이수 학점
		private String courseClassroom;  // 강의실
		private int courseStatus;        // 0=종료, 1=개설
		private String deptCode;         // 강의 전공
		private String createdate;       // 생성일시
		private String updatedate;       // 수정일시
		
		//TB_SYSUSER
		private int userNo; //사용자번호
		private String userAuth; //사용자권한
		private String userName; //사용자명
		private String userPhone; //사용자연락처
		private String deptName; //부서명
		 
		// TB_SYSAUTH
		private String authCode; //사용자 권한코드
		private String authName; //권한명
		  
		// TB_SYSAUTH_DETAIL
		private String authDetailCode; //사용자 세부 권한코드
		private String authDetailName; //세부 권한명
		
		// TB_GRADE
		private int gradeNo;		// 성적 PK
		private int studentUserNo;	// 성적 대상 학생 FK
		private String gradeValue;	// 성적표기 (등급 A+, B0 등 성적 표기 )
		private double gradeFinalScore; //최종점수
		
		// 쿼리 순번 시 사용 불필요.
		private Integer rownm;
		
		// 사용자명 별칭 나눔
		private String studentUserName; // 학생이름
		private String professorUserName; //교수이름

		// 조회 카테고리 셀렉박스용
		private String courseYearSemester; // 연도별-학기별
		
		// 개설코드한글명 필요함
		private String courseStatusName; // 0 종료 , 1 개설
		
		// 평가점수 등록 안됬을때 사용해야함
		private String gradeFinalScoreNullChk;
}
