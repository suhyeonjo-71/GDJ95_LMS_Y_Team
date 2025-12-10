package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentTimetableDTO;

// 학생 수강 목록 및 시간표 조회 Mapper
@Mapper
public interface StudentCourseBaseMapper {

    // 내 수강 과목 목록 조회
    List<StudentCourseDTO> selectMyCourseList(int studentUserNo);

    // 학생 시간표 조회
    List<StudentTimetableDTO> selectStudentTimetable(int studentUserNo);
}
