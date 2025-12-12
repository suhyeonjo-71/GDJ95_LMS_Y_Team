package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.AssignmentSubmissionDTO;
import com.example.lms.dto.StudentAssignmentDetailDTO;
import com.example.lms.dto.StudentAssignmentListDTO;

@Mapper
public interface StudentAssignmentMapper {

    // 과제 목록
    List<StudentAssignmentListDTO> selectAssignmentList(
            @Param("courseNo") int courseNo,
            @Param("writerUserNo") int writerUserNo);

    // 과제 상세
    StudentAssignmentDetailDTO selectAssignmentDetail(
            @Param("assignmentNo") int assignmentNo,
            @Param("writerUserNo") int writerUserNo);

    // 기존 제출 조회
    AssignmentSubmissionDTO selectMySubmission(
            @Param("assignmentNo") int assignmentNo,
            @Param("writerUserNo") int writerUserNo);

    // 제출 INSERT
    void insertSubmission(AssignmentSubmissionDTO dto);

    // 제출 UPDATE
    void updateSubmission(AssignmentSubmissionDTO dto);

    // 제출 무효화 (삭제 대신 상태 변경)
    void disableSubmission(
            @Param("assignmentNo") int assignmentNo,
            @Param("userNo") int userNo);

    // 홈 화면 요약: 최근 마감 과제 1개
    List<StudentAssignmentListDTO> selectRecentAssignment(
            @Param("courseNo") int courseNo,
            @Param("writerUserNo") int writerUserNo);
}
