package com.example.lms.controller.studentCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseQuestionDetailDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseInfoService;
import com.example.lms.service.studentCourse.StudentCourseQuestionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseQuestionController {

    private final StudentCourseQuestionService service;
    private final StudentCourseInfoService infoService;

    // 문의 목록
    @GetMapping("/studentCourseQuestionList")
    public String courseQuestionList(
            @RequestParam int courseNo,
            @RequestParam(defaultValue = "1") int currentPage,
            Model model,
            HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 통일된 헤더
        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);
        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);

        // 페이징
        int rowPerPage = 10;
        int totalCount = service.getTotalQuestionCount(courseNo);
        int lastPage = Math.max((int) Math.ceil((double) totalCount / rowPerPage), 1);

        currentPage = Math.max(1, Math.min(currentPage, lastPage));

        List<StudentQuestionDTO> list =
                service.getPagedQuestionList(courseNo, loginUser, currentPage, rowPerPage);

        int blockSize = 5;
        int blockStart = ((currentPage - 1) / blockSize) * blockSize + 1;
        int blockEnd = Math.min(blockStart + blockSize - 1, lastPage);

        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = blockStart; i <= blockEnd; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", i);
            map.put("current", i == currentPage);
            pageList.add(map);
        }

        model.addAttribute("questionList", list);
        model.addAttribute("pageList", pageList);
        model.addAttribute("hasPrev", blockStart > 1);
        model.addAttribute("hasNext", blockEnd < lastPage);
        model.addAttribute("prevPage", blockStart - 1);
        model.addAttribute("nextPage", blockEnd + 1);
        System.out.println("=== DEBUG:list 타입 ===");
        for (Object o : list) {
            System.out.println(o.getClass().getName());
        }

        return "studentCourse/studentCourseQuestionList";
    }

    // 문의 상세
    @GetMapping("/studentCourseQuestionDetail")
    public String detail(
            @RequestParam int courseQuestionNo,
            HttpSession session,
            Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        StudentCourseQuestionDetailDTO dto =
                service.getQuestionDetail(courseQuestionNo, loginUser);

        System.out.println("=== DEBUG: 상세 DTO 값 확인 ===");
        System.out.println("privatePost = " + dto.getPrivatePost());
        System.out.println("privatePostFlag = " + dto.isPrivatePostFlag());
        System.out.println("canView = " + dto.isCanView());
        System.out.println("answered = " + dto.isAnswered());
        System.out.println("title = " + dto.getCourseQuestionTitle());
        StudentCourseQuestionDetailDTO question =
                service.getQuestionDetail(courseQuestionNo, loginUser);

        if (!question.isCanView()) {
            return "redirect:/studentCourseQuestionList?courseNo=" + question.getCourseNo();
        }

        int courseNo = question.getCourseNo();

        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);
        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);
        
        model.addAttribute("question", question);

        model.addAttribute("isOwner",
                loginUser.getUserNo() == question.getWriterUserNo());

        return "studentCourse/studentCourseQuestionDetail";
    }


    // 문의 작성 폼
    @GetMapping("/studentCourseQuestionWriteForm")
    public String writeForm(
            @RequestParam int courseNo,
            Model model,
            HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);

        return "studentCourse/studentCourseQuestionWriteForm";
    }

    // 문의 작성 처리
    @PostMapping("/studentCourseQuestionWrite")
    public String write(CourseQuestionDTO dto, HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setWriterUserNo(loginUser.getUserNo());
        service.insertQuestion(dto);

        return "redirect:/studentCourseQuestionList?courseNo=" + dto.getCourseNo();
    }

    // 문의 수정 폼
    @GetMapping("/studentCourseQuestionEditForm")
    public String editForm(
            @RequestParam int courseQuestionNo,
            HttpSession session,
            Model model) {
        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        StudentCourseQuestionDetailDTO q = service.getQuestionDetail(courseQuestionNo, loginUser);
        int courseNo = q.getCourseNo();
        System.out.println("=== DEBUG: Controller 전달된 수정값 ===");
        System.out.println("dto.privatePost = " + q.getPrivatePost());
        System.out.println("dto.courseQuestionTitle = " + q.getCourseQuestionTitle());
        System.out.println("dto.courseQuestionContent = " + q.getCourseQuestionContent());
        StudentCourseDetailDTO courseHeader = infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);
        model.addAttribute("question", q);

        return "studentCourse/studentCourseQuestionEditForm";
    }

    // 문의 수정 처리
    @PostMapping("/studentCourseQuestionEdit")
    public String edit(CourseQuestionDTO dto, HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        boolean ok = service.updateQuestion(dto, loginUser);
        if (!ok) return "redirect:/accessDenied";

        return "redirect:/studentCourseQuestionDetail?courseQuestionNo=" + dto.getCourseQuestionNo();
    }

    // 문의 삭제
    @GetMapping("/studentCourseQuestionDelete")
    public String delete(
            @RequestParam int courseQuestionNo,
            HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int courseNo = service.getCourseNoByQuestion(courseQuestionNo);

        boolean ok = service.deleteQuestion(courseQuestionNo, loginUser);
        if (!ok) return "redirect:/accessDenied";

        return "redirect:/studentCourseQuestionList?courseNo=" + courseNo;
    }
}
