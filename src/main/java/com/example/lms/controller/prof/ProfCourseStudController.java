package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.AttendanceDTO;
import com.example.lms.dto.ProfCourseStudDTO;
import com.example.lms.service.prof.ProfCourseStudService;

@Controller
public class ProfCourseStudController {
	
	@Autowired
	ProfCourseStudService profCourseStudService;
	
	@GetMapping("/profCourseStudList")
	public String profCourseStudList(Model model,
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
	    for (int i = startPage; i <= endPage; i++) {
	        pages.add(i);
	    }
	    
	    boolean showPrev = currentPage > 1;
	    boolean showNext = currentPage < lastPage;
	    boolean showPagination = lastPage > 1; // 페이지가 1개면 전체 숨김
	    
	    model.addAttribute("courseNo", courseNo);

	    model.addAttribute("list", list);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("lastPage", lastPage);
	    model.addAttribute("pages", pages); 

	    model.addAttribute("prePage", currentPage > 1 ? currentPage - 1 : 1);
	    model.addAttribute("nextPage", currentPage < lastPage ? currentPage + 1 : lastPage);
	    
	    model.addAttribute("showPrev", showPrev);
	    model.addAttribute("showNext", showNext);
	    model.addAttribute("showPagination", showPagination);
	      
	    return "profCourseStud/profCourseStudList";
	}

}
