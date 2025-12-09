package com.example.lms.controller.studentCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseNoticeService;
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseNoticeController {

    private final StudentCourseNoticeService service;
    private final StudentCourseService studentCourseService;

    // 공지 목록
    @GetMapping("/studentCourseNoticeList")
    public String noticeList(
            @RequestParam int courseNo,
            @RequestParam(defaultValue = "1") int currentPage,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        // 강의 헤더 정보
        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);
        
        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_notice", true);

        int rowPerPage = 10;
        int startRow = (currentPage - 1) * rowPerPage;

        List<StudentCourseNoticeDTO> list = service.getNoticeList(courseNo, startRow, rowPerPage);
        int totalRow = service.getNoticeTotal(courseNo);

        // index 세팅
        int displayIndex = totalRow - startRow;
        for (StudentCourseNoticeDTO dto : list) {
            dto.setCourseNo(courseNo);
            dto.setIndex(displayIndex--);
        }

        int lastPage = (totalRow == 0) ? 1 : (totalRow + rowPerPage - 1) / rowPerPage;

        int pageGroup = (currentPage - 1) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, lastPage);

        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", i);
            map.put("current", i == currentPage);
            map.put("courseNo", courseNo);
            pageList.add(map);
        }

        model.addAttribute("list", list);
        model.addAttribute("pageList", pageList);

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("hasPrev", startPage > 1);
        model.addAttribute("hasNext", endPage < lastPage);
        model.addAttribute("prevPage", startPage - 1);
        model.addAttribute("nextPage", endPage + 1);

        return "studentCourse/studentCourseNoticeList";
    }

    // 공지 상세
    @GetMapping("/studentCourseNoticeDetail")
    public String noticeDetail(
            @RequestParam int courseNoticeNo,
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);
        
        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_notice", true);

        StudentCourseNoticeDTO detail = service.getStudentCourseNoticeDetail(courseNoticeNo);
        model.addAttribute("detail", detail);

        return "studentCourse/studentCourseNoticeDetail";
    }
}
