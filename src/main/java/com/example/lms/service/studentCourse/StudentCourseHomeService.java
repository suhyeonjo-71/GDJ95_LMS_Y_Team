package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.mapper.studentCourse.StudentCourseHomeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseHomeService {

    private final StudentCourseHomeMapper mapper;

    // ----------------------------
    // 강의 홈 정보 조회
    // ----------------------------
    public StudentCourseHomeDTO getStudentCourseHome(int courseNo, int studentUserNo) {
        return mapper.selectCourseHome(courseNo, studentUserNo);
    }

    // ----------------------------
    // 최근 공지
    // ----------------------------
    public List<StudentCourseNoticeDTO> getRecentNotices(int courseNo) {
        return mapper.selectRecentNotices(courseNo);
    }

    // ----------------------------
    // 최근 과제 요약
    // ----------------------------
    public List<StudentAssignmentListDTO> getRecentAssignment(int courseNo, int studentUserNo) {
        return mapper.selectRecentAssignment(courseNo, studentUserNo);
    }

    // ----------------------------
    // 출석 요약
    // ----------------------------
    public AttendanceSummaryDTO getAttendanceSummary(int courseNo, int studentUserNo) {
        return mapper.selectAttendanceSummary(courseNo, studentUserNo);
    }

    // ----------------------------
    // 성적 요약
    // ----------------------------
    public StudentGradeDTO getStudentGradeSummary(int courseNo, int studentUserNo) {
        return mapper.selectStudentGradeSummary(courseNo, studentUserNo);
    }

	 // ----------------------------
	 // 최근 질문
	 // ----------------------------
	 public List<StudentQuestionDTO> getRecentQuestionList(int courseNo, int studentUserNo, String loginUserRole) {
	
	     List<StudentQuestionDTO> list = mapper.selectRecentQuestionList(courseNo, studentUserNo);
	
	     for (StudentQuestionDTO q : list) {
	
	         boolean isPrivate = Boolean.TRUE.equals(q.getPrivatePost());
	         boolean isWriter = q.getWriterUserNo() == studentUserNo;
	         boolean isProfessor = "PROFESSOR".equals(loginUserRole);
	
	         boolean canView = !isPrivate || isWriter || isProfessor;
	         
	         q.setCanView(canView);
	         
	         // 🔥 비공개인데 볼 권한 없으면 제목 가리기
	         if (!canView) {
	             q.setQuestionTitle("비밀글입니다.");
	         }
	     }
	
	     return list;
	 }

}
