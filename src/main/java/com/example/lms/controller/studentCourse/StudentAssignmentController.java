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
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentAssignmentController {

    private final StudentAssignmentService service;
    private final StudentCourseService studentCourseService;

    private final String UPLOAD_DIR = "C:/lmsUpload/assignment/";


    // ---------------------------------------------------------
    // 📌 학생 과제 목록 페이지
    // ---------------------------------------------------------
    @GetMapping("/student/assignment/list")
    public String assignmentList(
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);
        model.addAttribute("course", courseHeader);
        model.addAttribute("nav_assignment", true);

        List<StudentAssignmentListDTO> list =
                service.getAssignmentList(courseNo, user.getUserNo());

        model.addAttribute("assignmentList", list);

        return "studentCourse/studentAssignmentList";
    }


    // ---------------------------------------------------------
    // 📌 학생 과제 상세 페이지
    // ---------------------------------------------------------
    @GetMapping("/student/assignment/detail")
    public String assignmentDetail(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            HttpSession session,
            Model model) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        // 🔥 course 헤더 데이터 조회
        StudentCourseDetailDTO courseHeader =
                studentCourseService.getStudentCourseDetail(courseNo);

        // 🔥 course 를 모델에 넣어야 함
        model.addAttribute("course", courseHeader);

        // 과제 상세
        StudentAssignmentDetailDTO detail =
                service.getAssignmentDetail(assignmentNo, user.getUserNo());

        model.addAttribute("detail", detail);

        return "studentCourse/studentAssignmentDetail";
    }



    // ---------------------------------------------------------
    // 📌 제출 / 수정 처리
    // ---------------------------------------------------------
    @PostMapping("/student/assignment/submit")
    public String submitAssignment(
            @RequestParam int assignmentNo,
            @RequestParam int courseNo,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file,
            HttpSession session) throws IOException {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        AssignmentSubmissionDTO old =
                service.getSubmission(assignmentNo, user.getUserNo());

        AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
        dto.setAssignmentNo(assignmentNo);
        dto.setWriterUserNo(user.getUserNo());
        dto.setAssignmentSubmissionContent(content);

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {

            // 기존 파일 삭제
            if (old != null && old.getAssignmentSubmissionFileUrl() != null) {
                String oldUrl = old.getAssignmentSubmissionFileUrl();
                String oldName = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
                File oldFile = new File(UPLOAD_DIR + oldName);
                if (oldFile.exists()) oldFile.delete();
            }

            // 폴더 확인
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            // 저장 파일명
            String fileName = file.getOriginalFilename();
            File target = new File(UPLOAD_DIR + fileName);
            file.transferTo(target);

            // DB에는 URL 형태로 저장
            dto.setAssignmentSubmissionFileUrl("/upload/assignment/" + fileName);
        }
        else {
            // 파일 유지
            if (old != null) {
                dto.setAssignmentSubmissionFileUrl(old.getAssignmentSubmissionFileUrl());
            }
        }

        service.submitAssignment(dto);

        return "redirect:/student/assignment/detail?assignmentNo=" + assignmentNo +
                "&courseNo=" + courseNo;
    }

    // 학생 제출 파일 삭제
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

            // 실제 파일 삭제
            String url = old.getAssignmentSubmissionFileUrl();
            String fileName = url.substring(url.lastIndexOf("/") + 1);

            File target = new File(UPLOAD_DIR + fileName);
            if (target.exists()) target.delete();

            // 제출 취소 (status=1 + file/content null)
            service.cancelSubmission(assignmentNo, user.getUserNo());
        }

        return "redirect:/student/assignment/detail?assignmentNo=" + assignmentNo +
                "&courseNo=" + courseNo;
    }

}
