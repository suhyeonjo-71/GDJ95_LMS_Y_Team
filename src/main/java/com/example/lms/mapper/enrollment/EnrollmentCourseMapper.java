package com.example.lms.mapper.enrollment;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.CourseTimeDTO;
import com.example.lms.dto.StudentCourseDetailDTO;

@Mapper
public interface EnrollmentCourseMapper {

    // 강의 기본 상세
    StudentCourseDetailDTO selectCourseDetail(int courseNo);

    // 강의 시간 목록
    List<CourseTimeDTO> selectCourseTimeList(int courseNo);

    // 현재 수강 인원
    int selectCurrentEnrollmentCount(int courseNo);
}
