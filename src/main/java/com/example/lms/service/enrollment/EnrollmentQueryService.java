package com.example.lms.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentTimetableDTO;
import com.example.lms.mapper.enrollment.EnrollmentCourseQueryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentQueryService {

    private final EnrollmentCourseQueryMapper mapper;

    // 강의 목록 필터링 조회
    public List<StudentCourseDTO> getCourseListForEnrollment(
            int studentUserNo,
            Integer yoil,
            String professor,
            String deptCode,
            int startRow,
            int rowPerPage
    ) {
        return mapper.selectCourseListForStudentFiltered(
                studentUserNo, yoil, professor, deptCode, startRow, rowPerPage
        );
    }

    // 필터링 총 개수
    public int countFilteredCourseList(Integer yoil, String professor, String deptCode) {
        return mapper.countCourseListFiltered(yoil, professor, deptCode);
    }

    // 학과 목록 조회
    public List<DeptDTO> getDeptList() {
        return mapper.selectDeptList();
    }

    // 강의 상세 조회
    public StudentCourseDetailDTO getCourseDetail(int courseNo) {
        return mapper.selectStudentCourseDetail(courseNo);
    }

    // 학생 시간표
    public List<StudentTimetableDTO> getStudentTimetable(int studentUserNo) {
        return mapper.selectStudentTimetable(studentUserNo);
    }
}
