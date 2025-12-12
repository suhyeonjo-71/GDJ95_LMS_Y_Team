package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.CourseQuestionDTO;

@Mapper
public interface StudentCourseQuestionMapper {

    // 최근 문의 (학생 홈 화면)
    List<CourseQuestionDTO> selectRecentQuestionsForStudent(@Param("courseNo") int courseNo);

    // 문의 목록 (페이징)
    List<CourseQuestionDTO> selectPagedQuestionList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 문의 총 개수
    int countQuestion(@Param("courseNo") int courseNo);

    // 문의 상세
    CourseQuestionDTO selectQuestionDetail(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 리스트
    List<CourseQuestionAnswerDTO> selectAnswerList(@Param("courseQuestionNo") int courseQuestionNo);

    // 문의 생성
    int insertQuestion(CourseQuestionDTO dto);

    // 작성자 여부 확인
    boolean isOwner(
            @Param("courseQuestionNo") int courseQuestionNo,
            @Param("userNo") int userNo);

    // 문의 수정
    int updateQuestion(CourseQuestionDTO dto);

    // 문의 삭제
    int deleteQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // courseNo 역추적
    int selectCourseNoByQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    // 댓글 작성자 여부
    boolean isAnswerOwner(
            @Param("answerNo") int answerNo,
            @Param("profNo") int profNo);

    // 댓글 생성
    int insertAnswer(CourseQuestionAnswerDTO dto);

    // 댓글 수정
    int updateAnswer(CourseQuestionAnswerDTO dto);

	void deleteAnswersByQuestionNo(int questionNo);
}
