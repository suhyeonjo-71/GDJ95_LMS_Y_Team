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

import com.example.lms.dto.CourseDTO;
import com.example.lms.dto.CourseNoticeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.prof.ProfCourseNoticeService;
import com.example.lms.service.prof.ProfCourseService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfCourseNoticeController {

    private final ProfCourseService courseService;

	@Autowired
	ProfCourseNoticeService courseNoticeService;

    ProfCourseNoticeController(ProfCourseService courseService) {
        this.courseService = courseService;
    }
    
    // 메뉴
    @GetMapping("/profCourseNotice")
    public String profCourseNotice(Model model, @SessionAttribute("loginUser") SysUserDTO user) {
    	
        int professorUserNo = user.getUserNo();
        
        List<CourseNoticeDTO> list = courseNoticeService.getCourseNoticeSummary(professorUserNo);
        model.addAttribute("courseNoticeSummaryList", list);
        
        return "profCourseNotice/profCourseNotice";  // 카드형 템플릿
    }

	
	// 공지사항 리스트
	@GetMapping("/profCourseNoticeList")
	public String courseNoticeList(Model model, 
									@RequestParam("courseNo") int courseNo, 
									@RequestParam(value = "currentPage", defaultValue = "1") int currentPage) {
								
		int rowPerPage = 10;
	    int startRow = (currentPage - 1) * rowPerPage;
		
		List<CourseNoticeDTO> list = courseNoticeService.getCourseNoticeListByPage(courseNo, startRow, rowPerPage);
		
		int totalRow = courseNoticeService.getCourseNoticeCount(courseNo);
		int lastPage = (totalRow % rowPerPage == 0) ? (totalRow / rowPerPage) : (totalRow / rowPerPage) +1;
		
		int startPage = ((currentPage - 1) / 10 * 10) + 1;
	    int endPage = startPage + 9;
	    if (endPage > lastPage) endPage = lastPage;
	    
	    List<Integer> pages = new ArrayList<>();
	    for (int i = startPage; i <= endPage; i++) pages.add(i);
	    
	    model.addAttribute("courseNo", courseNo);
	    model.addAttribute("list", list);
	    
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("lastPage", lastPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("pages", pages);
	    
	    model.addAttribute("prePage", currentPage > 1 ? currentPage - 1 : 1);
	    model.addAttribute("nextPage", currentPage < lastPage ? currentPage + 1 : lastPage);
	    
	    model.addAttribute("showPrev", currentPage > 1);
	    model.addAttribute("showNext", currentPage < lastPage);
	    model.addAttribute("showPagination", lastPage > 1);
	    
		return "profCourseNotice/profCourseNoticeList";
	}
	
	
	// 상세보기 + 조회수
	@GetMapping("/profCourseNoticeDetail")
	public String courseNoticeDetai(Model model, @RequestParam("courseNoticeNo") int courseNoticeNo) {
		
		CourseNoticeDTO courseNoticeDTO = courseNoticeService.getCourseNoticeDetail(courseNoticeNo);
		model.addAttribute("courseNotice", courseNoticeDTO);
		
		return "profCourseNotice/profCourseNoticeDetail";
	}
	
	// 등록
	@PostMapping("/addCourseNotice")
	public String addcourseNotice(CourseNoticeDTO courseNotice, HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
		
		// 작성자 : 로그인한 교수 
		courseNotice.setWriterUserNo(loginUser.getUserNo());
		// 조회수 0
		courseNotice.setCourseNoticeViewCount(0);
		
		courseNoticeService.addCourseNotice(courseNotice);
		
		return "redirect:/profCourseNoticeList?courseNo=" + courseNotice.getCourseNo();
	}
	
	
	@GetMapping("/addCourseNotice")
	public String addCourseNotice(@RequestParam("courseNo") int courseNo, Model model, HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		
		// 로그인, 교수 X
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
		 

		 model.addAttribute("courseNo", courseNo);
		
		 return "profCourseNotice/addCourseNotice";
	}
	
	// 수정
	@GetMapping("/modifyCourseNotice")
	public String modifyCourseNoticeForm(Model model, 
										HttpSession session, 
										@RequestParam("courseNoticeNo") int courseNoticeNo) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
		
		CourseNoticeDTO courseNotice = courseNoticeService.getCourseNoticeDetail(courseNoticeNo);
		model.addAttribute("courseNotice", courseNotice);

		return "profCourseNotice/modifyCourseNotice";
	}
	
	@PostMapping("/modifyCourseNotice")
	public String modifyCourseNoitce(HttpSession session, 
									CourseNoticeDTO courseNotice) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
		
		courseNotice.setWriterUserNo(loginUser.getUserNo());
		courseNoticeService.modifyCourseNotice(courseNotice);
		
		 return "redirect:/profCourseNoticeDetail?courseNoticeNo=" + courseNotice.getCourseNoticeNo();
	}
	
	// 삭제
	@GetMapping("/removeCourseNotice")
	public String removeCourseNotice(HttpSession session, @RequestParam int courseNoticeNo) {
	
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
	    
	    CourseNoticeDTO courseNotice = courseNoticeService.getCourseNoticeDetail(courseNoticeNo);
	    int courseNo = courseNotice.getCourseNo();
		
		courseNoticeService.removeCourseNotice(courseNoticeNo);
		
		return "redirect:/profCourseNoticeList?courseNo=" + courseNo;
	}
}
