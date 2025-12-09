package com.example.lms.controller.studentCourse;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.StudentAttendanceDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseAttendanceService;
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseAttendanceController {

    private final StudentCourseAttendanceService service;
    private final StudentCourseService studentCourseService;

    // ---------------------------------------------------------
    // 학생 출석 페이지
    // ---------------------------------------------------------
    @GetMapping("/student/attendance")
    public String attendancePage(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        int studentUserNo = user.getUserNo();

        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_attendance", true);

        // 회차별 출석 상세
        List<StudentAttendanceDTO> detailList =
                service.getAttendanceDetailList(courseNo, studentUserNo);

        // 출석/지각/결석 요약
        AttendanceSummaryDTO summary =
                service.getAttendanceSummary(courseNo, studentUserNo);

        model.addAttribute("detailList", detailList);
        model.addAttribute("summary", summary);

        return "studentCourse/studentAttendance";
    }
}
