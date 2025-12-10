package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.dto.StudentQuestionDTO;

// 학생 문의사항(Q&A) 처리 Mapper
@Mapper
public interface StudentCourseQuestionMapper {

    // 문의사항 목록 조회 (페이징)
    List<CourseQuestionDTO> selectPagedQuestionList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 문의사항 상세 조회
    CourseQuestionDTO selectQuestionDetail(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 리스트 조회
    List<CourseQuestionAnswerDTO> selectAnswerList(@Param("courseQuestionNo") int courseQuestionNo);

    // 문의사항 생성
    int insertQuestion(CourseQuestionDTO dto);

    // 본인 글 여부 확인
    boolean isOwner(
            @Param("courseQuestionNo") int courseQuestionNo,
            @Param("userNo") int userNo);

    // 문의사항 수정
    int updateQuestion(CourseQuestionDTO dto);

    // 문의사항 삭제
    int deleteQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 생성
    int insertAnswer(CourseQuestionAnswerDTO dto);

    // 댓글 본인 여부 확인
    boolean isAnswerOwner(
            @Param("answerNo") int answerNo,
            @Param("profNo") int profNo);

    // 댓글 수정
    int updateAnswer(CourseQuestionAnswerDTO dto);

    // 특정 문의글의 courseNo 조회
    int selectCourseNoByQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // 문의사항 전체 개수 조회
    int countQuestion(@Param("courseNo") int courseNo);

    // 최근 질문 조회 (학생 홈 화면용)
    List<StudentQuestionDTO> selectRecentQuestionsForStudent(@Param("courseNo") int courseNo);
}
