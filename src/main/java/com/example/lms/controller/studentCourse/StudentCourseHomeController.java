package com.example.lms.controller.studentCourse;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseHomeService;
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseHomeController {

    private final StudentCourseHomeService service;
    private final StudentCourseService studentCourseService;

    @GetMapping("/studentCourseHome")
    public String studentCourseHome(
            @RequestParam("courseNo") int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int studentUserNo = loginUser.getUserNo();

        // 공통 헤더/서브네비용 course 정보
        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_home", true);

        // 홈 화면 전용 요약 데이터
        StudentCourseHomeDTO courseInfo =
                service.getStudentCourseHome(courseNo, studentUserNo);
        model.addAttribute("courseInfo", courseInfo);

        model.addAttribute("noticeList", service.getRecentNotices(courseNo));
        model.addAttribute("assignment", service.getRecentAssignment(courseNo, studentUserNo));
        model.addAttribute("attendance", service.getAttendanceSummary(courseNo, studentUserNo));
        model.addAttribute("grade", service.getStudentGradeSummary(courseNo, studentUserNo));

        List<StudentQuestionDTO> recentQuestions =
                service.getRecentQuestionList(courseNo, studentUserNo, loginUser.getUserAuth());
        model.addAttribute("questionList", recentQuestions);
        
       
        model.addAttribute("loginUserName", loginUser.getUserName());

        return "studentCourse/studentCourseHome";
    }
}
