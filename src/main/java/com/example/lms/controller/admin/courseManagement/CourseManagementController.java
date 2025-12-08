package com.example.lms.controller.admin.courseManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.lms.controller.admin.userManagement.UserManagementController;
import com.example.lms.dto.CourseManagementDTO;
import com.example.lms.dto.SysAuthDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.admin.CourseManagementService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 2025. 12. 02.
 * Author - JM (Modified)
 * 학점 관리 컨트롤러 (관리자 , 학생전용 )
 */
@Slf4j
@Controller
// @RequestMapping("/admin") 여긴 공통경로 사용 안함..
public class CourseManagementController {
	
	@Autowired
	private CourseManagementService courseManagementService;
	
	// ================================================================================
    // 1. 시스템 사용자 전체 조회 (페이지 진입 시 최초 로딩 + 페이징 ) 접속자가 관리자인경우
    // ================================================================================
    @GetMapping("/admin/courseManagement")
    public String courseManagementByAdmin(Model model,
                                 @RequestParam(defaultValue = "1") Integer page, 
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required = false, defaultValue = "") String searchCourseCondition,
                                 @RequestParam(required = false, defaultValue = "") String selectedCourseYearAndSemester,
                                 HttpSession session) {
    	
    	SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String authCd = sessionUserDto.getAuthCode();
    	String loginUserName = sessionUserDto.getUserName();

        // 학생이면
        if (authCd.equals("S001")) {

            log.info("접속자가 학생이므로 학생 페이지로 이동");
            return "redirect:/myInfo/courseManagement";
        // 관리자이면
        } else if (authCd.equals("A001")) {

            // 페이징 계산
            // page 1 -> startRow 0, page 2 -> startRow 10
            Integer startRow = (page - 1) * limit;

            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("searchCourseCondition", searchCourseCondition);
            searchParams.put("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            searchParams.put("startRow", startRow);
            searchParams.put("limit", limit);

            
            Map<String, Object> courseMap = new HashMap<String, Object>();
            
            // 셀렉박스 연도-학기별 리스트
            List<CourseManagementDTO> selectCourseYearSemesterList = new ArrayList<CourseManagementDTO>();


            // 조회 리스트 == courseList, 페이징 총 개수 == totalPages, 조회 목록 총 갯수 == totalCount
            courseMap = courseManagementService.getCourseManagementListUseAdmin(searchParams);
            log.info("조회된 학점 리스트 Map: {}", courseMap);
            
            List<?> listChk = (List<?>) courseMap.get("searchList");
            log.info("조회된 list 크기: {}", listChk.size()); // list 크기 확인

            // 셀렉박스 연도-학기별 리스트  관리자전용
            selectCourseYearSemesterList = courseManagementService.selectCourseYearAndSemesterListByAdmin();
            log.info(selectCourseYearSemesterList.size() + "  selectCourseYearSemesterList chk");

            List<CourseManagementDTO> courseList =  (List<CourseManagementDTO>) courseMap.get("searchList");
            Integer totalCount = (Integer) courseMap.get("totalCount");
            Integer totalPages = (Integer) courseMap.get("totalPages");

            log.info("pagepagepagepagepage" + page);
            log.info("startRowstartRowstartRowstartRow" + startRow);
            log.info("totalCounttotalCounttotalCount" + totalCount);
            log.info("totalPagestotalPagestotalPages" + totalPages);
            
            model.addAttribute("loginUserName", loginUserName);
            model.addAttribute("courseList", courseList);
            model.addAttribute("selectCourseYearSemesterList", selectCourseYearSemesterList);
            model.addAttribute("currentPage", page);
            model.addAttribute("searchCourseCondition", searchCourseCondition);
            model.addAttribute("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            model.addAttribute("totalCount", totalCount); 
            model.addAttribute("totalPages", totalPages);

            return "/admin/courseManagement";
        } else {

            log.warn("접근 불가 권한 코드: {}", authCd);
            model.addAttribute("errorMessage", "해당 메뉴에 접근할 권한이 없습니다.");
            return "/common/error";
        }
    }

    // ================================================================================
    // 1. 1 시스템 사용자 전체 조회 (페이지 진입 시 최초 로딩 + 페이징 ) 접속자가 학생인경우
    // ================================================================================
    @GetMapping("/myInfo/courseManagement")
    public String courseManagementByStudt (Model model,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required = false, defaultValue = "") String searchCourseCondition,
                                 @RequestParam(required = false, defaultValue = "") String selectedCourseYearAndSemester,
                                 HttpSession session) {

        SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String authCd = sessionUserDto.getAuthCode();
    	Integer userNo = sessionUserDto.getUserNo();
    	
    	String loginUserName = sessionUserDto.getUserName();

    	log.info("userNouserNouserNouserNouserNouserNouserNouserNouserNo1 : " + userNo);
    	
        // 관리자
        if (authCd.equals("A001")) {

            log.info("접속자가 관리자이므로 관리자 페이지로 이동");
            return "redirect:/admin/courseManagement";
            // 학생
        } else if (authCd.equals("S001")) {

            // 페이징 계산
            // page 1 -> startRow 0, page 2 -> startRow 10
            Integer startRow = (page - 1) * limit;

            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("searchCourseCondition", searchCourseCondition);
            searchParams.put("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            searchParams.put("startRow", startRow);
            searchParams.put("limit", limit);
            searchParams.put("userNo", userNo);

            Map<String, Object> courseMap = new HashMap<String, Object>();
            List<CourseManagementDTO> selectCourseYearSemesterList = new ArrayList<CourseManagementDTO>();

            // 조회 리스트 == courseList, 페이징 총 개수 == totalPages, 조회 목록 총 갯수 == totalCount
            courseMap = courseManagementService.getCourseManagementListUseStudt(searchParams);
            log.info("조회된 학점 리스트 학생 Map: {}", courseMap);
            
            List<?> listChk = (List<?>) courseMap.get("searchList");
            log.info("조회된 list 크기 학생 : {}", listChk.size()); // list 크기 확인

            // 셀렉박스 연도-학기별 리스트 학생전용
            selectCourseYearSemesterList = courseManagementService.selectCourseYearAndSemesterListByAStudt(userNo);
            log.info(selectCourseYearSemesterList.size() + "  selectCourseYearSemesterList chk");

            List<CourseManagementDTO> courseList =  (List<CourseManagementDTO>) courseMap.get("searchList");
            Integer totalCount = (Integer) courseMap.get("totalCount");
            Integer totalPages = (Integer) courseMap.get("totalPages");

            model.addAttribute("loginUserName", loginUserName);
            model.addAttribute("courseList", courseList);
            model.addAttribute("selectCourseYearSemesterList", selectCourseYearSemesterList);
            model.addAttribute("currentPage", page);
            model.addAttribute("searchCourseCondition", searchCourseCondition);
            model.addAttribute("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            model.addAttribute("totalCount", totalCount); 
            model.addAttribute("totalPages", totalPages); 

            return "/myInfo/courseManagement";
        } else {

            log.warn("접근 불가 권한 코드: {}", authCd);
            model.addAttribute("errorMessage", "해당 메뉴에 접근할 권한이 없습니다.");
            return "/common/error";
        }
    }
    
    // 	================================================================================
    // 2. 강의 세부 정보 조회 (AJAX 전용)
    // ================================================================================
    @GetMapping("/getCourseDetailListByCourseNo")
    @ResponseBody
    public Map<String, Object> getCourseDetailListByCourseNo(@RequestParam("courseNo") Integer courseNo) {
        
    	Map<String, Object> response = new HashMap<>();

        try {
        	
            CourseManagementDTO courseList = courseManagementService.selectCourseDetail(courseNo); 
            
            response.put("status", "success");
            response.put("courseList", courseList);
        } catch (Exception e) {
        	
            log.error("세부 정보 조회 중 오류 발생", e);
            response.put("status", "error");
            response.put("message", "세부 권한 정보를 불러오는 데 실패했습니다.");
        }
        return response;
    }
    
    // ================================================================================
    // 3. 학점 조건 검색 조회 (AJAX - 리스트 갱신용) // 관리자인 경우
    // ================================================================================
    @GetMapping("/searchCourseInfoByAdmin")
    @ResponseBody
    public Map<String, Object> searchCourseInfoByAdmin(
    		@RequestParam(value = "currentPage", defaultValue = "1") int pageNo,
    		@RequestParam String searchCourseCondition,
            @RequestParam String selectedCourseYearAndSemester,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String authCd = sessionUserDto.getAuthCode();

        // 학생이면
        if (authCd.equals("S001")) {

            response.put("status", "student");
            response.put("message", "학생 권한입니다. 다른 API를 호출하세요.");
            return response;
        } else if (authCd.equals("A001")) {

            // 페이지당 항목 수
            int limit = 10;
            // 시작 행 계산: (현재 페이지 - 1) * 페이지당 개수
            Integer startRow = (pageNo - 1) * limit;

            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("searchCourseCondition", searchCourseCondition);
            searchParams.put("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            searchParams.put("startRow", startRow);
            searchParams.put("limit", limit);

            Map<String, Object> searchMap = new HashMap<String, Object>();
            List<CourseManagementDTO> selectCourseYearSemesterList = new ArrayList<CourseManagementDTO>();

            // 조회 리스트 == searchList, 페이징 총 개수 == totalPages, 조회 목록 총 갯수 == totalCount
            searchMap = courseManagementService.getCourseManagementListUseAdmin(searchParams);
            
            // 셀렉박스 연도-학기별 리스트 ( 연도-학기별 ) 관리자전용
            selectCourseYearSemesterList = courseManagementService.selectCourseYearAndSemesterListByAdmin();
 
            List<CourseManagementDTO> searchList =  (List<CourseManagementDTO>) searchMap.get("searchList");
            Integer totalCount = (Integer) searchMap.get("totalCount");
            Integer totalPages = (Integer) searchMap.get("totalPages");

            log.info("totalCounttotalCounttotalCount" + totalCount);
            log.info("totalPagestotalPagestotalPages" + totalPages);
            
            //  응답에 페이징 정보 포함
            response.put("status", "success");
            response.put("searchList", searchList);
            response.put("selectCourseYearSemesterList", selectCourseYearSemesterList);
            response.put("currentPage", pageNo);
            response.put("totalCount", totalCount);
          	response.put("totalPages", totalPages);

            return response;
        } else {

            response.put("status", "error");
            response.put("message", "권한이 없습니다.");
            return response;
        }
    }

    // ================================================================================
    // 3. 학점 조건 검색 조회 (AJAX - 리스트 갱신용) // 학생인 경우
    // ================================================================================
    @GetMapping("/searchCourseInfoByStudt")
    @ResponseBody
    public Map<String, Object> searchCourseInfoByStudt(
    		@RequestParam(value = "currentPage", defaultValue = "1") int pageNo,
    		@RequestParam String searchCourseCondition,
    		@RequestParam String selectedCourseYearAndSemester,
    		HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String authCd = sessionUserDto.getAuthCode();
    	Integer userNo = sessionUserDto.getUserNo();
    	
    	log.info("userNouserNouserNouserNouserNouserNouserNouserNouserNo : " + userNo);

        // 학생이면
        if (authCd.equals("A001")) {

            response.put("status", "admin");
            response.put("message", "관리자 권한입니다. 다른 API를 호출하세요.");
            return response;
        } else if (authCd.equals("S001")) {

            // 페이지당 항목 수
            int limit = 10;
            // 시작 행 계산: (현재 페이지 - 1) * 페이지당 개수
            Integer startRow = (pageNo - 1) * limit;

            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("searchCourseCondition", searchCourseCondition);
            searchParams.put("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
            searchParams.put("startRow", startRow);
            searchParams.put("limit", limit);
            searchParams.put("userNo", userNo);

            
            Map<String, Object> searchMap = new HashMap<String, Object>();
            List<CourseManagementDTO> selectCourseYearSemesterList = new ArrayList<CourseManagementDTO>();

            // 조회 리스트 == searchList, 페이징 총 개수 == totalPages, 조회 목록 총 갯수 == totalCount
            searchMap = courseManagementService.getCourseManagementListUseStudt(searchParams);
            
            // 셀렉박스 연도-학기별 리스트 ( 연도-학기별 ) 학생전용
            selectCourseYearSemesterList = courseManagementService.selectCourseYearAndSemesterListByAStudt(userNo);
            
            List<CourseManagementDTO> searchList =  (List<CourseManagementDTO>) searchMap.get("searchList");
            Integer totalCount = (Integer) searchMap.get("totalCount");
            Integer totalPages = (Integer) searchMap.get("totalPages");

            //  응답에 페이징 정보 포함
            response.put("status", "success");
            response.put("searchList", searchList);
            response.put("selectCourseYearSemesterList", selectCourseYearSemesterList);
            response.put("currentPage", pageNo);
            response.put("totalCount", totalCount);
            response.put("totalPages", totalPages);

            return response;
        } else {

            response.put("status", "error");
            response.put("message", "권한이 없습니다.");
            return response;
        }
    }
}
