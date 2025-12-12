package com.example.lms.controller.admin.universityNoticeManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.lms.dto.SysUserDTO;
import com.example.lms.dto.UniversityNoticeDTO;
import com.example.lms.service.admin.UniversityNoticeService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin") // 공통 경로 매핑
public class UniversityNoticeManagementController {
	
	@Autowired
	private UniversityNoticeService universityNoticeService;

	// ================================================================================
	// 1. 학교 공지사항 관리 조회 (페이지 진입 시 최초 로딩 + 페이징)
	// ================================================================================
	@GetMapping("/universityNoticeManagement") // HTML/Mustache 파일을 위한 기본 경로
	public String universityNoticeManagement(Model model,
								 @RequestParam(defaultValue = "1") Integer page,
								 @RequestParam(defaultValue = "10") Integer limit,
								 HttpSession session) {
		
		SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String loginUserName = sessionUserDto.getUserName();

		Integer startRow = (page - 1) * limit;

		// 1. 학교 전체 리스트 조회
		List<UniversityNoticeDTO> universityNoticeList = universityNoticeService.universityNoticeList(startRow, limit);
		
		universityNoticeService.assignPriorityColorClassForMustache(universityNoticeList);

		// 2. 전체 건수 조회 ( 페이징 처리 )
		Integer totalCount = universityNoticeService.universityNoticeListCnt();

		// 3. 총 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		log.info("totalCount : " + totalCount);

		// 학교 공지사항 입력 시 우선순위 셀렉박스 리스트
		List<UniversityNoticeDTO> universityNoticePriorityList = universityNoticeService.selectUniversityNoticePriorityList();

		// ⭐️ 누락된 검색 조건을 Model에 추가 (빈 문자열로 초기화) ⭐️
		model.addAttribute("loginUserName", loginUserName);
		model.addAttribute("searchUniversityNoticeCondition", "");
		model.addAttribute("universityNoticeList", universityNoticeList); // ⭐️ 변수명을 mustache와 일치시킴 ⭐️
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("universityNoticePriorityList", universityNoticePriorityList); // 여기서 담나?

		return "/admin/universityNoticeManagement"; // mustache 파일 경로
	}

	// ================================================================================
	// 2. 학교 공지사항 등록 (AJAX)
	// ================================================================================
	@PostMapping("/insertUniversityNotice")
	@ResponseBody
	public Map<String, Object> insertUniversityNotice(@RequestBody UniversityNoticeDTO insertUniversityNoticeDTO, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> userValidateParams = new HashMap<>();

		SysUserDTO loginUserSession = ( SysUserDTO ) session.getAttribute("loginUser");

		// ️ 세션 NULL 체크 추가
		if (loginUserSession == null) {

			response.put("status", "fail");
			response.put("message", "세션이 만료되었거나 로그인 정보가 없습니다.");
			return response;
		}

		Integer userNo = loginUserSession.getUserNo();
		String userId = loginUserSession.getUserId();

		userValidateParams.put("userNo", userNo);
		userValidateParams.put("userId", userId);

		try {

			if ( universityNoticeService.insertUpdateRemoveUniversityNoticeValidate(userValidateParams) < 1) {

				// 관리자가 아니므로 학교 공지사항 등록 불가
				response.put("status", "fail");
				response.put("message", "관리자가 아니므로 학교 공지사항을 등록할 수 없습니다.");
			} else {

				// 로그인 사용자 = 게시글 작성 사용자
				insertUniversityNoticeDTO.setUniversityWriterUserNo(userNo);

				int insertUniversityNoticeResult = universityNoticeService.insertUniversityNotice(insertUniversityNoticeDTO);

				if ( insertUniversityNoticeResult == 1) {

					log.info("UniversityNoticeManagementController$$insertUniversityNotice 학교 공지사항 등록 성공");
					response.put("status", "success");
					response.put("message", "학교 공지사항 등록에 성공하였습니다.");
				}
			}
		} catch (Exception e) {

			log.error("학교 공지사항 등록 중 오류 발생", e);
			response.put("status", "error");
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
		}

		return response;
	}

	// ================================================================================
	// 3. 학교 공지사항 상세 정보 조회 (AJAX)
	// ================================================================================
	@GetMapping("/getUniversityNoticeDetail") // ⭐️ URL 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> getUniversityNoticeDetail(@RequestParam Integer universityNoticeNo) {

		Map<String, Object> response = new HashMap<>();

		// 단일 결과를 조회하는 것으로 가정하고 List 대신 단일 DTO로 받습니다.
		UniversityNoticeDTO universityNoticeDetail = universityNoticeService.selectUniversityNoticeDetail(universityNoticeNo);

		if (universityNoticeDetail != null) {

			response.put("status", "success");
			response.put("data", universityNoticeDetail);
		} else {

			response.put("status", "fail");
			response.put("message", "해당 학교 공지사항 번호를 찾을 수 없습니다.");
		}

		return response;
	}

	// ================================================================================
	// 4. 학교 공지사항 상세 정보 수정 (AJAX)
	// ================================================================================
	@PostMapping("/updateUniversityNotice") // ⭐️ GET -> POST 변경 (수정 작업은 POST/PUT 사용) ⭐️
	@ResponseBody
	public Map<String, Object> updateUniversityNotice(@RequestBody UniversityNoticeDTO universityNoticeDTO, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> userValidateParams = new HashMap<>();

		SysUserDTO loginUserSession = ( SysUserDTO ) session.getAttribute("loginUser");
		Integer userNo = loginUserSession.getUserNo();
		String userId = loginUserSession.getUserId();
		Integer universityNo = universityNoticeDTO.getUniversityNoticeNo();

		userValidateParams.put("userNo", userNo);
		userValidateParams.put("userId", userId);
		userValidateParams.put("universityNo", universityNo);

		try {

			// 1. 직접 작성한 사용자 여부 검증
			boolean isUniversityNoticeWriter = universityNoticeService.updateRemoveUniversityNoticeValidate(userValidateParams) > 0;
			
			// 2. 관리자 여부 검증
			// 관리자 검증 파라미터에선 universityNo 불필요
			userValidateParams.remove("universityNo");
			boolean isAdmin = universityNoticeService.insertUpdateRemoveUniversityNoticeValidate(userValidateParams) > 0;

			// 3. 최종 권한 확인
			boolean hasPermission = isUniversityNoticeWriter || isAdmin;

			if (hasPermission) {

				if ( universityNoticeService.updateUniversityNotice(universityNoticeDTO) > 0 ) {

					log.info("UniversityNoticeManagementController$$updateUniversityNotice 업데이트 성공 (권한: {})", isUniversityNoticeWriter ? "작성자" : "관리자");
					response.put("status", "success");
					response.put("message", "학교 공지사항 수정에 성공하였습니다.");
				} else {

					log.info("UniversityNoticeManagementController$$updateUniversityNotice 업데이트 실패");
					response.put("status", "fail");
					response.put("message", "학교 공지사항 수정에 실패하였습니다.");
				}
			} else {
				log.info("UniversityNoticeManagementController$$updateUniversityNotice 권한 없음");
				response.put("status", "fail");
				response.put("message", "수정 권한이 없습니다. (작성자 또는 관리자만 수정 가능)");
			}
		} catch (Exception e) {
			log.error("학교 공지사항 수정 중 오류 발생", e);
			response.put("status", "error");
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
			throw e; // 트랜잭션을 롤백합니다.
		}

		return response;
	}

	// ================================================================================
	// 5. 학교 공지사항 검색 조회 (AJAX - 리스트 갱신용)
	// ================================================================================
	@GetMapping("/searchUniversityNotice") // ⭐️ URL 충돌 방지 및 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> searchUniversityNotice(@RequestParam String searchUniversityNoticeCondition,
										  @RequestParam(value = "currentPage", defaultValue = "1") int pageNo) {

		Map<String, Object> response = new HashMap<>();

		int limit = 10;
		int startRow = (pageNo - 1) * limit;

		// MyBatis는 여러 파라미터를 받을 때 Map이나 DTO에 담아서 전달해야 합니다.
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("searchUniversityNoticeCondition", searchUniversityNoticeCondition);
		searchParams.put("startRow", startRow);
		searchParams.put("limit", limit);

		List<UniversityNoticeDTO> searchUniversityNoticeInfoList = universityNoticeService.searchUniversityNoticeInfoList(searchParams);

		int totalCount = universityNoticeService.searchUniversityNoticeInfoListCnt(searchUniversityNoticeCondition);

		response.put("status", "success");
		response.put("searchUniversityNoticeInfoList", searchUniversityNoticeInfoList);
		response.put("totalCount", totalCount);
		response.put("currentPage", pageNo);

		return response;
	}

	// ================================================================================
	// 6. 다수 학교 공지사항 삭제 처리 (AJAX)
	// ================================================================================
	@PostMapping("/removeUniversityNotice")
	@ResponseBody
	public Map<String, Object> removeUniversityNotice(@RequestBody Map<String, List<Integer>> requestBody, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
		List<Integer> universityNoticeNoList = requestBody.get("universityNoticeNoList");
		int successCount = 0;
		int failCount = 0;
		List<Integer> noAuthList = new ArrayList<>();

		if (universityNoticeNoList == null || universityNoticeNoList.isEmpty()) {

			response.put("status", "fail");
			response.put("message", "삭제할 학교 공지사항 번호가 지정되지 않았습니다.");
			return response;
		}

		// ==========================================================
		// 학교 공지사항 같은 경우 등록사용자 여부 확인을 위해서 세션 처리 추가
		Map<String, Object> userValidateParams = new HashMap<>();
		SysUserDTO loginUserSession = ( SysUserDTO ) session.getAttribute("loginUser");
		if (loginUserSession == null) {
			response.put("status", "fail");
			response.put("message", "세션이 만료되었거나 로그인 정보가 없습니다.");
			return response;
		}
		Integer userNo = loginUserSession.getUserNo();
		String userId = loginUserSession.getUserId();

		userValidateParams.put("userNo", userNo);
		userValidateParams.put("userId", userId);


		// 관리자 여부를 미리 확인
		boolean isAdmin = universityNoticeService.insertUpdateRemoveUniversityNoticeValidate(userValidateParams) > 0;
		// ==========================================================


		try {

			for (Integer universityNoticeNo : universityNoticeNoList) {

				// 1. 해당 공지사항이 존재하는지 확인 (필수 검증)
				if (universityNoticeService.insertUpdRemvUniversityNoticeNoValidate(universityNoticeNo) < 1) {

					log.warn("해당 학교 공지사항 번호를 찾을 수 없어 삭제 실패. No: {}", universityNoticeNo);
					failCount++;
					continue;
				}


				// 2. 권한 검증: 관리자이거나 (OR) 직접 작성한 사용자여야 함
				boolean hasAuthority = isAdmin;

				if (!hasAuthority) {
					// 관리자가 아닌 경우, 작성자 여부를 확인
					userValidateParams.put("universityNo", universityNoticeNo); // ⭐️ Map에 공지 번호 추가
					hasAuthority = universityNoticeService.updateRemoveUniversityNoticeValidate(userValidateParams) > 0;
					userValidateParams.remove("universityNo"); // ⭐️ 다음 반복을 위해 공지 번호 제거
				}

				if (!hasAuthority) {
					log.warn("공지사항 {}는 삭제할 권한이 없어 삭제에 실패했습니다.", universityNoticeNo);
					noAuthList.add(universityNoticeNo);
					failCount++;
					continue;
				}

				// 3. 삭제 처리
				int removeUniversityNoticeResult = universityNoticeService.removeUniversityNotice(universityNoticeNo);

				if (removeUniversityNoticeResult == 1) {
					successCount++;
					log.info("UniversityNoticeManagementController$$removeUniversityNotice : 삭제 성공 - No: {}", universityNoticeNo);
				} else {
					log.error("학교 공지사항 삭제 중 DB 오류 발생. No: {}", universityNoticeNo);
					failCount++;
				}
			}

			// ⭐️ 최종 응답 메시지 구성 ⭐️
			String message = "";

			if (successCount > 0) {
				message += successCount + "개의 학교 공지사항이 성공적으로 삭제되었습니다.";
			}

			if (failCount > 0) {
				if (!message.isEmpty()) {
					message += " ";
				}
				message += failCount + "개의 학교 공지사항은 삭제에 실패했습니다. (사유: ";

				if (!noAuthList.isEmpty()) {
					message += "삭제 권한이 없는 공지사항: " + noAuthList + ")";
				} else {
					message += "기타 오류";
				}
				message += ")";
			}

			if (successCount > 0) {
				response.put("status", "success");
				response.put("message", message);
			} else if (failCount > 0) {
				response.put("status", "fail");
				response.put("message", message);
			} else {
				response.put("status", "fail");
				response.put("message", "삭제를 시도한 학교 공지사항이 없거나 처리된 항목이 없습니다.");
			}

		} catch (Exception e) {
			
			log.error("학교 공지사항 삭제 중 서버 오류 발생", e);
			response.put("status", "error");
			response.put("message", "서버 오류 발생: " + e.getMessage());
		}

		return response;
	}
}
