package com.example.lms.service.studentCourse;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.mapper.studentCourse.StudentGradeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentGradeService {

    private final StudentGradeMapper mapper;

    // 학생 성적 조회
    public StudentGradeDTO getStudentGrade(int courseNo, int studentUserNo) {
        return mapper.selectStudentGrade(courseNo, studentUserNo);
    }
}
