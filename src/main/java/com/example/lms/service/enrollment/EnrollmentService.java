package com.example.lms.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.dto.EnrollmentListDTO;
import com.example.lms.mapper.enrollment.EnrollmentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;

    // 수강 신청 처리
    public String addEnrollment(EnrollmentDTO dto) {

        // 중복 신청 체크
        if (enrollmentMapper.countEnrollment(dto.getStudentUserNo(), dto.getCourseNo()) > 0) {
            return "이미 신청한 강의입니다.";
        }

        // 시간표 겹침 체크
        if (enrollmentMapper.countTimeOverlap(dto.getStudentUserNo(), dto.getCourseNo()) > 0) {
            return "시간이 겹치는 강의입니다.";
        }

        // 신규 신청
        enrollmentMapper.insertEnrollment(dto);
        return "수강신청이 완료되었습니다.";
    }

    // 신청 내역 조회
    public List<EnrollmentListDTO> getEnrollmentList(int studentUserNo, int startRow, int rowPerPage) {
        return enrollmentMapper.selectEnrollmentListPaged(studentUserNo, startRow, rowPerPage);
    }

    // 신청 내역 총 개수
    public int getEnrollmentTotalCount(int studentUserNo) {
        return enrollmentMapper.countEnrollmentList(studentUserNo);
    }

    // 수강 취소
    public String cancelEnrollment(int studentUserNo, int enrollmentNo) {
        int result = enrollmentMapper.cancelEnrollment(studentUserNo, enrollmentNo);
        return (result == 1) ? "수강취소가 완료되었습니다." : "취소할 수 없습니다.";
    }
}
