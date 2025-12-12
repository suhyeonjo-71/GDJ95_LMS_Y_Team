package com.example.lms.mapper.enrollment;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.dto.EnrollmentListDTO;

@Mapper
public interface EnrollmentMapper {

    // 현재 신청 상태 확인 (0=신청중, 1=취소, null=기록 없음)
    Integer selectEnrollmentStatus(
            @Param("studentUserNo") int studentUserNo,
            @Param("courseNo") int courseNo
    );

    // 이미 취소된 기록을 다시 활성화
    int updateEnrollmentStatus(
            @Param("studentUserNo") int studentUserNo,
            @Param("courseNo") int courseNo
    );

    // 신규 신청
    int insertEnrollment(EnrollmentDTO dto);

    // 수강 취소
    int cancelEnrollment(
            @Param("studentUserNo") int studentUserNo,
            @Param("enrollmentNo") int enrollmentNo
    );

    // 신청 내역 조회 (페이징)
    List<EnrollmentListDTO> selectEnrollmentList(
            @Param("studentUserNo") int studentUserNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage
    );

    // 전체 개수 조회
    int selectEnrollmentTotalCount(int studentUserNo);
    
    // 시간표 중복
    int countTimeOverlap(
            @Param("studentUserNo") int studentUserNo,
            @Param("courseNo") int courseNo
    );
}
