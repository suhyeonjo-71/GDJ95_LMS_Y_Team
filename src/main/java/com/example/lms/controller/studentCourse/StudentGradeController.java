package com.example.lms.controller.studentCourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentGradeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseInfoService;
import com.example.lms.service.studentCourse.StudentGradeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentGradeService gradeService;
    private final StudentCourseInfoService infoService;

    // 학생 성적 페이지
    @GetMapping("/student/grade")
    public String studentGrade(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        int studentUserNo = user.getUserNo();

        // 강의 Header/SubNav 정보
        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_grade", true);

        // 학생 성적 요약 조회 (옵션 B 적용)
        StudentGradeDTO grade =
                gradeService.getStudentGradeSummary(courseNo, studentUserNo);

        model.addAttribute("grade", grade);

        return "studentCourse/studentGrade";
    }
}
