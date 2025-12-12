package com.example.lms.service.studentCourse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lms.dto.AssignmentSubmissionDTO;
import com.example.lms.dto.StudentAssignmentDetailDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.mapper.studentCourse.StudentAssignmentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentAssignmentService {

    private final StudentAssignmentMapper mapper;

    // 홈 화면 요약: 최근 과제 1개 조회
    public List<StudentAssignmentListDTO> getRecentAssignments(int courseNo, int studentUserNo) {
        return mapper.selectRecentAssignment(courseNo, studentUserNo);
    }

    // 전체 과제 목록
    public List<StudentAssignmentListDTO> getAssignmentList(int courseNo, int studentUserNo) {
        List<StudentAssignmentListDTO> list =
                mapper.selectAssignmentList(courseNo, studentUserNo);

        if (list != null) {
            System.out.println("DEBUG >>> 결과 개수 = " + list.size());
        }

        return list;
    }

    // 과제 상세 + 제출 정보
    public StudentAssignmentDetailDTO getAssignmentDetail(int assignmentNo, int studentUserNo) {
        return mapper.selectAssignmentDetail(assignmentNo, studentUserNo);
    }

    // 제출 정보 조회
    public AssignmentSubmissionDTO getSubmission(int assignmentNo, int userNo) {
        return mapper.selectMySubmission(assignmentNo, userNo);
    }

    // 과제 제출 또는 수정
    public void submitAssignment(AssignmentSubmissionDTO dto) {

        AssignmentSubmissionDTO existing =
                mapper.selectMySubmission(dto.getAssignmentNo(), dto.getWriterUserNo());

        // 최초 제출
        if (existing == null) {
            mapper.insertSubmission(dto);
            return;
        }

        // 수정 제출
        dto.setAssignmentSubmissionNo(existing.getAssignmentSubmissionNo());

        // 파일 미첨부 시 기존 파일 유지
        if (dto.getAssignmentSubmissionFileUrl() == null ||
            dto.getAssignmentSubmissionFileUrl().isBlank()) {
            dto.setAssignmentSubmissionFileUrl(existing.getAssignmentSubmissionFileUrl());
        }

        mapper.updateSubmission(dto);
    }

    // 제출 취소
    public void cancelSubmission(int assignmentNo, int userNo) {
        mapper.disableSubmission(assignmentNo, userNo);
    }
}
