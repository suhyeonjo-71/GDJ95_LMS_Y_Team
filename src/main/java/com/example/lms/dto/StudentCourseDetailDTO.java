package com.example.lms.dto;

import java.util.List;
import lombok.Data;

/**
 * 강의 상세 전체 정보 DTO
 * - 강의 기본 정보
 * - 교수 정보 / 학과 정보
 * - 강의 시간표 (다중 요일/시간)
 * - 현재 수강 인원
 */
@Data
public class StudentCourseDetailDTO {

    // 기본 강의 정보
    private Integer courseNo;
    private String courseName;
    private Integer courseYear;
    private Integer courseSemester;
    private Integer courseScore;
    private Integer courseCapacity;
    private String courseDescription;
    private String classroom;

    // 교수/학과 정보
    private String professorName;
    private String deptName;

    // 강의 시간 목록(요일/시간)
    private List<CourseTimeDTO> courseTimeList;

    // 현재 수강 인원 (enrollmentStatus = 0)
    private Integer currentCount;

    // 편의 메서드 (월~금 이름 출력)
    public String getYoilName(int yoil) {
        return switch (yoil) {
            case 1 -> "월";
            case 2 -> "화";
            case 3 -> "수";
            case 4 -> "목";
            case 5 -> "금";
            default -> "";
        };
    }
}
