package com.example.lms.mapper.studentCourse;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.CourseTimeDTO;

@Mapper
public interface StudentCourseDetailMapper {
    StudentCourseDetailDTO selectCourseDetail(int courseNo);
    List<CourseTimeDTO> selectCourseTimeList(int courseNo);
    int selectCurrentEnrollmentCount(int courseNo);
}
