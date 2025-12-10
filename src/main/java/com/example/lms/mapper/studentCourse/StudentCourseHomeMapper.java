package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.CourseQuestionDTO;

// 학생 강의 홈 화면 조회 Mapper
@Mapper
public interface StudentCourseHomeMapper {

    // 강의 홈 기본 정보 조회
    StudentCourseHomeDTO selectCourseHome(int courseNo, int studentUserNo);

    // 최근 공지사항 조회
    List<StudentCourseNoticeDTO> selectRecentNotices(int courseNo);

    // 최근 과제 조회
    List<StudentAssignmentListDTO> selectRecentAssignment(int courseNo, int studentUserNo);

    // 출석 요약 정보 조회
    AttendanceSummaryDTO selectAttendanceSummary(int courseNo, int studentUserNo);

    // 학생 성적 요약 조회
    StudentGradeDTO selectStudentGradeSummary(int courseNo, int studentUserNo);

    // 최근 문의사항 조회
    List<CourseQuestionDTO> selectRecentQuestionsForStudent(int courseNo);
}
