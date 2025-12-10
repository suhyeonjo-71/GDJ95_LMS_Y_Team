package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.mapper.studentCourse.StudentCourseNoticeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseNoticeService {

    private final StudentCourseNoticeMapper mapper;
    private final StudentCourseHomeService courseHomeService;

    // 강의 홈 정보 조회
    public StudentCourseHomeDTO getStudentCourseHome(int courseNo, int studentUserNo) {
        return courseHomeService.getStudentCourseHome(courseNo, studentUserNo);
    }

    // 공지 목록 조회 (페이징)
    public List<StudentCourseNoticeDTO> getNoticeList(int courseNo, int startRow, int rowPerPage) {
        return mapper.selectNoticeList(courseNo, startRow, rowPerPage);
    }

    // 공지 상세 조회
    public StudentCourseNoticeDTO getStudentCourseNoticeDetail(int courseNoticeNo) {
        return mapper.selectNoticeDetail(courseNoticeNo);
    }

    // 공지 총 개수 조회
    public int getNoticeTotal(int courseNo) {
        return mapper.selectNoticeTotal(courseNo);
    }
}
