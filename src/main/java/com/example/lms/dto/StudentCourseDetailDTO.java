package com.example.lms.dto;

import java.util.List;

import lombok.Data;
/**
 * 2025. 11. 28.
 * Author - yj
 * 학생 강의 상세보기 DTO
 * TB_COURSE / TB_SYSUSER / TB_DEPT / TB_COURSE_TIME JOIN 결과
 */

@Data
public class StudentCourseDetailDTO {
	private int courseNo;           // 강의번호 (PK)
    private String courseName;      // 강의명
    private int courseYear;         // 개설년도
    private int courseSemester;     // 학기 (1 또는 2)
    private int courseScore;        // 학점
    private int courseCapacity;     // 정원

    private String courseDescription;   // 강의 설명
    private String courseClassroom;           // 강의실
    private String deptName;            // 학과명

    private String professorName;       // 담당 교수명

    private List<CourseTimeDTO> courseTimeList;   // 강의 시간 정보 리스트
    
    private int currentCount;   // 현재 신청 인원
	
}