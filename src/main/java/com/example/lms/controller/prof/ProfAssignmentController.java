package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.lms.dto.AssignmentDTO;
import com.example.lms.dto.ProfCourseAssignmentDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.prof.ProfAssignmentService;
import com.example.lms.service.prof.ProfCourseGradeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfAssignmentController {

    @Autowired
    ProfAssignmentService assignmentService;
    
    @Autowired
    ProfCourseGradeService gradeService;
    
    // 메뉴
    @GetMapping("/profCourseAssignment")
    public String profCourseAssignment(Model model,
    								 @SessionAttribute("loginUser") SysUserDTO user) {

        int professorUserNo = user.getUserNo();
        List<ProfCourseAssignmentDTO> list = assignmentService.getCourseAssignmentSummary(professorUserNo);

        model.addAttribute("courseAssignmentSummaryList", list);

        return "profAssignment/profCourseAssignment"; 
    }

    // 리스트
    @GetMapping("/profCourseAssignmentList")
    public String assignmentList(Model model,
								          @RequestParam("courseNo") int courseNo,
								          @RequestParam(value = "currentPage", defaultValue = "1") int currentPage) {

        int rowPerPage = 10;
        int startRow = (currentPage - 1) * rowPerPage;

        List<AssignmentDTO> list = assignmentService.getCourseAssignmnetList(courseNo, startRow, rowPerPage);

        int totalRow = assignmentService.getCourseAssignmentCount(courseNo);
        int lastPage = (totalRow % rowPerPage == 0) ? (totalRow / rowPerPage) : (totalRow / rowPerPage) + 1;

        int startPage = ((currentPage - 1) / 10 * 10) + 1;
        int endPage = startPage + 9;
        if (endPage > lastPage) endPage = lastPage;

        List<Integer> pages = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) pages.add(i);

        model.addAttribute("list", list);
        model.addAttribute("courseNo", courseNo);

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("pages", pages);
        
        model.addAttribute("prePage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);

        model.addAttribute("showPrev", currentPage > 1);
	    model.addAttribute("showNext", currentPage < lastPage);
	    model.addAttribute("showPagination", lastPage > 1);

        return "profAssignment/profCourseAssignmentList";
    }

    // 상세보기
    @GetMapping("/profCourseAssignmentDetail")
    public String assignmentDetail(
            Model model,
            @RequestParam("assignmentNo") int assignmentNo,
            @RequestParam("courseNo") int courseNo) {

        AssignmentDTO assignmentDTO = assignmentService.getCourseAssignmentDetail(assignmentNo);
        List<ProfCourseAssignmentDTO> submissionList = assignmentService.getCourseSubmissionList(assignmentNo, courseNo);
        
        model.addAttribute("assignment", assignmentDTO);
        model.addAttribute("submissionList", submissionList);
        model.addAttribute("courseNo", courseNo);

        return "profAssignment/profCourseAssignmentDetail";
    }
    
    // 점수 저장
    @PostMapping("/profAssignmentScore")
    public String updateAssignmentScore(@RequestParam("assignmentSubmissionNo") int submissionNo,
							            @RequestParam("assignmentNo") int assignmentNo,
							            @RequestParam("courseNo") int courseNo,
							            @RequestParam("assignmentScore") Integer score) { 

    	assignmentService.updateSubmissionScore(submissionNo, score);
    	
    	gradeService.recalculateAndSaveFinalGrade(courseNo, submissionNo);

        return "redirect:/profCourseAssignmentDetail?assignmentNo=" + assignmentNo + "&courseNo=" + courseNo;
    }

    // 등록 폼
    @GetMapping("/addAssignment")
    public String addAssignmentForm(Model model, HttpSession session, @RequestParam("courseNo") int courseNo) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
		    return "redirect:/login";
		}

        model.addAttribute("courseNo", courseNo);

        return "profAssignment/addAssignment";
    }
    // 등록 처리
    @PostMapping("/addAssignment")
    public String addAssignment(AssignmentDTO assignment, HttpSession session) {

        assignmentService.addAssignment(assignment);

        return "redirect:/profCourseAssignmentList?courseNo=" + assignment.getCourseNo();
    }

    // 수정 폼
    @GetMapping("/modifyAssignment")
    public String modifyAssignmentForm(Model model, 
    									HttpSession session,
    									@RequestParam("assignmentNo") int assignmentNo) {

        AssignmentDTO assignment = assignmentService.getCourseAssignmentDetail(assignmentNo);
        model.addAttribute("assignment", assignment);
        
        model.addAttribute("assignmentStatus1", assignment.getAssignmentStatus() == 1);
        model.addAttribute("assignmentStatus0", assignment.getAssignmentStatus() == 0);


        return "profAssignment/modifyAssignment";
    }
    // 수정 처리
    @PostMapping("/modifyAssignment")
    public String modifyAssignment(AssignmentDTO assignment) {

        assignmentService.modifyAssignment(assignment);

        return "redirect:/profCourseAssignmentDetail?assignmentNo=" 
                + assignment.getAssignmentNo()
                + "&courseNo=" + assignment.getCourseNo();
    }

    // 삭제
    @GetMapping("/removeAssignment")
    public String removeAssignment(@RequestParam int assignmentNo) {

        AssignmentDTO assignment = assignmentService.getCourseAssignmentDetail(assignmentNo);
        int courseNo = assignment.getCourseNo();

        assignmentService.removeAssignment(assignmentNo);

        return "redirect:/profCourseAssignmentList?courseNo=" + courseNo;
    }
    
}
