package com.example.lms.mapper.prof;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.CourseNoticeDTO;

@Mapper
public interface ProfCourseNoticeMapper {
	
	// 메뉴
	List<CourseNoticeDTO> selectCourseNoticeSummary(int professorUserNo);
	
	// 공지사항 리스트
	List<CourseNoticeDTO> selectCourseNoticeList(int courseNo, int startRow, int rowPerPage);
	int selectCourseNoticeCount(int courseNo); // 페이징
	int updateCourseNoticeViewCount(int courseNoticeNo); // 조회수
	
	// 상세보기
	CourseNoticeDTO selectCourseNoticeDetail(int courseNoticeNo);
	
	// 등록
	int insertCourseNotice(CourseNoticeDTO cn);
	
	// 수정
	int updateCourseNotice(CourseNoticeDTO cn);
	
	// 삭제
	int deleteCourseNotice(int courseNoticeNo);
}
