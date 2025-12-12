package com.example.lms.service.prof;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.ProfCourseStudDTO;
import com.example.lms.mapper.prof.ProfCourseStudMapper;

@Service
@Transactional
public class ProfCourseStudService {
	
	@Autowired
	ProfCourseStudMapper profCourseStudMapper;
	
	// 강의별 수강생 리스트
	public List<ProfCourseStudDTO> getCourseStudentList(int courseNo, int startRow, int rowPerPage) {
		return profCourseStudMapper.selectCourseStudentList(courseNo, startRow, rowPerPage);
	}
	
	public int getCourseStudentCount(int courseNo) {
		return profCourseStudMapper.selectCourseStudentCount(courseNo);
	}
}
