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

    private final StudentCourseHomeMapper mapper;

    // 강의 홈 정보 조회
    public StudentCourseHomeDTO getStudentCourseHome(int courseNo, int studentUserNo) {
        return mapper.selectCourseHome(courseNo, studentUserNo);
    }

    // 최근 공지 조회
    public List<StudentCourseNoticeDTO> getRecentNotices(int courseNo) {
        return mapper.selectRecentNotices(courseNo);
    }

    // 최근 과제 조회
    public List<StudentAssignmentListDTO> getRecentAssignment(int courseNo, int studentUserNo) {
        return mapper.selectRecentAssignment(courseNo, studentUserNo);
    }

    // 출석 요약 조회
    public AttendanceSummaryDTO getAttendanceSummary(int courseNo, int studentUserNo) {
        return mapper.selectAttendanceSummary(courseNo, studentUserNo);
    }

    // 성적 요약 조회
    public StudentGradeDTO getStudentGradeSummary(int courseNo, int studentUserNo) {
        return mapper.selectStudentGradeSummary(courseNo, studentUserNo);
    }

    // 최근 질문 조회 + 비밀글 처리
    public List<StudentQuestionDTO> getRecentQuestionList(
            int courseNo, int studentUserNo, String userAuth) {

        List<CourseQuestionDTO> rawList = mapper.selectRecentQuestionsForStudent(courseNo);
        List<StudentQuestionDTO> result = new ArrayList<>();

        boolean isProfOrAdmin = "PROFESSOR".equals(userAuth) || "ADMIN".equals(userAuth);

        for (CourseQuestionDTO q : rawList) {

            StudentQuestionDTO dto = new StudentQuestionDTO();

            // 기본 정보
            dto.setQuestionNo(q.getCourseQuestionNo());
            dto.setQuestionTitle(q.getCourseQuestionTitle());
            dto.setCreatedate(q.getCreatedate());
            dto.setWriterUserNo(q.getWriterUserNo());
            dto.setWriterName(q.getWriterName());
            dto.setAnswered(q.isAnswered());
            dto.setPrivatePost(q.getPrivatePost());

            boolean isPrivate = q.getPrivatePost() == 1;
            boolean isWriter = q.getWriterUserNo() != null && q.getWriterUserNo() == studentUserNo;

            boolean canView = !isPrivate || isWriter || isProfOrAdmin;
            dto.setCanView(canView);
            dto.setPrivatePostFlag(isPrivate);

            // 비밀글인데 권한 없는 학생일 경우
            if (isPrivate && !canView) {
                dto.setQuestionTitle("비밀글입니다.");
                dto.setWriterName(null);
            }

            result.add(dto);
        }

        return result;
    }


}
