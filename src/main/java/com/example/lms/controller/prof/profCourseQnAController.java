package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.prof.ProfCourseQnAService;

import jakarta.servlet.http.HttpSession;

@Controller
public class profCourseQnAController {
	
	@Autowired
	ProfCourseQnAService profCourseQnAService;
	
	// 메뉴
	@GetMapping("/profCourseQnA")
	public String profCourseQnAMain(Model model, HttpSession session) {

	    SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
	    if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }

	    model.addAttribute("courseQnASummaryList", profCourseQnAService.getCourseQnASummary(loginUser.getUserNo()));

	    return "profCourseQnA/profCourseQnA";
	}
	
	// 문의 목록
	@GetMapping("/profCourseQnAList")
	public String profCourseQuestionList(@RequestParam("courseNo") int courseNo,
									     @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
									     Model model,
									     HttpSession session) {

	    SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
	    if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }

	    int rowPerPage = 10;
	    int startRow = (currentPage - 1) * rowPerPage;

	    // 페이징된 리스트
	    List<Map<String, Object>> list = profCourseQnAService.getQuestionList(courseNo, startRow, rowPerPage);

	    for(Map<String, Object> question : list) {
	    	Object answeredObj = question.get("answered");
	    	
	    	boolean isAnswered = false;
	        if (answeredObj instanceof Number) {
	            isAnswered = ((Number) answeredObj).intValue() == 1;
	        }
	        
	        question.put("isAnswered", isAnswered);
	    }

	    int totalRow = profCourseQnAService.getQuestionCount(courseNo);
	    int lastPage = (totalRow % rowPerPage == 0) ? (totalRow / rowPerPage) : (totalRow / rowPerPage) + 1;

	    int startPage = ((currentPage - 1) / 10 * 10) + 1;
	    int endPage = startPage + 9;
	    if (endPage > lastPage) endPage = lastPage;

	    List<Integer> pages = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) pages.add(i);

	    model.addAttribute("courseNo", courseNo);
	    model.addAttribute("list", list);

	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("prePage", currentPage - 1);
	    model.addAttribute("nextPage", currentPage + 1);

	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("lastPage", lastPage);
	    model.addAttribute("pages", pages);

	    model.addAttribute("showPrev", currentPage > 1);
	    model.addAttribute("showNext", currentPage < lastPage);
	    model.addAttribute("showPagination", lastPage > 1);

	    return "profCourseQnA/profCourseQnAList";
	}

	
	// 상세
	@GetMapping("/profCourseQnADetail")
	public String profCourseQuestionDetail(@RequestParam("courseQuestionNo") int courseQuestionNo,
											@RequestParam("courseNo") int courseNo,
											Model model,
											HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
            return "redirect:/login";
        }
        
        model.addAttribute("question",profCourseQnAService.getQuestionDetail(courseQuestionNo));
        model.addAttribute("answers", profCourseQnAService.getAnswerList(courseQuestionNo));
        model.addAttribute("courseQuestionNo", courseQuestionNo);
        model.addAttribute("courseNo", courseNo);
        
        return "profCourseQnA/profCourseQnADetail";
	}
	
	// 답변 작성
	@GetMapping("/profCourseAnswerForm")
	public String profCourseAnswerForm(@RequestParam("courseQuestionNo") int courseQuestionNo,
										@RequestParam("courseNo") int courseNo,
										Model model,
										HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
            return "redirect:/login";
        }
        
        model.addAttribute("courseQuestionNo", courseQuestionNo);
        model.addAttribute("courseNo", courseNo);
        
        return "profCourseQnA/profCourseAnswerForm";
	}
	
	@PostMapping("/profCourseAnswer")
	public String profCourseAnswer(@RequestParam("courseQuestionNo") int courseQuestionNo,
                                  @RequestParam("courseNo") int courseNo,
                                  @RequestParam("answerContent") String answerContent,
                                  HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		
		profCourseQnAService.addAnswer(courseQuestionNo, loginUser.getUserNo(), answerContent);
		
		return "redirect:/profCourseQnADetail?courseQuestionNo=" + courseQuestionNo + "&courseNo=" + courseNo;
	}
	
	// 답변 수정
	@PostMapping("/profCourseAnswerUpdate")
	public String profCourseAnswerUpdate(@RequestParam("answerNo") int answerNo,
								        @RequestParam("courseQuestionNo") int courseQuestionNo,
								        @RequestParam("courseNo") int courseNo,
								        @RequestParam("answerContent") String answerContent) {
		
		profCourseQnAService.updateAnswer(answerNo, answerContent);
		
		return "redirect:/profCourseQnADetail?courseQuestionNo=" + courseQuestionNo + "&courseNo=" + courseNo;
	}
	
	// 답변 삭제
    @GetMapping("/profCourseAnswerDelete")
    public String profAnswerDelete(@RequestParam("answerNo") int answerNo,
                                   @RequestParam("courseQuestionNo") int courseQuestionNo,
                                   @RequestParam("courseNo") int courseNo) {

    	profCourseQnAService.deleteAnswer(answerNo, courseQuestionNo);

        return "redirect:/profCourseQnADetail?courseQuestionNo=" + courseQuestionNo + "&courseNo=" + courseNo;
    }
}