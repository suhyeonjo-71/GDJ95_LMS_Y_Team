package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.StudentAttendanceDTO;

// 학생 출석 상세 및 요약 조회 Mapper
@Mapper
public interface StudentCourseAttendanceMapper {

    // 학생 주차별 출석 상세 리스트 조회
    List<StudentAttendanceDTO> selectAttendanceDetailList(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 학생 출석 요약 정보 조회 (출석률 등)
    AttendanceSummaryDTO selectAttendanceSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);
}
