package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.StudentCourseNoticeDTO;

// 학생 공지사항 조회 Mapper
@Mapper
public interface StudentCourseNoticeMapper {

    // 공지사항 목록 조회 (페이징)
    List<StudentCourseNoticeDTO> selectNoticeList(
            @Param("courseNo") int courseNo,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    // 공지사항 상세 조회
    StudentCourseNoticeDTO selectNoticeDetail(int courseNoticeNo);

    // 공지사항 총 개수 조회
    int selectNoticeTotal(int courseNo);
}
