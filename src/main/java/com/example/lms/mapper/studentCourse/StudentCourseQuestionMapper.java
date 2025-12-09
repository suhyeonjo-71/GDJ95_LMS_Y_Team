package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.CourseQuestionDTO;

@Mapper
public interface StudentCourseQuestionMapper {

    // 페이징 목록
    List<CourseQuestionDTO> selectPagedQuestionList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 상세 조회
    CourseQuestionDTO selectQuestionDetail(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 리스트
    List<CourseQuestionAnswerDTO> selectAnswerList(@Param("courseQuestionNo") int courseQuestionNo);

    // INSERT
    int insertQuestion(CourseQuestionDTO dto);

    // 본인글 여부
    boolean isOwner(
            @Param("courseQuestionNo") int courseQuestionNo,
            @Param("userNo") int userNo);

    // UPDATE
    int updateQuestion(CourseQuestionDTO dto);

    // DELETE
    int deleteQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 INSERT
    int insertAnswer(CourseQuestionAnswerDTO dto);

    // 댓글 작성자 여부
    boolean isAnswerOwner(
            @Param("answerNo") int answerNo,
            @Param("profNo") int profNo);

    // 댓글 수정
    int updateAnswer(CourseQuestionAnswerDTO dto);

    // 특정 문의 글의 courseNo 조회
    int selectCourseNoByQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // 전체 개수
    int countQuestion(@Param("courseNo") int courseNo);


    // 🔥 빠져 있던 부분 (최근 질문 조회) → 반드시 추가!!!
    List<CourseQuestionDTO> selectRecentQuestionsForStudent(@Param("courseNo") int courseNo);
}
