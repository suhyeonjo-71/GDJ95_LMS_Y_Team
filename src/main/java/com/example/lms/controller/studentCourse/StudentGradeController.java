package com.example.lms.controller.studentCourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseService;
import com.example.lms.service.studentCourse.StudentGradeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentGradeService service;
    private final StudentCourseService studentCourseService;

    // 학생 성적 페이지
    @GetMapping("/student/grade")
    public String studentGrade(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        int studentUserNo = user.getUserNo();

        // ★ 강의 Header/SubNav 정보
        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_grade", true);

        // ★ 학생 성적 조회
        StudentGradeDTO grade =
                service.getStudentGrade(courseNo, studentUserNo);

        model.addAttribute("grade", grade);

        return "studentCourse/studentGrade";
    }
}
