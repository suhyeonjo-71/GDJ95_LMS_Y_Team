package com.example.lms.mapper.prof;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.AssignmentDTO;
import com.example.lms.dto.ProfCourseAssignmentDTO;

@Mapper
public interface ProfAssignmentMapper {
	
	//메뉴
	List<ProfCourseAssignmentDTO> selectCourseAssignmentSummary(int professorUserNo);
	
	// 강의별 과제 리스트
	List<AssignmentDTO> selectCourseAssignmentList(int courseNo, int startRow, int rowPerPage);
	int selectCourseAssignmentCount(int courseNo); //페이징
	
	// 상세보기
	AssignmentDTO selectCourseAssignmentDetail(int assignmentNo);
	
	// 학생 과제 제출 리스트
	List<ProfCourseAssignmentDTO> selectCourseSubmissionList(@Param("assignmentNo") int assignmentNo,
            											@Param("courseNo") int courseNo);
	
	// 점수 저장
	void updateSubmissionScore(@Param("submissionNo") int submissionNo, @Param("score") Integer score);
	
	// 과제 제출 번호 : 사용자번호
	int selectWriterUserNoBySubmissionNo(int submissionNo);
	
	// 등록
	int insertAssignment(AssignmentDTO a);
	
	// 수정
	int updateAssignment(AssignmentDTO a);
	
	// 삭제
	int deleteAssignment(int assignmentNo);
}