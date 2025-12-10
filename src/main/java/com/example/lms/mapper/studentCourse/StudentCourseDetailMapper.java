package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.CourseTimeDTO;

// 학생 강의 상세 조회 Mapper
@Mapper
public interface StudentCourseDetailMapper {

    // 강의 기본 상세 정보 조회
    StudentCourseDetailDTO selectCourseDetail(int courseNo);

    // 강의 시간표(요일/교시) 목록 조회
    List<CourseTimeDTO> selectCourseTimeList(int courseNo);

    // 해당 강의의 현재 수강 인원 조회
    int selectCurrentEnrollmentCount(int courseNo);
}
