package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.StudentAttendanceDTO;

@Mapper
public interface StudentCourseAttendanceMapper {

    // 회차별 출석 상세 조회
    List<StudentAttendanceDTO> selectAttendanceDetailList(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 출석 요약
    AttendanceSummaryDTO selectAttendanceSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);
}
