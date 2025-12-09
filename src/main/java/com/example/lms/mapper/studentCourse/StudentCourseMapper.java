package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.GradeSummaryDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentAttendanceDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentTimetableDTO;

@Mapper
public interface StudentCourseMapper {
	StudentCourseDetailDTO selectStudentCourseHeaderInfo(
	        @Param("courseNo") int courseNo,
	        @Param("studentUserNo") int studentUserNo);

    // 공지
    List<StudentCourseNoticeDTO> selectStudentCourseNoticeList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    int selectStudentCourseNoticeTotal(int courseNo);

    StudentCourseNoticeDTO selectStudentCourseNoticeDetail(int courseNoticeNo);

    int updateStudentCourseNoticeViewCount(int courseNoticeNo);


    // 강의 기본 정보
    StudentCourseHomeDTO selectCourseBasicInfo(int courseNo);

    // 최근 공지
    List<StudentCourseNoticeDTO> selectRecentNotices(int courseNo);

    // 과제 요약 1개
    StudentAssignmentListDTO selectAssignmentSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 출석 요약
    AttendanceSummaryDTO selectAttendanceSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

	List<StudentAttendanceDTO> selectAttendanceDetailList(
	        @Param("courseNo") int courseNo,
	        @Param("studentUserNo") int studentUserNo);
	
    // 성적 요약
    GradeSummaryDTO selectGradeSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 최근 질문
    List<StudentQuestionDTO> selectRecentQuestions(int courseNo);


    // 내 수강과목 목록
    List<StudentCourseDTO> selectMyCourseList(int studentUserNo);

    // 강의 상세
    StudentCourseDetailDTO selectStudentCourseDetail(int courseNo);

    // 시간표
    List<StudentTimetableDTO> selectStudentTimetable(int studentUserNo);


    // 수강신청 필터 목록
    List<StudentCourseDTO> selectCourseListForStudentFiltered(
            @Param("studentUserNo") int studentUserNo,
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    int countCourseListFiltered(
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode);

    // 학과 리스트
    List<DeptDTO> selectDeptList();

    // 학생 과제 목록
    List<StudentAssignmentListDTO> selectAssignmentList(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);
    
}