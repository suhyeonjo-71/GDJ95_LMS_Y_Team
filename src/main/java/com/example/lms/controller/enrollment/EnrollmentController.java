package com.example.lms.controller.enrollment;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.dto.EnrollmentListDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentTimetableDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.enrollment.EnrollmentService;
import com.example.lms.service.studentCourse.StudentCourseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentCourseService studentCourseService;
	
    // ---------------------------------------------------------
 	// 필터
 	// ---------------------------------------------------------
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

        // ===== 필터 값 정제 =====
        Integer yoilClean = (yoil == null || yoil < 1 || yoil > 5) ? 0 : yoil;

        String professorClean = (professor == null) ? "" : professor.trim();
        if (professorClean.isBlank()) professorClean = "";

        String deptCodeClean = (deptCode == null) ? "" : deptCode.trim();
        if (deptCodeClean.isBlank()) deptCodeClean = "";

        // DB 조회
        List<StudentCourseDTO> list =
                studentCourseService.getCourseListForStudentFiltered(
                        studentUserNo, yoilClean, professorClean, deptCodeClean, startRow, rowPerPage
                );

        int totalRow = studentCourseService.countFilteredCourseList(yoilClean, professorClean, deptCodeClean);
        int lastPage = (totalRow + rowPerPage - 1) / rowPerPage;

        // 페이징 계산
        int pageGroup = (currentPage - 1) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, lastPage);

        // pageList 구성
        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("page", i);
            p.put("current", i == currentPage);

            // 필터 값 넣기
            p.put("yoil", yoilClean);
            p.put("professor", professorClean);
            p.put("deptCode", deptCodeClean);

            pageList.add(p);
        }

        // ===== Model에 넣기 =====
        model.addAttribute("courseList", list);
        model.addAttribute("yoil", yoilClean);
        model.addAttribute("professor", professorClean);
        model.addAttribute("deptCode", deptCodeClean);
        model.addAttribute("deptList", studentCourseService.getDeptList());

        model.addAttribute("pageList", pageList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("hasPrev", currentPage > 1);
        model.addAttribute("hasNext", currentPage < lastPage);
        model.addAttribute("prevPage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);
        
        // HEADER 표시용
        model.addAttribute("pageTitle", "수강가능 강의");
        model.addAttribute("pageDescription", "강의 정보를 확인하고 수강신청을 진행할 수 있습니다");
        model.addAttribute("loginUserName", loginUser.getUserName());
        
        model.addAttribute("nav_enrollment", "text-blue-600 border-blue-600");
        
        return "enrollment/courseListForEnrollment";
    }

	 // ---------------------------------------------------------
	 // 수강 신청 처리
	 // ---------------------------------------------------------
	 @PostMapping("/addEnrollment")
	 public String addEnrollment(
	         EnrollmentDTO dto,
	         @RequestParam(defaultValue="1") int currentPage,
	         // ✅ 1. 필터링 파라미터 추가
	         @RequestParam(value = "yoil", required = false) Integer yoil,
	         @RequestParam(value = "professor", required = false) String professor,
	         @RequestParam(value = "deptCode", required = false) String deptCode,
	         HttpSession session,
	         RedirectAttributes redirectAttributes) {
	
	     SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
	
	     dto.setStudentUserNo(loginUser.getUserNo());
	     dto.setEnrollmentStatus(0); // 기본 신청값
	
	     String msg = enrollmentService.addEnrollment(dto);
	
	     redirectAttributes.addFlashAttribute("message", msg);
	     redirectAttributes.addFlashAttribute("currentPage", currentPage);
	     
	     // ✅ 2. 리다이렉트 URL 구성 (필터 파라미터 포함)
	     StringBuilder redirectUrl = new StringBuilder("redirect:/courseListForEnrollment?currentPage=").append(currentPage);
	     
	     if (yoil != null) {
	         redirectUrl.append("&yoil=").append(yoil);
	     }
	     if (professor != null && !professor.isEmpty()) {
	         redirectUrl.append("&professor=").append(professor);
	     }
	     if (deptCode != null && !deptCode.isEmpty()) {
	         redirectUrl.append("&deptCode=").append(deptCode);
	     }
	
	     return redirectUrl.toString();
	 }

    // 수강 신청 내역
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

        // 신청 내역 조회
        List<EnrollmentListDTO> list =
                enrollmentService.getEnrollmentList(studentUserNo, startRow, rowPerPage);

        model.addAttribute("list", list);

        // 전체 row
        int totalRow = enrollmentService.getEnrollmentTotalCount(studentUserNo);
        int lastPage = (totalRow + rowPerPage - 1) / rowPerPage;

        // 페이지 그룹 (5개 단위)
        int pageGroup = (currentPage - 1) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = startPage + 4;
        if (endPage > lastPage) endPage = lastPage;

        // pageList 구성
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
        model.addAttribute("hasPrev", currentPage > 1);
        model.addAttribute("hasNext", currentPage < lastPage);
        model.addAttribute("prevPage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);

        // HEADER 표시용
        model.addAttribute("pageTitle", "수강신청 내역");
        model.addAttribute("pageDescription", "신청 완료한 강의와 취소 내역을 확인할 수 있습니다.");
        model.addAttribute("loginUserName", loginUser.getUserName());
        
        model.addAttribute("nav_enrollment", true);

        return "enrollment/enrollmentList";
    }

    // ---------------------------------------------------------
    // 수강 취소
    // ---------------------------------------------------------
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