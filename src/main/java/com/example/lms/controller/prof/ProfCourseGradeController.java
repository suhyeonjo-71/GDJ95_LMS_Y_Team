package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.ProfCourseGradeDTO;
import com.example.lms.dto.ProfCourseStudDTO;
import com.example.lms.service.prof.ProfCourseGradeService;
import com.example.lms.service.prof.ProfCourseStudService;


@Controller
public class ProfCourseGradeController {

	@Autowired
	ProfCourseGradeService profCourseGradeService;
	
	@Autowired
	ProfCourseStudService profCourseStudService;
	
	// 학생 성적 리스트
	@GetMapping("/profCourseGradeList")
	public String profCourseGradeList(Model model,
									@RequestParam("courseNo") int courseNo,
									@RequestParam(value = "currentPage", defaultValue = "1") int currentPage) {
		
		int rowPerPage = 10;
        int startRow = (currentPage - 1) * rowPerPage;
        
        List<ProfCourseStudDTO> list = profCourseStudService.getCourseStudentList(courseNo, startRow, rowPerPage);
        
        int totalRow = profCourseStudService.getCourseStudentCount(courseNo);
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

        return "profCourseGrade/profCourseGradeList";
	}
	
	// 성적 입력 폼
	@GetMapping("/profCourseGradeForm")
	public String gradeForm(@RequestParam int courseNo,
	                        @RequestParam int studentUserNo,
	                        Model model) {
		
		// 기본 데이터 조회 (출석률 / 과제 점수 / 시험 만점)
        ProfCourseGradeDTO dto = profCourseGradeService.getGradeData(courseNo, studentUserNo);
        if (dto == null) {
        	dto = new ProfCourseGradeDTO();
        	dto.setCourseNo(courseNo);
        	dto.setStudentUserNo(studentUserNo);
        	dto.setAttendanceRate(0);
        	dto.setAssignmentScore(0.0);
        	dto.setExamMaxScore(100);
        }

		model.addAttribute("courseNo", courseNo);
	    model.addAttribute("studentUserNo", studentUserNo);
	    model.addAttribute("gradeData", dto); 
	    
	    return "profCourseGrade/profCourseGradeForm";
	}
	
	// 성적 계산, 저장
	@PostMapping("/profCourseGradeForm")
	public String calcGrade(@RequestParam int courseNo,
							@RequestParam int studentUserNo,
							@RequestParam(name = "assignmentScore", defaultValue = "0") double assignmentScore,
	                        @RequestParam(name = "examScore", defaultValue = "0") double examScore,
	                        Model model) {
		
		// 1) DB에서 출석률 / 과제 기본값 / 시험만점 가져오기
        ProfCourseGradeDTO dto = profCourseGradeService.getGradeData(courseNo, studentUserNo);
        if (dto == null) {
        	dto = new ProfCourseGradeDTO();
        	dto.setCourseNo(courseNo);
        	dto.setStudentUserNo(studentUserNo);
        	dto.setAttendanceRate(0);
        	dto.setExamMaxScore(100);
        }

        // 2) 폼에서 받은 과제/시험 점수 반영
        dto.setAssignmentScore(assignmentScore);
        dto.setExamScore(examScore);

        // 3) 비율 설정 (교수 재량 – 일단 고정값)
        dto.setAttendanceRatio(10);
        dto.setAssignmentRatio(20);
        dto.setExamRatio(70);

        // 4) 계산
        ProfCourseGradeDTO result = profCourseGradeService.calculate(dto);

        // 5) DB 저장 (시험 원점수 + 최종점수 + 등급)
        profCourseGradeService.saveGrade(studentUserNo, courseNo, examScore, assignmentScore,
                						result.getGradeValue(), result.getFinalScore()); 
        
        model.addAttribute("courseNo", courseNo);
        model.addAttribute("studentUserNo", studentUserNo);
        model.addAttribute("gradeData", result);
        
        return "redirect:/profCourseGradeList?courseNo=" + courseNo;
	}
	

}
