package com.example.lms.controller.main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.lms.dto.MainDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.dto.UniversityNoticeDTO;
import com.example.lms.service.admin.UniversityNoticeService;
import com.example.lms.service.certifipdf.CertificatePdfService;
import com.example.lms.service.common.authorization.CertificateOfGraduationValidateService;
import com.example.lms.service.common.authorization.ViewAuthorizationValidateService;
import com.example.lms.service.main.MainService;
import com.lowagie.text.DocumentException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 2025. 12. 04.
 * Author - jm
 * 메인페이지 컨트롤러
 */
@Slf4j
@Controller
public class MainController {
	
		@Autowired
		private MainService mainService;
		
		@Autowired
		private ViewAuthorizationValidateService viewAuthorizationValidateService;
		
		@Autowired
		private UniversityNoticeService universityNoticeService;
		
		@Autowired
		private CertificateOfGraduationValidateService certificateOfGraduationValidateService;
		
		@Autowired
		private CertificatePdfService certificatePdfService;
		
	
		// 메인 페이지 GET
		@GetMapping("/main")
		public String main(HttpSession session, Model model) {
			
			// 세션불러오기
			SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");
			
			Integer userNo = loginSysUserDTO.getUserNo();
			String authCode = loginSysUserDTO.getAuthCode();
			
			// 메뉴 권한 필터 처리
			
			Map<String, Boolean> menuValidateRsltMap = viewAuthorizationValidateService.MenuAuthorizationValidate(authCode);
			
			Boolean useAdmin = menuValidateRsltMap.get("useAdmin");
			Boolean useStudent = menuValidateRsltMap.get("useStudent"); 
			Boolean useProfessor = menuValidateRsltMap.get("useProfessor");
			Boolean useStaff = menuValidateRsltMap.get("useStaff");
			
			// 메인페이지 사용자 프로파일 카드
			MainDTO userProfileCard = new MainDTO();
			userProfileCard = mainService.userProfileCard(userNo);
			
			// 메인 페이지 학교 공지사항
			List<MainDTO> mainUniversityNoticeList = new ArrayList<MainDTO>();
			mainUniversityNoticeList = mainService.mainUniversityNoticeList();
			
			for (MainDTO notice : mainUniversityNoticeList) {
			    Integer code = notice.getUniversityNoticePriorityCode();
			    // Mustache 템플릿에서 조건부 렌더링을 위해 Boolean 값 추가
			    notice.setIs_10(Integer.valueOf(10).equals(code)); //고정공지
			    notice.setIs_20(Integer.valueOf(20).equals(code)); //우선공지
			    notice.setIs_30(Integer.valueOf(30).equals(code)); //일반공지
			}
			
			
			// 메인 페이지 시간표
			MainDTO mainCourseTimeParams = new MainDTO();
			mainCourseTimeParams.setUserNo(userNo);
			mainCourseTimeParams.setAuthCode(authCode);
			// 오늘 요일 숫자 가져오기 (월=1, 화=2, ..., 금=5)
     		// 일요일=0 → 토요일=6
			int todayYoil = LocalDate.now().getDayOfWeek().getValue() - 1;
			if (todayYoil >= 1 && todayYoil <= 5) {  // 주중만
			    mainCourseTimeParams.setCourseTimeYoil(todayYoil);
			}
			
			List<MainDTO> mainCourseTimeTable = new ArrayList<MainDTO>();
			mainCourseTimeTable = mainService.getMainCourseTimeTable(mainCourseTimeParams);
			boolean hasSchedule = !mainCourseTimeTable.isEmpty();
			
			
			// 메뉴권한 필터처리
			model.addAttribute("useAdmin", useAdmin);
			model.addAttribute("useStudent", useStudent);
			model.addAttribute("useProfessor", useProfessor);
			model.addAttribute("useStaff", useStaff);
			// 메인페이지 사용자 프로파일 카드
			model.addAttribute("userProfileCard", userProfileCard);
			// 메인 페이지 학교 공지사항
			model.addAttribute("mainUniversityNoticeList", mainUniversityNoticeList);
			// 메인 페이지 시간표
			model.addAttribute("mainCourseTimeTable", mainCourseTimeTable);
			// 메인 페이지 뷰에서 만약 리스트 빈값 이면 일정없습니다. 로 띄운다.
			model.addAttribute("hasSchedule", hasSchedule);
	
		    return "/main/main"; 
		}
		
		///////////////////////////////////////////////////////////////////////////////////
		
		// ================================================================================
	    // 학교 공지사항 목록 조회 (모달 진입 시 AJAX 호출)
	    // ================================================================================
	    /**
	     * 공지사항 목록 모달에 표시할 데이터를 조회합니다. (AJAX)
	     */
	    @GetMapping("/getMainUniversityNoticeList") // AJAX 호출용 URL
	    @ResponseBody // JSON 데이터를 반환
	    public Map<String, Object> getMainUniversityNoticeList(
	            @RequestParam(defaultValue = "1") Integer page,
	            @RequestParam(defaultValue = "10") Integer limit) {
	        	
	        Map<String, Object> response = new HashMap<>();

	        Integer startRow = (page - 1) * limit;

	        // 1. 학교 전체 리스트 조회
	        List<UniversityNoticeDTO> universityNoticeList = universityNoticeService.universityNoticeList(startRow, limit);
	        
	        // 2. 전체 건수 조회
	        Integer totalCount = universityNoticeService.universityNoticeListCnt();

	        // 3. 총 페이지 수 계산
	        int totalPages = (int) Math.ceil((double) totalCount / limit);

	        // 4. 우선순위 Boolean 값 설정 (Mustache 조건부 렌더링을 위해)
	        for (UniversityNoticeDTO notice : universityNoticeList) {
	            Integer code = notice.getUniversityNoticePriorityCode();
	            notice.setIs_10(Integer.valueOf(10).equals(code)); //고정공지
	            notice.setIs_20(Integer.valueOf(20).equals(code)); //우선공지
	            notice.setIs_30(Integer.valueOf(30).equals(code)); //일반공지
	        }
	        
	        response.put("status", "success");
	        response.put("universityNoticeList", universityNoticeList);
	        response.put("currentPage", page);
	        response.put("totalPages", totalPages);
	        response.put("totalCount", totalCount);
	        response.put("searchUniversityNoticeCondition", "");
	        
	        return response;
	    }

	    // ================================================================================
	    // 학교 공지사항 상세 정보 조회 (AJAX)
	    // ================================================================================
	    /**
	     * 특정 공지사항의 상세 정보를 조회합니다. (AJAX)
	     */
	    @GetMapping("/getMainUniversityNoticeDetail")
	    @ResponseBody
	    public Map<String, Object> getUniversityNoticeDetail(@RequestParam Integer universityNoticeNo) {

	        Map<String, Object> response = new HashMap<>();

	        // 1. 상세 정보를 조회 (Service에서 조회수 증가 처리 권장)
	        UniversityNoticeDTO universityNoticeDetail = universityNoticeService.selectUniversityNoticeDetail(universityNoticeNo);

	        if (universityNoticeDetail != null) {
	        	
	        	// 조회수 증가처리
	        	mainService.increaseUniversityNoticeViewCnt(universityNoticeNo);

	            response.put("status", "success");
	            response.put("data", universityNoticeDetail);
	        } else {

	            response.put("status", "fail");
	            response.put("message", "해당 학교 공지사항 번호를 찾을 수 없습니다.");
	        }

	        return response;
	    }
	    
	    // ================================================================================
	    // 학교 공지사항 검색 조회 (AJAX - 리스트 갱신용)
	    // ================================================================================
	    /**
	     * 검색 조건에 따라 공지사항 리스트를 조회하고 페이징 정보를 반환합니다. (AJAX)
	     */
	    @GetMapping("/searchMainUniversityNotice")
	    @ResponseBody
	    public Map<String, Object> searchUniversityNotice(@RequestParam String searchUniversityNoticeCondition,
	                                          @RequestParam(value = "currentPage", defaultValue = "1") int pageNo) {

	        Map<String, Object> response = new HashMap<>();

	        int limit = 10;
	        int startRow = (pageNo - 1) * limit;

	        // 1. MyBatis 전달을 위한 파라미터 Map 설정
	        Map<String, Object> searchParams = new HashMap<>();
	        searchParams.put("searchUniversityNoticeCondition", searchUniversityNoticeCondition);
	        searchParams.put("startRow", startRow);
	        searchParams.put("limit", limit);

	        // 2. 검색 리스트 및 전체 카운트 조회
	        List<UniversityNoticeDTO> searchUniversityNoticeInfoList = universityNoticeService.searchUniversityNoticeInfoList(searchParams);
	        int totalCount = universityNoticeService.searchUniversityNoticeInfoListCnt(searchUniversityNoticeCondition);
	        int totalPages = (int) Math.ceil((double) totalCount / limit);

	        // 3. 우선순위 Boolean 값 설정
	        for (UniversityNoticeDTO notice : searchUniversityNoticeInfoList) {
	            Integer code = notice.getUniversityNoticePriorityCode();
	            notice.setIs_10(Integer.valueOf(10).equals(code)); //고정공지
	            notice.setIs_20(Integer.valueOf(20).equals(code)); //우선공지
	            notice.setIs_30(Integer.valueOf(30).equals(code)); //일반공지
	        }

	        // 4. 응답 Map에 데이터 추가
	        response.put("status", "success");
	        response.put("universityNoticeList", searchUniversityNoticeInfoList); // 변수명 통일 (프론트엔드 일관성)
	        response.put("totalCount", totalCount);
	        response.put("totalPages", totalPages); 
	        response.put("currentPage", pageNo);
	        response.put("searchUniversityNoticeCondition", searchUniversityNoticeCondition);

	        return response;
	    }
	    
	    /**
	     * 클라이언트(FullCalendar)에서 선택한 날짜에 해당하는 시간표 데이터를 JSON으로 반환합니다.
	     * 엔드포인트: /api/schedule?date=YYYY-MM-DD
	     */
	    @GetMapping("/api/schedule") // JavaScript의 fetch(ajaxUrl) 경로와 일치해야 합니다.
	    @ResponseBody
	    public Map<String, Object> getDynamicMainCourseTime(
	            @RequestParam("date") String dateString, // 클라이언트가 보낸 'YYYY-MM-DD' 날짜 문자열
	            HttpSession session
	    ) {
	        Map<String, Object> resultMap = new HashMap<>();

	        try {
	            // 1. 세션에서 사용자 정보 불러오기
	            SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");

	            Integer userNo = loginSysUserDTO.getUserNo();
	            String authCode = loginSysUserDTO.getAuthCode();

	            // 2. 전달된 날짜 문자열을 LocalDate로 변환하고 요일(DayOfWeek) 계산
	            LocalDate selectedDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
	            DayOfWeek dayOfWeek = selectedDate.getDayOfWeek();
	            
	            // 3. 시간표 조회를 위한 요일 번호 설정 (월=1, 화=2, ..., 금=5)
	            Integer courseTimeYoil = null; 
	            
	            // DayOfWeek.getValue()는 월요일(1)부터 일요일(7)까지의 값을 반환합니다.
	            // 금요일(5)까지만 시간표 조회가 필요하다고 가정합니다.
	            if (dayOfWeek.getValue() >= DayOfWeek.MONDAY.getValue() && 
	                dayOfWeek.getValue() <= DayOfWeek.FRIDAY.getValue()) {
	                
	                // 월(1) ~ 금(5) 값 그대로 사용
	                courseTimeYoil = dayOfWeek.getValue(); 
	            }
	            
	            // 4. 시간표 조회
	            List<MainDTO> mainCourseTimeTable = new ArrayList<>();
	            boolean hasSchedule = false;

	            // 주말이 아닌 경우에만 시간표를 조회합니다.
	            if (courseTimeYoil != null) {
	            	
	                MainDTO mainCourseTimeParams = new MainDTO();
	                mainCourseTimeParams.setUserNo(userNo);
	                mainCourseTimeParams.setAuthCode(authCode);
	                mainCourseTimeParams.setCourseTimeYoil(courseTimeYoil); 
	                
	                // 메인 서비스 호출
	                mainCourseTimeTable = mainService.getMainCourseTimeTable(mainCourseTimeParams);
	                hasSchedule = !mainCourseTimeTable.isEmpty();
	            }

	            // 5. 결과 Map에 담아 반환
	            resultMap.put("date", dateString);
	            resultMap.put("hasSchedule", hasSchedule);
	            resultMap.put("mainCourseTimeTable", mainCourseTimeTable);
	            
	        } catch (Exception e) {
	            // 날짜 파싱 오류, DB 오류 등 예외 처리
	            System.err.println("시간표 조회 중 오류 발생: " + e.getMessage());
	            resultMap.put("hasSchedule", false);
	            resultMap.put("errorMessage", "서버 처리 중 오류가 발생했습니다.");
	        }

	        return resultMap;
	    }
	    
	    
	    /**
		 * 졸업증명서 사용자 항목 (일반 뷰 페이지)
		 */
		@GetMapping("/main/certificateOfGraduation")
		public String certificateOfGraduation(HttpSession session, Model model) {

			// 세션불러오기
			SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");

			Integer userNo = loginSysUserDTO.getUserNo();
			String authCode = loginSysUserDTO.getAuthCode();
			String authDetailName = loginSysUserDTO.getAuthDetailName();
			Integer userGrade = loginSysUserDTO.getUserGrade(); // 새로 추가된 항목

			MainDTO paramMainDto = new MainDTO();
			paramMainDto.setUserNo(userNo);
			paramMainDto.setAuthCode(authCode);
			paramMainDto.setAuthDetailName(authDetailName);
			paramMainDto.setUserGrade(userGrade); // 새로 추가된 항목 반영

			List<MainDTO> certificateOfGraduationList = new ArrayList<>();

			boolean certificateValidate = certificateOfGraduationValidateService.certificateOfGraduationValidate(paramMainDto);

			if (certificateValidate) {
				certificateOfGraduationList = mainService.certificateOfGraduationList(userNo);
			}

			model.addAttribute("certificateOfGraduationList", certificateOfGraduationList);
			// false면 다운 불가하도록
			model.addAttribute("certificateValidate", certificateValidate);

			return "/certificate/certificateOfGraduation";
		}

		/**
	     * 졸업증명서 PDF 발급
	     */
		@GetMapping("/main/certificateOfGraduation/pdf")
		public void certificateOfGraduationPdf(
	            HttpSession session, 
	            HttpServletResponse response
	    ) throws IOException, DocumentException { // DocumentException은 PDF 라이브러리 예외

			SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");

			Integer userNo = loginSysUserDTO.getUserNo();
			String authCode = loginSysUserDTO.getAuthCode();
			String authDetailName = loginSysUserDTO.getAuthDetailName();
			Integer userGrade = loginSysUserDTO.getUserGrade(); // 새로 추가된 항목

			MainDTO paramMainDto = new MainDTO();
			paramMainDto.setUserNo(userNo);
			paramMainDto.setAuthCode(authCode);
			paramMainDto.setAuthDetailName(authDetailName);
			paramMainDto.setUserGrade(userGrade); // 새로 추가된 항목 반영

			boolean certificateValidate = certificateOfGraduationValidateService.certificateOfGraduationValidate(paramMainDto);

			if (!certificateValidate) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "발급 권한이 없습니다.");
				return;
			}

			// 1) 원본 데이터 조회 (Date/Timestamp 포함)
			List<MainDTO> certificateOfGraduationList = mainService.certificateOfGraduationList(userNo);
            
            // ⭐️⭐️⭐️ 2) 서비스 메서드를 호출하여 날짜 포맷팅을 수행 ⭐️⭐️⭐️
            // Date 객체를 'YYYY-MM-DD' 형식의 문자열로 변환하여 Map 리스트를 얻습니다.
            List<Map<String, Object>> formattedDataList = 
                certificatePdfService.formatCertificateDates(certificateOfGraduationList);

			// 3) Mustache로 HTML 문자열 렌더링
			// ⭐️ 가공된 리스트(formattedDataList)를 서비스로 전달
			String html = certificatePdfService.renderCertificateHtml(formattedDataList, certificateValidate);

			// 4) HTML -> PDF 변환
	        // DocumentException이 여기서 발생할 수 있습니다.
			byte[] pdfBytes = certificatePdfService.generatePdfFromHtml(html); 

			// 5) HTTP 응답으로 내려주기
	        // 파일명 인코딩 (한글 파일명 깨짐 방지)
	        String filename = "졸업증명서.pdf";
	        // URL 인코딩은 서비스 레이어가 아닌 컨트롤러에서 처리하는 것이 일반적입니다.
	        String encodedFilename = java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

			response.setContentType("application/pdf");
	        // Content-Disposition 헤더에 파일명 인코딩을 적용합니다.
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", encodedFilename, encodedFilename));
			response.setContentLength(pdfBytes.length);

			ServletOutputStream os = response.getOutputStream();
			os.write(pdfBytes);
			os.flush();
		}

}
