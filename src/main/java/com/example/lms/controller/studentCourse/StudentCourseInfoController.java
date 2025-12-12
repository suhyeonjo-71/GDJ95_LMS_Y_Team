package com.example.lms.controller.studentCourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseInfoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseInfoController {

    private final StudentCourseInfoService infoService;

    // 강의 상세
    @GetMapping("/course/info")
    public String courseInfo(
            @RequestParam int courseNo,
            @RequestParam(defaultValue = "student") String mode,
            HttpSession session,
            Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        StudentCourseDetailDTO detail = infoService.getCourseDetail(courseNo);
        model.addAttribute("course", detail);
        model.addAttribute("mode", mode);
        model.addAttribute("loginUserName", loginUser.getUserName());

        // Header 세팅
        switch (mode) {
            case "enroll":
                model.addAttribute("pageTitle", "수강신청 강의 상세");
                model.addAttribute("pageDescription", "신청 전 강의 정보를 확인하세요.");
                break;
            case "enrollList":
                model.addAttribute("pageTitle", "수강신청 내역 상세");
                model.addAttribute("pageDescription", "이미 신청한 강의 상세 정보입니다.");
                break;
            default:
                model.addAttribute("pageTitle", "강의 상세 정보");
                model.addAttribute("pageDescription", "강의 정보를 확인하세요.");
        }

        return "studentCourse/studentCourseInfo";
    }
}
