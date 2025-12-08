package com.example.lms.controller.studentCourse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.lms.dto.AssignmentSubmissionDTO;
import com.example.lms.dto.StudentAssignmentDetailDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentAssignmentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentAssignmentController {

    private final StudentAssignmentService service;

    // ---------------------------------------------------------
    // 학생 과제 목록 페이지
    // ---------------------------------------------------------
    @GetMapping("/student/assignment/list")
    public String assignmentList(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if(user == null) return "redirect:/login";

        List<StudentAssignmentListDTO> list =
                service.getAssignmentList(courseNo, user.getUserNo());

        model.addAttribute("courseNo", courseNo);
        model.addAttribute("assignmentList", list);
        
        return "studentCourse/studentAssignmentList";
    }

    // ---------------------------------------------------------
    // 학생 과제 상세 페이지
    // ---------------------------------------------------------
    @GetMapping("/student/assignment/detail")
    public String assignmentDetail(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            HttpSession session,
            Model model) throws IOException {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if(user == null) return "redirect:/login";

        StudentAssignmentDetailDTO detail =
                service.getAssignmentDetail(assignmentNo, user.getUserNo());

        model.addAttribute("courseNo", courseNo);
        model.addAttribute("assignment", detail);

        return "studentCourse/studentAssignmentDetail";
    }

    // ---------------------------------------------------------
    // 학생 과제 제출 / 수정 처리
    // ---------------------------------------------------------
    @PostMapping("/student/assignment/submit")
    public String submitAssignment(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file,
            HttpSession session) throws IOException {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if(user == null) return "redirect:/login";

        AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
        dto.setAssignmentNo(assignmentNo);
        dto.setWriterUserNo(user.getUserNo());
        dto.setAssignmentSubmissionContent(content);

        if(file != null && !file.isEmpty()) {
            String uploadDir = "C:/lmsUpload/assignment/";
            File dir = new File(uploadDir);
            if(!dir.exists()) dir.mkdirs();

            File target = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(target);

            dto.setAssignmentSubmissionFileUrl("/upload/assignment/" + file.getOriginalFilename());
        }

        service.submitAssignment(dto);

        return "redirect:/student/assignment/detail?assignmentNo=" + assignmentNo +
               "&courseNo=" + courseNo;
    }
}
