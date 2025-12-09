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

    // 학생 과제 목록
    public List<StudentAssignmentListDTO> getAssignmentList(int courseNo, int studentUserNo) {

        System.out.println("DEBUG >>> getAssignmentList() 호출됨");
        System.out.println("DEBUG >>> courseNo = " + courseNo);
        System.out.println("DEBUG >>> studentUserNo = " + studentUserNo);

        List<StudentAssignmentListDTO> list =
                mapper.selectAssignmentList(courseNo, studentUserNo);

        System.out.println("DEBUG >>> mapper.selectAssignmentList 결과 = " + list);
        if (list != null) {
            System.out.println("DEBUG >>> 결과 개수 = " + list.size());
        }

        return list;
    }

    // ---------------------------------------------------------
    // 📌 2. 학생 과제 상세 + 제출 정보
    // ---------------------------------------------------------
    public StudentAssignmentDetailDTO getAssignmentDetail(int assignmentNo, int studentUserNo) {
        return mapper.selectAssignmentDetail(assignmentNo, studentUserNo);
    }


    // ---------------------------------------------------------
    // 📌 3. 제출 조회
    // ---------------------------------------------------------
    public AssignmentSubmissionDTO getSubmission(int assignmentNo, int userNo) {
        return mapper.selectMySubmission(assignmentNo, userNo);
    }


    // ---------------------------------------------------------
    // 📌 4. 제출 / 수정
    // ---------------------------------------------------------
    public void submitAssignment(AssignmentSubmissionDTO dto) {

        AssignmentSubmissionDTO existing =
                mapper.selectMySubmission(dto.getAssignmentNo(), dto.getWriterUserNo());

        if (existing == null) {
            mapper.insertSubmission(dto);

        } else {
            dto.setAssignmentSubmissionNo(existing.getAssignmentSubmissionNo());

            if (dto.getAssignmentSubmissionFileUrl() == null ||
                dto.getAssignmentSubmissionFileUrl().isBlank()) {
                dto.setAssignmentSubmissionFileUrl(
                        existing.getAssignmentSubmissionFileUrl()
                );
            }

            mapper.updateSubmission(dto);
        }
    }


    // ---------------------------------------------------------
    // ❗📌 5. 제출 취소 (파일삭제 + 내용삭제 + submitted false)
    // ---------------------------------------------------------
    public void cancelSubmission(int assignmentNo, int userNo) {
        mapper.disableSubmission(assignmentNo, userNo);
    }
}
