package com.example.lms.mapper.prof;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.ProfCourseDTO;
import com.example.lms.dto.ProfCourseTimeDTO;

@Mapper
public interface ProfCourseMapper {
	
	// 교수별 강의 리스트
	List<ProfCourseDTO> selectFilteredCourseList(
    	    int professorUserNo, Integer year, String semester, String status, int startRow, int rowPerPage);

	// 리스트 페이징
    int selectFilteredCourseCount(
    	    int professorUserNo, Integer year, String semester, String status);

    // 필터 년도 저장
	List<Integer> selectDistinctYearsByProfessor(int professorUserNo);

	
	// 등록
	int insertCourse(ProfCourseDTO c);
	
	// 강의 시간 등록
	int insertCourseTime(ProfCourseTimeDTO pct);
	
	// 시간 중복 체크
	int checkDuplicateTime(
	        @Param("yoil") int yoil,
	        @Param("start") int start,
	        @Param("end") int end,
	        @Param("classroom") String classroom,
	        @Param("professorUserNo") int professorUserNo,
	        @Param("courseNo") int courseNo
	);
	
	// 학과 목록 조회
	List<DeptDTO> selectDeptList();
	
	// 대시보드 (강의 + 학과 + 강의시간)
	List<ProfCourseDTO> selectCourseDetail(int courseNo);
	
	// 수정
    int updateCourse(ProfCourseDTO c);

    // 삭제
    int deleteCourse(int courseNo);
    
    // 강의 시간 삭제
    int deleteCourseTime(int courseNo);
}

