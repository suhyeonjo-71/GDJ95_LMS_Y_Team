package com.example.lms.service.studentCourse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.mapper.studentCourse.StudentCourseHomeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseHomeService {

    private final StudentCourseHomeMapper homeMapper;

    // 각 기능 요약 데이터 서비스
    private final StudentCourseNoticeService noticeService;
    private final StudentAssignmentService assignmentService;
    private final StudentCourseAttendanceService attendanceService;
    private final StudentGradeService gradeService;
    private final StudentCourseQuestionService questionService;

    public StudentCourseHomeDTO getStudentCourseHome(
            int courseNo, int studentUserNo, String userAuth) {

        StudentCourseHomeDTO dto = homeMapper.selectCourseHome(courseNo, studentUserNo);
        if (dto == null) return null;

        // 서브네비 활성 탭 세팅
        dto.setNavHome(true);

        // 요약 데이터 조립
        dto.setNoticeList(noticeService.getRecentNotices(courseNo));
        dto.setAssignmentList(assignmentService.getRecentAssignments(courseNo, studentUserNo));
        dto.setAttendanceSummary(attendanceService.getAttendanceSummary(courseNo, studentUserNo));
        dto.setGradeSummary(gradeService.getStudentGradeSummary(courseNo, studentUserNo));
        dto.setQuestionList(
        	    questionService.getRecentQuestions(courseNo, studentUserNo, userAuth)
        	);

        return dto;
    }
}
