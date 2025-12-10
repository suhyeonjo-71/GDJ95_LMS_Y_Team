package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentTimetableDTO;
import com.example.lms.mapper.studentCourse.StudentCourseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseBaseService {

    private final StudentCourseMapper mapper;

    // 내 수강 과목 목록 조회
    public List<StudentCourseDTO> getMyCourseList(int studentUserNo) {
        return mapper.selectMyCourseList(studentUserNo);
    }

    // 학생 시간표 조회
    public List<StudentTimetableDTO> getStudentTimetable(int studentUserNo) {
        return mapper.selectStudentTimetable(studentUserNo);
    }
}
