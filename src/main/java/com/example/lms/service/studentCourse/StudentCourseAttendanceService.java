package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.StudentAttendanceDTO;
import com.example.lms.mapper.studentCourse.StudentCourseAttendanceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseAttendanceService {

    private final StudentCourseAttendanceMapper mapper;

    // 회차별 출석 상세 조회
    public List<StudentAttendanceDTO> getAttendanceDetailList(int courseNo, int studentUserNo) {
        return mapper.selectAttendanceDetailList(courseNo, studentUserNo);
    }

    // 출석 요약 조회
    public AttendanceSummaryDTO getAttendanceSummary(int courseNo, int studentUserNo) {
        return mapper.selectAttendanceSummary(courseNo, studentUserNo);
    }
}
