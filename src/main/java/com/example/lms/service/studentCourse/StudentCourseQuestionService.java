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

    // ===============================
    // 🔥 문의 개수 (페이징용)
    // ===============================
    public int getTotalQuestionCount(int courseNo) {
        return mapper.countQuestion(courseNo);
    }

    // ===============================
    // 🔥 목록 + 번호 + 비밀글 처리
    // ===============================
    public List<CourseQuestionDTO> getPagedQuestionList(
            int courseNo, SysUserDTO loginUser, int currentPage, int rowPerPage) {

        int startRow = (currentPage - 1) * rowPerPage;

        List<CourseQuestionDTO> list =
                mapper.selectPagedQuestionList(courseNo, startRow, rowPerPage);

        boolean isProfessor = "PROFESSOR".equalsIgnoreCase(loginUser.getUserAuth());
        int displayIndex = startRow + 1;

        System.out.println("====== LIST DEBUG ======");
        System.out.println("loginUserNo : " + loginUser.getUserNo());
        System.out.println("isProfessor : " + isProfessor);

        for (CourseQuestionDTO q : list) {

            q.setIndex(displayIndex++);

            boolean privateFlag = q.isPrivatePost();  // DTO의 변환 메소드 사용
            boolean isOwner = q.getWriterUserNo() == loginUser.getUserNo();
            boolean canView = !privateFlag || isOwner || isProfessor;
            
            q.setCanView(canView);

            System.out.println("-------------------------");
            System.out.println("[ROW]");
            System.out.println("  questionNo     : " + q.getCourseQuestionNo());
            System.out.println("  writerUserNo   : " + q.getWriterUserNo());
            System.out.println("  privatePost(DB): " + q.getPrivatePost());
            System.out.println("  privateFlag    : " + privateFlag);
            System.out.println("  answered       : " + q.isAnswered());
            System.out.println("  isOwner        : " + isOwner);
            System.out.println("  canView        : " + canView);

            if (!canView) {
                q.setCourseQuestionTitle("비밀글입니다.");
                q.setWriterName("비공개");
            }
        }

        System.out.println("====== END LIST DEBUG ======");

        return list;
    }

    // ===============================
    // 🔥 상세 조회 + 답변 리스트 + 비밀글 처리
    // ===============================
    public CourseQuestionDTO getQuestionDetail(int courseQuestionNo, SysUserDTO loginUser) {

        CourseQuestionDTO question = mapper.selectQuestionDetail(courseQuestionNo);
        List<CourseQuestionAnswerDTO> answerList = mapper.selectAnswerList(courseQuestionNo);

        question.setAnswerList(answerList);

        boolean isProfessor = "PROFESSOR".equalsIgnoreCase(loginUser.getUserAuth());
        boolean isOwner = question.getWriterUserNo() == loginUser.getUserNo();

        // privatePost → Boolean 변환
        boolean privateFlag = (question.getPrivatePost() == 1);
        question.setPrivatePostFlag(privateFlag);

        boolean canView = !privateFlag || isOwner || isProfessor;

        question.setCanView(canView);

        // 디버그 출력
        System.out.println("======= QUESTION DETAIL DEBUG =======");
        System.out.println("Q_NO        : " + question.getCourseQuestionNo());
        System.out.println("writerUserNo: " + question.getWriterUserNo());
        System.out.println("loginUserNo : " + loginUser.getUserNo());
        System.out.println("privatePost : " + question.getPrivatePost());
        System.out.println("privateFlag : " + privateFlag);
        System.out.println("isOwner     : " + isOwner);
        System.out.println("isProfessor : " + isProfessor);
        System.out.println("→ canView   : " + canView);
        System.out.println("=====================================");

        if (!canView) {
            question.setCourseQuestionTitle("비밀글입니다.");
            question.setWriterName("비공개");
            question.setCourseQuestionContent(null);
        }

        return question;
    }

    // ===============================
    // 등록
    // ===============================
    public int insertQuestion(CourseQuestionDTO dto) {
        return mapper.insertQuestion(dto);
    }

    // ===============================
    // 수정
    // ===============================
    public boolean updateQuestion(CourseQuestionDTO dto, SysUserDTO loginUser) {

        boolean isOwner =
                mapper.isOwner(dto.getCourseQuestionNo(), loginUser.getUserNo());

        if (!isOwner) return false;

        return mapper.updateQuestion(dto) == 1;
    }

    // ===============================
    // 삭제
    // ===============================
    public boolean deleteQuestion(int courseQuestionNo, SysUserDTO loginUser) {

        boolean isOwner =
                mapper.isOwner(courseQuestionNo, loginUser.getUserNo());

        if (!isOwner) return false;

        return mapper.deleteQuestion(courseQuestionNo) == 1;
    }

    // ===============================
    // courseNo 조회 (리다이렉트용)
    // ===============================
    public int getCourseNoByQuestion(int courseQuestionNo) {
        return mapper.selectCourseNoByQuestion(courseQuestionNo);
    }
}
