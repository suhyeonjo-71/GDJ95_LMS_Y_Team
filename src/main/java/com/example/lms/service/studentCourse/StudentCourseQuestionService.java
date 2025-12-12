package com.example.lms.service.studentCourse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.dto.StudentCourseQuestionDetailDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.mapper.studentCourse.StudentCourseQuestionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentCourseQuestionService {

    private final StudentCourseQuestionMapper mapper;

    // 홈 화면 최근 질문 3개
    public List<StudentQuestionDTO> getRecentQuestions(
            int courseNo, int userNo, String userAuth) {

        List<CourseQuestionDTO> raw =
                mapper.selectRecentQuestionsForStudent(courseNo);

        return convert(raw, userNo, userAuth);
    }

    // 문의 개수-
    public int getTotalQuestionCount(int courseNo) {
        return mapper.countQuestion(courseNo);
    }

    // 페이징 목록 + 비밀글 처리
    public List<StudentQuestionDTO> getPagedQuestionList(
            int courseNo, SysUserDTO loginUser,
            int currentPage, int rowPerPage) {

        int startRow = (currentPage - 1) * rowPerPage;

        List<CourseQuestionDTO> raw =
                mapper.selectPagedQuestionList(courseNo, startRow, rowPerPage);

        // loginUser → userNo, userAuth 로 변환하여 convert() 호출
        List<StudentQuestionDTO> list =
                convert(raw, loginUser.getUserNo(), loginUser.getUserAuth());

        // 목록 번호 지정
        int index = startRow + 1;
        for (StudentQuestionDTO dto : list) {
            dto.setIndex(index++);
        }
        
        return list;
    }

    // 문의 상세 + 댓글 + 비밀글 처리
    public StudentCourseQuestionDetailDTO getQuestionDetail(int questionNo, SysUserDTO loginUser) {

        CourseQuestionDTO raw = mapper.selectQuestionDetail(questionNo);
        if (raw == null) return null;

        boolean isPrivate = raw.getPrivatePost() == 1;
        boolean isOwner = raw.getWriterUserNo() == loginUser.getUserNo();
        boolean isProf = loginUser.getUserAuth().equals("PROFESSOR")
                      || loginUser.getUserAuth().equals("ADMIN");

        boolean canView = !isPrivate || isOwner || isProf;

        StudentCourseQuestionDetailDTO dto = new StudentCourseQuestionDetailDTO();
        dto.setCourseQuestionNo(raw.getCourseQuestionNo());
        dto.setCourseNo(raw.getCourseNo());
        dto.setWriterUserNo(raw.getWriterUserNo());
        dto.setWriterName(canView ? raw.getWriterName() : "비공개");
        dto.setCreatedate(raw.getCreatedate());

        dto.setPrivatePost(raw.getPrivatePost());
        dto.setPrivatePostFlag(raw.getPrivatePost() == 1);

        dto.setCanView(canView);
        dto.setAnswered(raw.isAnswered());

        if (canView) {
            dto.setCourseQuestionTitle(raw.getCourseQuestionTitle());
            dto.setCourseQuestionContent(raw.getCourseQuestionContent());
            dto.setAnswerList(mapper.selectAnswerList(questionNo));
        } else {
            dto.setCourseQuestionTitle("비밀글입니다.");
            dto.setCourseQuestionContent(null);
            dto.setAnswerList(null);
        }

        return dto;
    }

    // 문의 등록
    public int insertQuestion(CourseQuestionDTO dto) {
        return mapper.insertQuestion(dto);
    }

    // 문의 수정
    public boolean updateQuestion(CourseQuestionDTO dto, SysUserDTO loginUser) {

        boolean isOwner = mapper.isOwner(dto.getCourseQuestionNo(), loginUser.getUserNo());
        if (!isOwner) return false;
        
        return mapper.updateQuestion(dto) == 1;
    }
    
    // 문의 삭제
    public boolean deleteQuestion(int questionNo, SysUserDTO loginUser) {

        boolean isOwner = mapper.isOwner(questionNo, loginUser.getUserNo());
        if (!isOwner) return false;

        // 1. 답변 먼저 삭제
        mapper.deleteAnswersByQuestionNo(questionNo);

        // 2. 질문 삭제
        return mapper.deleteQuestion(questionNo) == 1;
    }

    // 문의 → courseNo 조회 (리다이렉트용)
    public int getCourseNoByQuestion(int questionNo) {
        return mapper.selectCourseNoByQuestion(questionNo);
    }

    // 공통 변환 메서드 (비밀글 처리)
    private List<StudentQuestionDTO> convert(
            List<CourseQuestionDTO> raw,
            int userNo,
            String userAuth) {

        boolean isProf = "PROFESSOR".equalsIgnoreCase(userAuth)
                      || "ADMIN".equalsIgnoreCase(userAuth);

        List<StudentQuestionDTO> list = new ArrayList<>();

        for (CourseQuestionDTO q : raw) {
            boolean isPrivate = q.getPrivatePost() == 1;
            boolean isOwner = q.getWriterUserNo() == userNo;
            boolean canView = !isPrivate || isOwner || isProf;

            StudentQuestionDTO dto = new StudentQuestionDTO();
            dto.setQuestionNo(q.getCourseQuestionNo());
            dto.setWriterUserNo(q.getWriterUserNo());
            dto.setCreatedate(q.getCreatedate());
            dto.setAnswered(q.isAnswered());
            dto.setPrivatePostFlag(isPrivate);
            dto.setCanView(canView);

            if (canView) {
                dto.setQuestionTitle(q.getCourseQuestionTitle());
                dto.setWriterName(q.getWriterName());
            } else {
                dto.setQuestionTitle("비밀글입니다.");
                dto.setWriterName("비공개");
            }

            list.add(dto);
        }

        return list;
    }

}
