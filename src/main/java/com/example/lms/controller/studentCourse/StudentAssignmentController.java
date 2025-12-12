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
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentAssignmentService;
import com.example.lms.service.studentCourse.StudentCourseInfoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentAssignmentController {

	private final StudentAssignmentService service;
	private final StudentCourseInfoService infoService;

    private final String UPLOAD_DIR = "C:/lmsUpload/assignment/";

    // 학생 과제 목록
    @GetMapping("/student/assignment/list")
    public String assignmentList(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader =
                infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_assignment", true);

        List<StudentAssignmentListDTO> list =
                service.getAssignmentList(courseNo, user.getUserNo());

        model.addAttribute("assignmentList", list);

        return "studentCourse/studentAssignmentList";
    }

    // 학생 과제 상세
    @GetMapping("/student/assignment/detail")
    public String assignmentDetail(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader =
                infoService.getCourseDetail(courseNo);

        model.addAttribute("course", courseHeader);
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("nav_assignment", true);

        StudentAssignmentDetailDTO detail =
                service.getAssignmentDetail(assignmentNo, user.getUserNo());

        model.addAttribute("detail", detail);

        return "studentCourse/studentAssignmentDetail";
    }

    // 과제 제출/수정
    @PostMapping("/student/assignment/submit")
    public String submitAssignment(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file,
            HttpSession session) throws IOException {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        System.out.println("\n================= [DEBUG] SUBMIT ASSIGNMENT =================");
        System.out.println("assignmentNo = " + assignmentNo);
        System.out.println("courseNo = " + courseNo);
        System.out.println("content = \"" + content + "\"");
        System.out.println("file != null ? " + (file != null));
        System.out.println("file.isEmpty() ? " + (file != null && file.isEmpty()));

        AssignmentSubmissionDTO old =
                service.getSubmission(assignmentNo, user.getUserNo());
        System.out.println("old submission = " + old);
        if (old != null) {
            System.out.println("old.submissionNo = " + old.getAssignmentSubmissionNo());
            System.out.println("old.content = " + old.getAssignmentSubmissionContent());
        }

        AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
        dto.setAssignmentNo(assignmentNo);
        dto.setWriterUserNo(user.getUserNo());
        dto.setAssignmentSubmissionContent(content);

        // submissionNo set 확인
        if (old != null) {
            dto.setAssignmentSubmissionNo(old.getAssignmentSubmissionNo());
            System.out.println("SET dto.submissionNo = " + old.getAssignmentSubmissionNo());
        } else {
            System.out.println("NEW submission → submissionNo 없음 → INSERT 예정");
        }

        System.out.println("==============================================================\n");

        
        if (file != null && !file.isEmpty()) {

            if (old != null && old.getAssignmentSubmissionFileUrl() != null) {
                String oldUrl = old.getAssignmentSubmissionFileUrl();
                String oldName = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
                File oldFile = new File(UPLOAD_DIR + oldName);
                if (oldFile.exists()) oldFile.delete();
            }

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            File target = new File(UPLOAD_DIR + fileName);
            file.transferTo(target);

            dto.setAssignmentSubmissionFileUrl("/upload/assignment/" + fileName);
        } else {
            if (old != null) {
                dto.setAssignmentSubmissionFileUrl(old.getAssignmentSubmissionFileUrl());
            }
        }

        service.submitAssignment(dto);

        return "redirect:/student/assignment/detail?assignmentNo=" + assignmentNo +
                "&courseNo=" + courseNo;
    }

    // 제출 파일 삭제 + 제출 취소
    @PostMapping("/student/assignment/fileDelete")
    public String deleteAssignmentFile(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            HttpSession session) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        AssignmentSubmissionDTO old =
                service.getSubmission(assignmentNo, user.getUserNo());

        if (old != null && old.getAssignmentSubmissionFileUrl() != null) {

            String url = old.getAssignmentSubmissionFileUrl();
            String fileName = url.substring(url.lastIndexOf("/") + 1);

            File target = new File(UPLOAD_DIR + fileName);
            if (target.exists()) target.delete();

            service.cancelSubmission(assignmentNo, user.getUserNo());
        }

        return "redirect:/student/assignment/detail?assignmentNo=" + assignmentNo +
                "&courseNo=" + courseNo;
    }
}
