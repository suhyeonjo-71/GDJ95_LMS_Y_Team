package com.example.lms.controller.studentCourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseDetailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseDetailController {

    private final StudentCourseDetailService studentCourseDetailService;

    // --------------------------------------------------
    // 학생 수강신청 - 강의 상세 조회
    // --------------------------------------------------
    @GetMapping("/studentCourseDetail")
    public String courseDetail(
            @RequestParam("courseNo") int courseNo,
            HttpSession session,
            Model model) {

        // 로그인 사용자 정보
        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 1) 강의 상세 정보 조회
        StudentCourseDetailDTO detail = studentCourseDetailService.getCourseDetail(courseNo);

        // 2) 강의 정보 전달
        model.addAttribute("course", detail);

        // 3) 강의 시간 리스트 전달 (DTO 내부 List)
        model.addAttribute("timeList", detail.getCourseTimeList());

        // HEADER 표시용
        model.addAttribute("pageTitle", "강의 상세");
        model.addAttribute("pageDescription", "해당 강의의 상세 정보와 학습 내용을 한눈에 확인할 수 있습니다.");
        model.addAttribute("loginUserName", loginUser.getUserName());

        // 메인 네비에서 '수강신청' 활성화 (파란색)
        model.addAttribute("nav_enrollment", true);

        return "studentCourse/studentCourseDetail";
    }
}
