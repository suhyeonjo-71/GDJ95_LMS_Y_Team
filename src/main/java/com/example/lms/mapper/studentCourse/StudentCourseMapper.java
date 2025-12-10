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

// 학생 관련 통합 Mapper
@Mapper
public interface StudentCourseMapper {

    // 강의 헤더 정보 조회
    StudentCourseDetailDTO selectStudentCourseHeaderInfo(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 공지 목록 조회
    List<StudentCourseNoticeDTO> selectStudentCourseNoticeList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 공지 총 개수
    int selectStudentCourseNoticeTotal(int courseNo);

    // 공지 상세 조회
    StudentCourseNoticeDTO selectStudentCourseNoticeDetail(int courseNoticeNo);

    // 공지 조회수 증가
    int updateStudentCourseNoticeViewCount(int courseNoticeNo);

    // 강의 기본 정보
    StudentCourseHomeDTO selectCourseBasicInfo(int courseNo);

    // 최근 공지 조회
    List<StudentCourseNoticeDTO> selectRecentNotices(int courseNo);

    // 최근 과제 1개
    StudentAssignmentListDTO selectAssignmentSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 과제 목록 조회
    List<StudentAssignmentListDTO> selectAssignmentList(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 출석 요약 조회
    AttendanceSummaryDTO selectAttendanceSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 출석 상세 리스트
    List<StudentAttendanceDTO> selectAttendanceDetailList(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 성적 요약
    GradeSummaryDTO selectGradeSummary(
            @Param("courseNo") int courseNo,
            @Param("studentUserNo") int studentUserNo);

    // 최근 질문 목록
    List<StudentQuestionDTO> selectRecentQuestions(int courseNo);

    // 내 수강과목 목록
    List<StudentCourseDTO> selectMyCourseList(int studentUserNo);

    // 강의 상세 정보
    StudentCourseDetailDTO selectStudentCourseDetail(int courseNo);

    // 학생 시간표
    List<StudentTimetableDTO> selectStudentTimetable(int studentUserNo);

    // 수강신청 필터 목록 조회
    List<StudentCourseDTO> selectCourseListForStudentFiltered(
            @Param("studentUserNo") int studentUserNo,
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 수강신청 필터 목록 총 개수
    int countCourseListFiltered(
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode);

    // 학과 리스트
    List<DeptDTO> selectDeptList();
}
