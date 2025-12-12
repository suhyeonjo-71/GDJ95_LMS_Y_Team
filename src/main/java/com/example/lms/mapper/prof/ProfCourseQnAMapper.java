package com.example.lms.mapper.prof;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.CourseQuestionAnswerDTO;
import com.example.lms.dto.CourseQuestionDTO;

@Mapper
public interface ProfCourseQnAMapper {
	
	// 메뉴
	List<Map<String, Object>> selectCourseQnASummary(int professorUserNo);

	// 문의 목록 조회
	List<Map<String, Object>> selectQuestionList(int courseNo, int startRow, int rowPerPage);
	
	// 목록 페이징
	int selectQuestionCount(int courseNo);
	
	// 상세보기
	Map<String, Object> selectQuestionDetail(int courseQuestionNo);

	// 답변 목록 조회
	List<Map<String, Object>> selectAnswerList(int courseQuestionNo);
	
	// 답변 등록
	void insertAnswer(@Param("courseQuestionNo") int courseQuestionNo,
			          @Param("professorUserNo") int professorUserNo,
			          @Param("answerContent") String answerContent);

	// 답변 수정
	void updateAnswer(@Param("answerNo") int answerNo,
	            		@Param("answerContent") String answerContent);

	// 답변 삭제
	void deleteAnswer(int answerNo);

	// 답변이 있는지 확인 -> 없으면 미답변
	int answerCount(int courseQuestionNo);
	
	// 답변 등록 -> 상태 1
	void updateQuestionStatusAnswered(int courseQuestionNo);
	// 답변 삭제 -> 상태 0
	void updateQuestionStatusReopen(int courseQuestionNo);
}
