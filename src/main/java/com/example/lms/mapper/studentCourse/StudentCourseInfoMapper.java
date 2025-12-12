package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.CourseTimeDTO;
import com.example.lms.dto.StudentCourseDetailDTO;

@Mapper
public interface StudentCourseInfoMapper {

    // 강의 상세 기본 정보
    StudentCourseDetailDTO selectCourseDetail(int courseNo);

    // 강의 시간 목록
    List<CourseTimeDTO> selectCourseTimeList(int courseNo);

    // 현재 신청 인원
    int selectCurrentEnrollmentCount(int courseNo);
}
