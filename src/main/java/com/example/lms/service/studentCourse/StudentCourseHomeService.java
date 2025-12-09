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
    public List<StudentQuestionDTO> getRecentQuestionList(
            int courseNo, 
            int studentUserNo, 
            String userAuth // <-- 원인 해결: 세 번째 인자 String userAuth 받도록 수정
        ) {

            // 1. 매퍼 호출: courseNo만 넘기도록 수정 (Mapper 정의에 따름)
            List<StudentQuestionDTO> list = mapper.selectRecentQuestions(courseNo); 

            // 2. 권한 확인: 강사(PROFESSOR) 또는 관리자(ADMIN)인지 확인
            boolean isInstructorOrAdmin = "PROFESSOR".equals(userAuth) || "ADMIN".equals(userAuth); 

            for (StudentQuestionDTO q : list) {

                // 3. 비밀글 접근 권한 로직
            	boolean isPrivate = (q.getPrivatePost() != null && q.getPrivatePost() == 1);
                boolean isWriter = (q.getWriterUserNo() != null && q.getWriterUserNo() == studentUserNo);

                // 뷰 가능 조건: 비밀글이 아니거나 (!isPrivate) || 글 작성자이거나 (isWriter) || 강사/관리자일 경우 (isInstructorOrAdmin)
                boolean canView = !isPrivate || isWriter || isInstructorOrAdmin;
                q.setCanView(canView);

                // 4. 답변 여부
                boolean answered = (q.getAnswerCount() != null && q.getAnswerCount() > 0);
                q.setAnswered(answered);

                // 5. 비밀글이면 제목 가리기
                if (!canView) {
                    q.setQuestionTitle("비밀글입니다."); // q.setCourseQuestionTitle("비밀글입니다."); 도 가능, DTO 필드에 따라
                }
            }

            return list;
        }

}
