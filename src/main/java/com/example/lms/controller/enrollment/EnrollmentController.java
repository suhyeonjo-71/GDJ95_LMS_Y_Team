package com.example.lms.controller.enrollment;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.dto.EnrollmentListDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.enrollment.EnrollmentQueryService;
import com.example.lms.service.enrollment.EnrollmentService;
import com.example.lms.service.enrollment.EnrollmentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentQueryService queryService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentCourseService courseService;

    // 수강신청 리스트 (필터 + 페이징)
    @GetMapping("/courseListForEnrollment")
    public String courseListForEnrollment(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "yoil", required = false) Integer yoil,
            @RequestParam(value = "professor", required = false) String professor,
            @RequestParam(value = "deptCode", required = false) String deptCode,
            HttpSession session,
            Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int studentUserNo = loginUser.getUserNo();
        int rowPerPage = 10;
        int startRow = (currentPage - 1) * rowPerPage;

        // 필터 정제
        Integer yoilClean = (yoil == null || yoil < 1 || yoil > 5) ? 0 : yoil;
        String professorClean = (professor == null) ? "" : professor.trim();
        String deptCodeClean = (deptCode == null) ? "" : deptCode.trim();

        // DB 조회
        List<StudentCourseDTO> list = queryService.getCourseListForEnrollment(
                studentUserNo,
                yoilClean,
                professorClean,
                deptCodeClean,
                startRow,
                rowPerPage
        );

        int totalRow = queryService.countFilteredCourseList(yoilClean, professorClean, deptCodeClean);
        int lastPage = (totalRow + rowPerPage - 1) / rowPerPage;

        // 페이지 그룹 (5개 단위)
        int pageGroup = (currentPage - 1) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, lastPage);

        // pageList 구성
        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("page", i);
            p.put("current", i == currentPage);

            // 필터 유지용
            p.put("yoil", yoilClean);
            p.put("professor", professorClean);
            p.put("deptCode", deptCodeClean);

            pageList.add(p);
        }

        // 화면 전달
        model.addAttribute("courseList", list);
        model.addAttribute("deptList", queryService.getDeptList());

        model.addAttribute("yoil", yoilClean);
        model.addAttribute("professor", professorClean);
        model.addAttribute("deptCode", deptCodeClean);

        model.addAttribute("pageList", pageList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", lastPage);

        boolean hasPrev = currentPage > 1;
        boolean hasNext = currentPage < lastPage;

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        // Mustache 렌더링 오류 방지 – 항상 prev/next 제공
        int prevPage = hasPrev ? currentPage - 1 : 1;
        int nextPage = hasNext ? currentPage + 1 : lastPage;

        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);


        // HEADER
        model.addAttribute("pageTitle", "수강가능 강의");
        model.addAttribute("pageDescription", "강의 정보를 확인하고 수강신청을 진행할 수 있습니다");
        model.addAttribute("loginUserName", loginUser.getUserName());
        model.addAttribute("nav_enrollment", true);

        return "enrollment/courseListForEnrollment";
    }

    // 수강신청 처리
    @PostMapping("/addEnrollment")
    public String addEnrollment(
            EnrollmentDTO dto,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(value = "yoil", required = false) Integer yoil,
            @RequestParam(value = "professor", required = false) String professor,
            @RequestParam(value = "deptCode", required = false) String deptCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        dto.setStudentUserNo(loginUser.getUserNo());
        dto.setEnrollmentStatus(0);

        String msg = enrollmentService.addEnrollment(dto);
        redirectAttributes.addFlashAttribute("message", msg);

        // 리다이렉트 유지
        StringBuilder redirectUrl = new StringBuilder("redirect:/courseListForEnrollment?currentPage=" + currentPage);

        if (yoil != null) redirectUrl.append("&yoil=").append(yoil);
        if (professor != null && !professor.isEmpty()) redirectUrl.append("&professor=").append(professor);
        if (deptCode != null && !deptCode.isEmpty()) redirectUrl.append("&deptCode=").append(deptCode);

        return redirectUrl.toString();
    }

    // 수강신청 내역
    @GetMapping("/enrollmentList")
    public String enrollmentList(
            Model model,
            HttpSession session,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int studentUserNo = loginUser.getUserNo();
        int rowPerPage = 10;
        int startRow = (currentPage - 1) * rowPerPage;

        List<EnrollmentListDTO> enrollmentList = 
                enrollmentService.getEnrollmentList(studentUserNo, startRow, rowPerPage);

        model.addAttribute("list", enrollmentList);

        int totalRow = enrollmentService.getEnrollmentTotalCount(studentUserNo);
        int lastPage = (totalRow + rowPerPage - 1) / rowPerPage;

        int pageGroup = (currentPage - 1) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, lastPage);

        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("page", i);
            p.put("current", i == currentPage);
            pageList.add(p);
        }

        model.addAttribute("pageList", pageList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", lastPage);

        boolean hasPrev = currentPage > 1;
        boolean hasNext = currentPage < lastPage;

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        // Mustache 렌더링 오류 방지 – 항상 prev/next 제공
        int prevPage = hasPrev ? currentPage - 1 : 1;
        int nextPage = hasNext ? currentPage + 1 : lastPage;

        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        // HEADER
        model.addAttribute("pageTitle", "수강신청 내역");
        model.addAttribute("pageDescription", "신청 완료한 강의와 취소 내역을 확인할 수 있습니다.");
        model.addAttribute("loginUserName", loginUser.getUserName());
        model.addAttribute("nav_enrollment", true);

        return "enrollment/enrollmentList";
    }

    // 수강 취소
    @PostMapping("/cancelEnrollment")
    public String cancelEnrollment(
            @RequestParam int enrollmentNo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String msg = enrollmentService.cancelEnrollment(loginUser.getUserNo(), enrollmentNo);
        redirectAttributes.addFlashAttribute("message", msg);

        return "redirect:/enrollmentList";
    }
}
