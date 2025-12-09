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

    // 제출 여부 확인
    Integer selectSubmissionExists(
            @Param("assignmentNo") int assignmentNo,
            @Param("writerUserNo") int writerUserNo);

    // 제출 INSERT
    void insertSubmission(AssignmentSubmissionDTO dto);

    // 제출 UPDATE
    void updateSubmission(AssignmentSubmissionDTO dto);

    // 기존 제출 조회
    AssignmentSubmissionDTO selectMySubmission(
            @Param("assignmentNo") int assignmentNo,
            @Param("writerUserNo") int writerUserNo);

    // 제출 전체 무효화 (파일 삭제 + 제출 취소)
    void disableSubmission(
            @Param("assignmentNo") int assignmentNo,
            @Param("userNo") int userNo);
}
