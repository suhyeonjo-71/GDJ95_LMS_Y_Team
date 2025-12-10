package com.example.lms.service.prof;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.AssignmentDTO;
import com.example.lms.dto.AssignmentSubmissionDTO;
import com.example.lms.dto.ProfCourseAssignmentDTO;
import com.example.lms.mapper.prof.ProfAssignmentMapper;

@Service
@Transactional
public class ProfAssignmentService {

	@Autowired
	ProfAssignmentMapper assignmentMapper;
	
	// 메뉴
	public List<ProfCourseAssignmentDTO> getCourseAssignmentSummary(int professorUserNo) {
        return assignmentMapper.selectCourseAssignmentSummary(professorUserNo);
    }
	
	// 강의별 과제 리스트
	public List<AssignmentDTO> getCourseAssignmnetList(int courseNo, int startRow, int rowPerPage) {
		
		return assignmentMapper.selectCourseAssignmentList(courseNo, startRow, rowPerPage);
	}
	// 리스트 페이징
	public int getCourseAssignmentCount(int courseNo) {
		
		return assignmentMapper.selectCourseAssignmentCount(courseNo);
	}
	
	// 상세보기
	public AssignmentDTO getCourseAssignmentDetail(int assignmentNo) {
		
		return assignmentMapper.selectCourseAssignmentDetail(assignmentNo);
	}
	
	// 학생 과제 제출 리스트
	public List<ProfCourseAssignmentDTO> getCourseSubmissionList(int assignmentNo, int courseNo) {
		return assignmentMapper.selectCourseSubmissionList(assignmentNo, courseNo);
	}
	
	// 등록
	public int addAssignment(AssignmentDTO a) {
		
		return assignmentMapper.insertAssignment(a);
	}
	
	// 수정
	public int modifyAssignment(AssignmentDTO a) {
		
		return assignmentMapper.updateAssignment(a);
	}
	
	// 삭제
	public int removeAssignment(int assignmentNo) {
		
		return assignmentMapper.deleteAssignment(assignmentNo); 
	}
}
