package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.mapper.studentCourse.StudentCourseQuestionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentCourseQuestionService {

    private final StudentCourseQuestionMapper mapper;

    // 문의 개수 (페이징용)
    public int getTotalQuestionCount(int courseNo) {
        return mapper.countQuestion(courseNo);
    }

    // 문의 목록 + 번호 매기기 + 비밀글 권한 처리
    public List<CourseQuestionDTO> getPagedQuestionList(
            int courseNo, SysUserDTO loginUser, int currentPage, int rowPerPage) {

        int startRow = (currentPage - 1) * rowPerPage;

        List<CourseQuestionDTO> list =
                mapper.selectPagedQuestionList(courseNo, startRow, rowPerPage);

        boolean isProfessor = "PROFESSOR".equalsIgnoreCase(loginUser.getUserAuth());

        int displayIndex = startRow + 1;

        for (CourseQuestionDTO q : list) {

            q.setIndex(displayIndex++);

            boolean isOwner = q.getWriterUserNo() == loginUser.getUserNo();
            boolean canView = !q.isPrivatePost() || isOwner || isProfessor;

            q.setCanView(canView);

            if (!canView) {
                q.setCourseQuestionTitle("비밀글입니다.");
                q.setWriterName("비공개");
                q.setCourseQuestionContent(null);
            }
        }

        return list;
    }

    // ====================================================================
    // 📌 문의 상세 + 답변 목록 + 비밀글 권한 처리
    // ====================================================================
    public CourseQuestionDTO getQuestionDetail(int courseQuestionNo, SysUserDTO loginUser) {

        CourseQuestionDTO question = mapper.selectQuestionDetail(courseQuestionNo);
        List<CourseQuestionAnswerDTO> answerList = mapper.selectAnswerList(courseQuestionNo);

        question.setAnswerList(answerList);

        boolean isProfessor = "PROFESSOR".equalsIgnoreCase(loginUser.getUserAuth());
        boolean isOwner = question.getWriterUserNo() == loginUser.getUserNo();

        boolean canView = !question.isPrivatePost() || isOwner || isProfessor;
        question.setCanView(canView);

        if (!canView) {
            question.setCourseQuestionTitle("비밀글입니다.");
            question.setWriterName("비공개");
            question.setCourseQuestionContent(null);
        }

        return question;
    }

    // ====================================================================
    // 📌 문의글 작성 (학생)
    // ====================================================================
    public int insertQuestion(CourseQuestionDTO dto) {
        return mapper.insertQuestion(dto);
    }

    // ====================================================================
    // 📌 문의글 수정 (작성자만)
    // ====================================================================
    public boolean updateQuestion(CourseQuestionDTO dto, SysUserDTO loginUser) {

        boolean isOwner =
                mapper.isOwner(dto.getCourseQuestionNo(), loginUser.getUserNo());

        if (!isOwner) return false;

        return mapper.updateQuestion(dto) == 1;
    }

    // ====================================================================
    // 📌 문의글 삭제 (작성자만)
    // ====================================================================
    public boolean deleteQuestion(int courseQuestionNo, SysUserDTO loginUser) {

        boolean isOwner =
                mapper.isOwner(courseQuestionNo, loginUser.getUserNo());

        if (!isOwner) return false;

        return mapper.deleteQuestion(courseQuestionNo) == 1;
    }

    // ====================================================================
    // 📌 courseNo 조회 (삭제/리다이렉트용)
    // ====================================================================
    public int getCourseNoByQuestion(int courseQuestionNo) {
        return mapper.selectCourseNoByQuestion(courseQuestionNo);
    }
}
