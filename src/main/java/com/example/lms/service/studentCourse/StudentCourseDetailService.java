package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.CourseTimeDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.mapper.studentCourse.StudentCourseDetailMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseDetailService {

    private final StudentCourseDetailMapper mapper;

    // 강의 상세정보 조회 (시간목록 + 현재 신청인원 포함)
    public StudentCourseDetailDTO getCourseDetail(int courseNo) {

        System.out.println("DEBUG >>> getCourseDetail() 호출됨");
        System.out.println("DEBUG >>> courseNo = " + courseNo);

        StudentCourseDetailDTO dto = mapper.selectCourseDetail(courseNo);
        System.out.println("DEBUG >>> selectCourseDetail 결과 = " + dto);

        if (dto != null) {

            // 강의 시간 목록 조회
            List<CourseTimeDTO> timeList = mapper.selectCourseTimeList(courseNo);
            dto.setCourseTimeList(timeList);
            System.out.println("DEBUG >>> timeList 조회 = " + timeList);

            // 현재 수강 인원 조회
            int count = mapper.selectCurrentEnrollmentCount(courseNo);
            dto.setCurrentCount(count);
            System.out.println("DEBUG >>> currentCount = " + count);
        }

        return dto;
    }
}
