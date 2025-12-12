package com.example.lms.service.studentCourse;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.mapper.studentCourse.StudentGradeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentGradeService {

    private final StudentGradeMapper mapper;

    // 홈 화면 요약용 성적 조회
    public StudentGradeDTO getStudentGradeSummary(int courseNo, int studentUserNo) {
        return mapper.selectStudentGradeSummary(courseNo, studentUserNo);
    }
}
