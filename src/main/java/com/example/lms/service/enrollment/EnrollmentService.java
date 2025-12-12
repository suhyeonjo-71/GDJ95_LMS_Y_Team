package com.example.lms.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.dto.EnrollmentListDTO;
import com.example.lms.mapper.enrollment.EnrollmentMapper;

import lombok.RequiredArgsConstructor;

/**
 * 수강신청 핵심 로직(Service Layer)
 * - 신청 여부 체크
 * - 신규 신청 / 재신청 / 취소 처리
 * - 신청 내역 조회
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentMapper mapper;

    // 수강 신청 (신규 / 재신청 / 중복 방지)
    public String addEnrollment(EnrollmentDTO dto) {

        // 시간표 중복 검사
        int overlap = mapper.countTimeOverlap(dto.getStudentUserNo(), dto.getCourseNo());
        if (overlap > 0) {
            return "이미 같은 시간대에 다른 강의를 수강 중입니다.";
        }

        // 기존 신청 상태 확인
        Integer status = mapper.selectEnrollmentStatus(dto.getStudentUserNo(), dto.getCourseNo());

        // 이미 신청 중
        if (status != null && status == 0) {
            return "이미 수강 중인 강의입니다.";
        }

        // 과거 취소 기록 → 재활성화
        if (status != null && status == 1) {
            mapper.updateEnrollmentStatus(dto.getStudentUserNo(), dto.getCourseNo());
            return "수강신청이 다시 활성화되었습니다.";
        }

        // 신규 신청
        mapper.insertEnrollment(dto);
        return "수강신청이 완료되었습니다.";
    }


    // 수강 취소
    public String cancelEnrollment(int studentUserNo, int enrollmentNo) {
        int result = mapper.cancelEnrollment(studentUserNo, enrollmentNo);
        return (result == 1) ? "수강 취소되었습니다." : "취소에 실패했습니다.";
    }

    // 수강신청 내역 목록
    public List<EnrollmentListDTO> getEnrollmentList(int studentUserNo, int startRow, int rowPerPage) {

        System.out.println("=== DEBUG:getEnrollmentList() 호출 ===");
        System.out.println("studentUserNo = " + studentUserNo);
        System.out.println("startRow = " + startRow + ", rowPerPage = " + rowPerPage);

        List<EnrollmentListDTO> list =
                mapper.selectEnrollmentList(studentUserNo, startRow, rowPerPage);

        System.out.println("=== DEBUG: 조회된 신청 내역 " + list.size() + "건 ===");

        for (EnrollmentListDTO dto : list) {
            System.out.println("DEBUG >> enrollmentNo=" + dto.getEnrollmentNo()
                    + ", courseNo=" + dto.getCourseNo()
                    + ", courseName=" + dto.getCourseName()
                    + ", status=" + dto.getEnrollmentStatus()
                    + ", yoil=" + dto.getYoilName());
        }

        System.out.println("=== DEBUG:end ===");

        return list;
    }


    // 전체 수강신청 기록 수
    public int getEnrollmentTotalCount(int studentUserNo) {
        return mapper.selectEnrollmentTotalCount(studentUserNo);
    }
}
