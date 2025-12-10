package com.example.lms.mapper.studentCourse;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.StudentGradeDTO;

// 학생 성적 조회 Mapper
@Mapper
public interface StudentGradeMapper {

    // 학생 성적 조회
    StudentGradeDTO selectStudentGrade(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);
}
