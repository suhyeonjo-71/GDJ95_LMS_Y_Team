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
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseQuestionService;
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseQuestionController {

    private final StudentCourseQuestionService service;
    private final StudentCourseService studentCourseService;
    
    // 문의 목록
    @GetMapping("/studentCourseQuestionList")
    public String courseQuestionList(
            @RequestParam int courseNo,
            @RequestParam(defaultValue = "1") int currentPage,
            Model model, HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader =
        		studentCourseService.getStudentCourseHeaderInfo(courseNo, loginUser.getUserNo());

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);

        // 페이징 계산
        int rowPerPage = 10;
        int totalCount = service.getTotalQuestionCount(courseNo);
        int lastPage = (int) Math.ceil((double) totalCount / rowPerPage);
        if (lastPage == 0) lastPage = 1;

        if (currentPage < 1) currentPage = 1;
        if (currentPage > lastPage) currentPage = lastPage;

        List<CourseQuestionDTO> list =
                service.getPagedQuestionList(courseNo, loginUser, currentPage, rowPerPage);

        int pagePerBlock = 5;
        int blockStartPage = ((currentPage - 1) / pagePerBlock) * pagePerBlock + 1;
        int blockEndPage = Math.min(blockStartPage + pagePerBlock - 1, lastPage);

        boolean hasPrev = blockStartPage > 1;
        boolean hasNext = blockEndPage < lastPage;
        int prevPage = blockStartPage - 1;
        int nextPage = blockEndPage + 1;

        // 페이지 리스트 구성
        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = blockStartPage; i <= blockEndPage; i++) {
            Map<String, Object> page = new HashMap<>();
            page.put("page", i);
            page.put("current", (i == currentPage));
            pageList.add(page);
        }

        model.addAttribute("questionList", list);
        model.addAttribute("pageList", pageList);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);
        if (hasPrev) model.addAttribute("prevPage", prevPage);
        if (hasNext) model.addAttribute("nextPage", nextPage);

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

        CourseQuestionDTO question =
                service.getQuestionDetail(courseQuestionNo, loginUser);

        StudentCourseDetailDTO courseHeader =
        		 studentCourseService.getStudentCourseHeaderInfo(question.getCourseNo(), loginUser.getUserNo());

        model.addAttribute("course", courseHeader);
        model.addAttribute("nav_qa", true);
        model.addAttribute("courseNo", question.getCourseNo());

        boolean isOwner =
                loginUser.getUserNo() == question.getWriterUserNo();
        model.addAttribute("isOwner", isOwner);

        model.addAttribute("question", question);

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

        StudentCourseDetailDTO courseHeader =
        		 studentCourseService.getStudentCourseHeaderInfo(courseNo, loginUser.getUserNo());

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_qa", true);

        return "studentCourse/studentCourseQuestionWriteForm";
    }

    // 문의 작성 처리
    @PostMapping("/studentCourseQuestionWrite")
    public String write(CourseQuestionDTO dto, HttpSession session) {

        SysUserDTO loginUser =
                (SysUserDTO) session.getAttribute("loginUser");
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

        SysUserDTO loginUser =
                (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        CourseQuestionDTO q = service.getQuestionDetail(courseQuestionNo, loginUser);

        StudentCourseDetailDTO courseHeader =
        		 studentCourseService.getStudentCourseHeaderInfo(q.getCourseNo(), loginUser.getUserNo());

        model.addAttribute("course", courseHeader);
        model.addAttribute("nav_qa", true);
        model.addAttribute("courseNo", q.getCourseNo());
        model.addAttribute("question", q);

        return "studentCourse/studentCourseQuestionEditForm";
    }

    // 문의 수정 처리
    @PostMapping("/studentCourseQuestionEdit")
    public String edit(CourseQuestionDTO dto, HttpSession session) {

        SysUserDTO loginUser =
                (SysUserDTO) session.getAttribute("loginUser");
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

        SysUserDTO loginUser =
                (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int courseNo = service.getCourseNoByQuestion(courseQuestionNo);

        boolean ok = service.deleteQuestion(courseQuestionNo, loginUser);
        if (!ok) return "redirect:/accessDenied";

        return "redirect:/studentCourseQuestionList?courseNo=" + courseNo;
    }
}
