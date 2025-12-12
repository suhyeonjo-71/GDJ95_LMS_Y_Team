package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.CourseTimeDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.mapper.studentCourse.StudentCourseInfoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseInfoService {

    private final StudentCourseInfoMapper mapper;

    // 📌 강의 기본 상세 + 시간표 + 현재 수강 인원 조회 (학생/수강신청 공통 조회)
    public StudentCourseDetailDTO getCourseDetail(int courseNo) {

        StudentCourseDetailDTO dto = mapper.selectCourseDetail(courseNo);
        if (dto == null) return null;

        // 강의 시간 목록
        List<CourseTimeDTO> timeList = mapper.selectCourseTimeList(courseNo);
        dto.setCourseTimeList(timeList);

        // 현재 수강 인원
        int count = mapper.selectCurrentEnrollmentCount(courseNo);
        dto.setCurrentCount(count);

        return dto;
    }
}
