package com.example.lms.mapper.enrollment;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentTimetableDTO;

@Mapper
public interface EnrollmentCourseQueryMapper {

    List<StudentCourseDTO> selectCourseListForStudentFiltered(
            @Param("studentUserNo") int studentUserNo,
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode,
            @Param("startRow") int startRow,
            @Param("rowPerPage") int rowPerPage);

    int countCourseListFiltered(
            @Param("yoil") Integer yoil,
            @Param("professor") String professor,
            @Param("deptCode") String deptCode);

    List<DeptDTO> selectDeptList();

    StudentCourseDetailDTO selectStudentCourseDetail(int courseNo);

    List<StudentTimetableDTO> selectStudentTimetable(int studentUserNo);
}