package com.example.lms.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.CourseTimeDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.mapper.enrollment.EnrollmentCourseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentCourseService {

    private final EnrollmentCourseMapper mapper;

    public StudentCourseDetailDTO getCourseDetail(int courseNo) {

        StudentCourseDetailDTO dto = mapper.selectCourseDetail(courseNo);

        if (dto != null) {

            // 강의 시간 목록
            List<CourseTimeDTO> timeList = mapper.selectCourseTimeList(courseNo);
            dto.setCourseTimeList(timeList);

            // 현재 수강 인원
            int count = mapper.selectCurrentEnrollmentCount(courseNo);
            dto.setCurrentCount(count);
        }

        return dto;
    }
}
