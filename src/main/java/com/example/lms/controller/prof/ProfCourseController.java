package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.ProfCourseDTO;
import com.example.lms.dto.ProfCourseTimeDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.mapper.prof.ProfCourseMapper;
import com.example.lms.service.prof.ProfCourseService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfCourseController {
	
	@Autowired
	private ProfCourseService courseService;
	
	@Autowired
	private ProfCourseMapper courseMapper;
	
	// 교수별 강의 리스트
	@GetMapping("/courseList")
	public String courseListByProfessor(HttpSession session,
								        Model model,
								        @RequestParam(value = "year", required = false) Integer year,
								        @RequestParam(value = "semester", required = false) String semester,
								        @RequestParam(value = "status", required = false) String status,
								        @RequestParam(value = "currentPage", defaultValue = "1") int currentPage) {

	    SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
	    if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }
	    
	    List<Integer> distinctYears = courseService.getDistinctYears(loginUser.getUserNo());
	    
	    List<Map<String, Object>> yearOptions = new ArrayList<>();
	    for (int y : distinctYears) {
	        Map<String, Object> option = new HashMap<>();
	        option.put("value", y);
	        option.put("isSelected", year != null && y == year.intValue()); 
	        yearOptions.add(option);
	    }
	    model.addAttribute("yearOptions", yearOptions);
	    
	    Map<String, Boolean> selected = new HashMap<>();
	    selected.put("semester1", "1".equals(semester));
	    selected.put("semester2", "2".equals(semester));
	    selected.put("status1", "1".equals(status));
	    selected.put("status0", "0".equals(status));
	    model.addAttribute("selected", selected);

	    int rowPerPage = 10;
	    int startRow = (currentPage - 1) * rowPerPage;

	    List<ProfCourseDTO> courseList =
	    		courseService.getFilteredCourseList(loginUser.getUserNo(), year, semester, status, startRow, rowPerPage);

	    int totalRow = courseService.getFilteredCourseCount(loginUser.getUserNo(), year, semester, status);
	    int lastPage = (totalRow % rowPerPage == 0) ? (totalRow / rowPerPage) : (totalRow / rowPerPage) + 1;

	    int startPage = ((currentPage - 1) / 10 * 10) + 1;
	    int endPage = startPage + 9;
        if (endPage > lastPage) endPage = lastPage;

	    List<Integer> pages = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) pages.add(i);

	    model.addAttribute("list", courseList);
	    
	    model.addAttribute("year", (year == null) ? "" : year);
	    model.addAttribute("semester", (semester == null) ? "" : semester);
	    model.addAttribute("status", (status == null) ? "" : status);

	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("lastPage", lastPage);
	    model.addAttribute("pages", pages);
	    
	    model.addAttribute("prePage", currentPage - 1);
	    model.addAttribute("nextPage",currentPage + 1);
	    
	    model.addAttribute("showPrev", currentPage > 1);
	    model.addAttribute("showNext", currentPage < lastPage);
	    model.addAttribute("showPagination", lastPage > 1);

	    return "profCourse/courseList";
	}

	// 등록 화면
	@GetMapping("/addCourse")
	public String addCourse(HttpSession session, Model model) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
		    return "redirect:/login";
		}
		
		// 교시 range (1~10교시)
		List<Integer> range = new ArrayList<>();
	    for (int i = 1; i <= 10; i++) {
	        range.add(i);
	    }
	    model.addAttribute("range", range);
	    
	    // 학과 목록
	    List<DeptDTO> deptList = courseMapper.selectDeptList();
	    model.addAttribute("deptList", deptList);
	    
		return "profCourse/addCourse";
	}
	
	// 등록 처리
	@PostMapping("/addCourse")
	public String addCourse(ProfCourseDTO course, 
							@RequestParam("yoil")  List<Integer> yoilList,
						    @RequestParam("start") List<Integer> startList,
						    @RequestParam("end")   List<Integer> endList,
						    HttpSession session,
						    Model model) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
		    return "redirect:/login";
		}
	
		course.setProfessorUserNo(loginUser.getUserNo());
		
		List<ProfCourseTimeDTO> timeList = new ArrayList<>();
	    for (int i = 0; i < yoilList.size(); i++) {
	        ProfCourseTimeDTO t = new ProfCourseTimeDTO();
	        t.setCourseTimeYoil(yoilList.get(i));
	        t.setCourseTimeStart(startList.get(i));
	        t.setCourseTimeEnd(endList.get(i));
	        timeList.add(t);
	    }
		
	    String err = courseService.addCourse(course, timeList);
	    if (err != null) {

	    	 course.setTimeList(timeList); // 시간 복구
	         model.addAttribute("course", course); // 강의 전체 데이터를 넣어줌

	         model.addAttribute("errorMsg", err);
	         model.addAttribute("deptList", courseMapper.selectDeptList());
	         model.addAttribute("range", IntStream.rangeClosed(1, 10).boxed().toList());
	         
	         return "profCourse/addCourse";
	    }
		
		return "redirect:/courseList";
	}
	
	// 수정 화면
	@GetMapping("/modifyCourse")
	public String editCourseForm(Model model,
								@RequestParam("courseNo") int courseNo,
								HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
		
		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
		    return "redirect:/login";
		}
		
		ProfCourseDTO course = courseService.getCourseDetail(courseNo);
	    model.addAttribute("course", course);
	    
	    // 수정 화면에서도 학과/교시 필요하면 여기서도 추가로 넣어줄 수 있음
	    List<DeptDTO> deptList = courseMapper.selectDeptList();
	    model.addAttribute("deptList", deptList);
	    
	    List<Integer> range = new ArrayList<>();
	    for (int i = 1; i <= 10; i++) {
	        range.add(i);
	    }
	    model.addAttribute("range", range);
	    model.addAttribute("range", java.util.Arrays.asList(1,2,3,4,5,6,7,8,9,10));


	    return "profCourse/modifyCourse";
	}
	
	// 수정 처리
	@PostMapping("/modifyCourse")
    public String editCourse(Model model,
				    		ProfCourseDTO course, 
				    		@RequestParam("yoil")  List<Integer> yoil,
				    	    @RequestParam("start") List<Integer> start,
				    	    @RequestParam("end")   List<Integer> end,
				    	    HttpSession session) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
            return "redirect:/login";
        }

        course.setProfessorUserNo(loginUser.getUserNo());
        
        List<ProfCourseTimeDTO> timeList = new ArrayList<>();
        for (int i = 0; i < yoil.size(); i++) {
        	ProfCourseTimeDTO t = new ProfCourseTimeDTO();
        	t.setCourseTimeYoil(yoil.get(i));
        	t.setCourseTimeStart(start.get(i));
        	t.setCourseTimeEnd(end.get(i));
        	timeList.add(t);
        }

        String error = courseService.modifyCourse(course, timeList);

        if (error != null) {
        	course.setTimeList(timeList);
        	
            model.addAttribute("errorMsg", error);
            model.addAttribute("course", course);
            model.addAttribute("deptList", courseMapper.selectDeptList());
            model.addAttribute("range", IntStream.rangeClosed(1, 10).boxed().toList());

            return "profCourse/modifyCourse";
        }

        return "redirect:/courseDashboard?courseNo=" + course.getCourseNo();
    }
	
	// 삭제
	@GetMapping("/removeCourse")
    public String remove(@RequestParam int courseNo, HttpSession session) {
		
		SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");

		if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
		    return "redirect:/login";
		}
		
        courseService.removeCourse(courseNo);
        
        return "redirect:/courseList";
    }
	
	// 대시보드
	@GetMapping("/courseDashboard")
	public String courseDashboard(Model model,
								  @RequestParam("courseNo") int courseNo,
								  HttpSession session) {

	    SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");

	    if (loginUser == null || !loginUser.getUserAuth().startsWith("P_")) {
	        return "redirect:/login";
	    }

	    ProfCourseDTO course = courseService.getCourseDetail(courseNo);
	    model.addAttribute("course", course);
	    
	    System.out.println("course = " + course);
	    
	    return "profCourse/courseDashboard";
	}
}
