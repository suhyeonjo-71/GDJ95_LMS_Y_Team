package com.example.lms.controller.studentCourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseHomeService;
import com.example.lms.service.studentCourse.StudentCourseInfoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseHomeController {

    private final StudentCourseHomeService homeService;
    private final StudentCourseInfoService infoService;

    @GetMapping("/studentCourseHome")
    public String studentCourseHome(
            @RequestParam("courseNo") int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int studentUserNo = loginUser.getUserNo();
        String userAuth = loginUser.getUserAuth();

        // 1) 헤더 / 서브네비용 기본 강의 정보
        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_home", true);
        

        // 2) 홈 요약 데이터 전체
        StudentCourseHomeDTO courseInfo =
                homeService.getStudentCourseHome(courseNo, studentUserNo, userAuth);
        model.addAttribute("courseInfo", courseInfo);

        // 3) 로그인 사용자명
        model.addAttribute("loginUserName", loginUser.getUserName());
        
        // Mustache에서 요구하는 이름으로 각각 넣기
        model.addAttribute("course", courseInfo);                          // 강의 기본정보
        model.addAttribute("noticeList", courseInfo.getNoticeList());      // 공지 리스트
        model.addAttribute("assignment", courseInfo.getAssignmentList());      // 최근 과제 1건
        model.addAttribute("attendance", courseInfo.getAttendanceSummary());      // 출석 요약
        model.addAttribute("grade", courseInfo.getGradeSummary());                // 성적 요약
        model.addAttribute("questionList", courseInfo.getQuestionList());  // 최근 Q&A 리스트

        return "studentCourse/studentCourseHome";
    }
}
