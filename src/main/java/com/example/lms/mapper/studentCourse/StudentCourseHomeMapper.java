package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.AttendanceSummaryDTO;

@Mapper
public interface StudentCourseHomeMapper {

    StudentCourseHomeDTO selectCourseHome(int courseNo, int studentUserNo);

    List<StudentCourseNoticeDTO> selectRecentNotices(int courseNo);

    List<StudentAssignmentListDTO> selectRecentAssignment(int courseNo, int studentUserNo);

    AttendanceSummaryDTO selectAttendanceSummary(int courseNo, int studentUserNo);

    StudentGradeDTO selectStudentGradeSummary(int courseNo, int studentUserNo);

    List<StudentQuestionDTO> selectRecentQuestions(int courseNo);
}
