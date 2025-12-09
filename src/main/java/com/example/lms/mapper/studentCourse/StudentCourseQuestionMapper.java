package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.CourseQuestionDTO;

@Mapper
public interface StudentCourseQuestionMapper {

    List<CourseQuestionDTO> selectPagedQuestionList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    CourseQuestionDTO selectQuestionDetail(@Param("courseQuestionNo") int courseQuestionNo);

    List<CourseQuestionAnswerDTO> selectAnswerList(@Param("courseQuestionNo") int courseQuestionNo);

    int insertQuestion(CourseQuestionDTO dto);

    boolean isOwner(
            @Param("courseQuestionNo") int courseQuestionNo,
            @Param("userNo") int userNo);

    int updateQuestion(CourseQuestionDTO dto);

    int deleteQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    int insertAnswer(CourseQuestionAnswerDTO dto);

    boolean isAnswerOwner(
            @Param("answerNo") int answerNo,
            @Param("profNo") int profNo);

    int updateAnswer(CourseQuestionAnswerDTO dto);

    int selectCourseNoByQuestion(@Param("courseQuestionNo") int courseQuestionNo);

    int countQuestion(@Param("courseNo") int courseNo);

}
