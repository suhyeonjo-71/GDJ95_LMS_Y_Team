package com.example.lms.mapper.studentCourse;

import org.apache.ibatis.annotations.Mapper;
import com.example.lms.dto.StudentCourseHomeDTO;

@Mapper
public interface StudentCourseHomeMapper {

    // 학생 강의 홈 기본 정보 조회
    StudentCourseHomeDTO selectCourseHome(int courseNo, int studentUserNo);
}
