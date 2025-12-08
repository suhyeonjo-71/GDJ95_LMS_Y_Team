package com.example.lms.controller.studentCourse;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseHomeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseHomeController {

    private final StudentCourseHomeService service;
    
    // -------------------------------------------
    // 강의 홈 (Dashboard)
    // -------------------------------------------
    @GetMapping("/studentCourseHome")
    public String studentCourseHome(
    		@RequestParam("courseNo") int courseNo,
            HttpSession session,
            Model model) {
    	
    	// 로그인 사용자 정보
        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        
        String userRole = loginUser.getUserAuth();   // STUDENT 또는 PROFESSOR

        int studentUserNo = loginUser.getUserNo();

        // <!-- 강의 기본 정보 -->
        StudentCourseHomeDTO courseInfo = service.getStudentCourseHome(courseNo, studentUserNo);
        model.addAttribute("course", courseInfo);
        model.addAttribute("courseNo", courseNo);

        // <!-- 네비게이션 활성화 -->
        model.addAttribute("nav_home", "border-blue-600 text-blue-600");

        // <!-- 공지 요약 -->
        model.addAttribute("noticeList", service.getRecentNotices(courseNo));

        // <!-- 과제 요약 -->
        model.addAttribute("assignment", service.getRecentAssignment(courseNo, studentUserNo));

        // <!-- 출석 요약 -->
        model.addAttribute("attendance", service.getAttendanceSummary(courseNo, studentUserNo));

        // <!-- 성적 요약 -->
        model.addAttribute("grade", service.getStudentGradeSummary(courseNo, studentUserNo));

        // <!-- 최근 Q&A -->
     // <!-- 최근 Q&A -->
        List<StudentQuestionDTO> recentQuestions =
                service.getRecentQuestionList(courseNo, studentUserNo, userRole);
        model.addAttribute("questionList", recentQuestions);

        
        // HEADER 표시용
        model.addAttribute("loginUserName", loginUser.getUserName());
        model.addAttribute("nav_enrollment", "text-blue-600 border-blue-600");
        
        return "studentCourse/studentCourseHome";
    }
}
