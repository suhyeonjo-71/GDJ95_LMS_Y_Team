package com.example.lms.controller.admin.deptManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.admin.DeptService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin") // 공통 경로 매핑
public class DeptManagementController {
	
	@Autowired
	private DeptService deptService;
	
	// ================================================================================
	// 1. 학과 관리 조회 (페이지 진입 시 최초 로딩 + 페이징)
	// ================================================================================
	@GetMapping("/deptManagement") // HTML/Mustache 파일을 위한 기본 경로
	public String deptManagement(Model model,
			@RequestParam(defaultValue = "1") Integer page,	
			@RequestParam(defaultValue = "10") Integer limit,
			HttpSession session) {
		
		SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String loginUserName = sessionUserDto.getUserName();
		
		Integer startRow = (page - 1) * limit;
		
		// 1. 학과 전체 리스트 조회
		List<DeptDTO> deptList = deptService.deptList(startRow, limit);
		
		// 2. 전체 건수 조회 ( 페이징 처리 )
		Integer totalCount = deptService.deptListCnt();
		
		// 3. 총 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);
		
		log.info("totalCount : " + totalCount);
		
		// ⭐️ 누락된 검색 조건을 Model에 추가 (빈 문자열로 초기화) ⭐️
		model.addAttribute("loginUserName", loginUserName);
	    model.addAttribute("searchDeptCondition", "");
		model.addAttribute("deptList", deptList); // ⭐️ 변수명을 mustache와 일치시킴 ⭐️
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		
		return "/admin/deptManagement"; // mustache 파일 경로
	}
	
	// ================================================================================
	// 2. 학과 등록 (AJAX)
	// ================================================================================
	@PostMapping("/insertDept")
	@Transactional
	@ResponseBody
	public Map<String, Object> insertDept(@RequestBody DeptDTO insertDeptDTO) {
		
		Map<String, Object> response = new HashMap<>();
		String deptCode = insertDeptDTO.getDeptCode();
		
		try {
			
			if ( deptService.insertUpdRemvDeptValidate(deptCode) > 0 ) {
				
				// 존재하고 있는 코드로 등록 불가
				response.put("status", "fail");
				response.put("message", "이미 존재하고 있는 학과코드 입니다.");
			} else {
				
				int insertDeptResult = deptService.insertDept(insertDeptDTO);
				
				if ( insertDeptResult == 1) {
					
					log.info("DeptManagementController$$insertDept 학과 등록 성공");
					response.put("status", "success");
					response.put("message", "학과 코드 등록에 성공하였습니다.");
				}
			}
		} catch (Exception e) {
			
			log.error("학과 등록 중 오류 발생", e);
			response.put("status", "error");
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
		}
		
		return response;
	}
	
	// ================================================================================
	// 3. 학과 코드 상세 정보 조회 (AJAX)
	// ================================================================================
	@GetMapping("/getDeptDetail") // ⭐️ URL 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> getDeptDetail(@RequestParam String deptCode) {
		
		Map<String, Object> response = new HashMap<>();
		
		// 단일 결과를 조회하는 것으로 가정하고 List 대신 단일 DTO로 받습니다.
		DeptDTO deptDetail = deptService.selectDeptDetail(deptCode);
		
		if (deptDetail != null) {
			
			response.put("status", "success");
			response.put("data", deptDetail);
		} else {
			
			response.put("status", "fail");
			response.put("message", "해당 학과 코드를 찾을 수 없습니다.");
		}
		
		return response;
	}
	
	// ================================================================================
	// 4. 학과 코드 상세 정보 수정 (AJAX)
	// ================================================================================
	@PostMapping("/updateDept") // ⭐️ GET -> POST 변경 (수정 작업은 POST/PUT 사용) ⭐️
	@Transactional
	@ResponseBody
	public Map<String, Object> updateDept(@RequestBody DeptDTO updateDeptDTO) {
		
		Map<String, Object> response = new HashMap<>();
		String deptCode = updateDeptDTO.getDeptCode();
		
		try {
			
			int updateRemoveDeptValidateResult = deptService.updateRemoveDeptValidate(deptCode);
			
			if ( updateRemoveDeptValidateResult == 0 ) {
				
				log.info("DeptManagementController$$updateRemoveDeptValidate 검증 성공");
				
				int updateDeptResult = deptService.updateDept(updateDeptDTO);
				
				if( updateDeptResult == 1 ) {
					
					log.info("DeptManagementController$$updateDept 업데이트 성공");
					response.put("status", "success");
					response.put("message", "학과 코드 수정에 성공하였습니다.");
				} else {
					
					response.put("status", "fail");
					response.put("message", "학과 코드 수정에 실패하였습니다.");
				}
				
			} else {
				
				log.info("DeptManagementController$$updateRemoveDeptValidate 검증 실패");
			}
			
		} catch (Exception e) {
			log.error("학과 수정 중 오류 발생", e);
			response.put("status", "error");
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
			throw e; // 트랜잭션을 롤백합니다.
		}
		
		return response;
	}
	
	// ================================================================================
	// 5. 학과 코드 검색 조회 (AJAX - 리스트 갱신용)
	// ================================================================================
	@GetMapping("/searchDept") // ⭐️ URL 충돌 방지 및 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> searchDept(@RequestParam String searchDeptCondition,
			@RequestParam(value = "currentPage", defaultValue = "1") int pageNo) {
		
		Map<String, Object> response = new HashMap<>();
		
		int limit = 10;
		int startRow = (pageNo - 1) * limit;
		
		// MyBatis는 여러 파라미터를 받을 때 Map이나 DTO에 담아서 전달해야 합니다.
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("searchDeptCondition", searchDeptCondition);
		searchParams.put("startRow", startRow);
		searchParams.put("limit", limit);
		
		List<DeptDTO> searchDeptInfoList = deptService.searchDeptInfoList(searchParams);
		
		int totalCount = deptService.searchDeptInfoListCnt(searchDeptCondition);
		
		response.put("status", "success");
		response.put("searchDeptInfoList", searchDeptInfoList); 
		response.put("totalCount", totalCount);	
		response.put("currentPage", pageNo);
		
		return response;
	}
	
	// ================================================================================
		// 6. 다수 학과 코드 삭제 처리 (AJAX)
		// ================================================================================
		@PostMapping("/removeDept")
		@Transactional
		@ResponseBody
		public Map<String, Object> removeDept(@RequestBody Map<String, List<String>> requestBody) { 
			
			Map<String, Object> response = new HashMap<>();
			List<String> deptCodeList = requestBody.get("deptCodeList");
			int successCount = 0;
		    int failCount = 0;
		    List<String> inUseCodes = new ArrayList<>(); // 사용 중인 코드 리스트
			
			if (deptCodeList == null || deptCodeList.isEmpty()) {
				
				response.put("status", "fail");
				response.put("message", "삭제할 학과 코드가 지정되지 않았습니다.");
				return response;
			}
			
			try {
				
				for (String deptCode : deptCodeList) {
	                
					// 1. 검증: 사용자 테이블에서 사용 중인지 확인
					int updateRemoveDeptValidate = deptService.updateRemoveDeptValidate(deptCode);
					
					if (updateRemoveDeptValidate > 0) {
						// 사용 중인 경우: 실패 처리 목록에 추가하고 다음 코드로 넘어감
						log.warn("학과 코드 {}는 사용자 테이블에서 사용 중이라 삭제에 실패했습니다.", deptCode);
		                inUseCodes.add(deptCode);
		                failCount++;
						continue; // 2번 삭제 로직으로 가지 않고 다음 반복으로 점프
					}
					
					// 2. 삭제 처리 로직 시작 (사용 중이 아닌 경우에만 실행됨)
					
					// 2-1. 삭제 전에 insertUpdRemvDeptValidate 체크
					int insertUpdRemvDeptValidate = deptService.insertUpdRemvDeptValidate(deptCode);
					
					if ( insertUpdRemvDeptValidate < 1 ) {
						
						// 해당 코드가 존재 하지 않음
						log.warn("해당 학과 코드를 찾을 수 없습니다.", deptCode);
		                failCount++;
		                continue; // 다음 코드로 넘어감
					}
					
					// 2-2. 학과 테이블 삭제
					int removeDeptResult = deptService.removeDept(deptCode);
					
					if (removeDeptResult == 1) {
	                    // ⭐️ 학과 삭제 성공 시 바로 성공 카운트 증가 ⭐️
	                    successCount++; 
						
						log.info("DeptManagementController$$removeDept : 삭제 성공", deptCode);
					} else {
		                
						// DB에서 삭제 실패 (권한 코드는 사용 중이 아니었으나, DB에서 오류 발생 등)
		                log.error("학과 코드 삭제 중 DB 오류 발생.", deptCode);
		                failCount++;
		            }
				}
				
		        // ⭐️ 최종 응답 메시지 구성 ⭐️
		        String message = "";
		        
		        if (successCount > 0) {
		            message += successCount + "개의 학과 코드가 성공적으로 삭제되었습니다.";
		        }
		        
		        if (failCount > 0) {
		            if (!message.isEmpty()) {
		                message += " ";
		            }
		            message += failCount + "개의 코드는 삭제에 실패했습니다. (사유: ";
		            
		            if (!inUseCodes.isEmpty()) {
		                message += "사용 중인 코드: " + String.join(", ", inUseCodes);
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
					response.put("message", "삭제를 시도한 코드가 없거나 처리된 항목이 없습니다.");
				}
			
			} catch (Exception e) {
				log.error("학과 삭제 중 서버 오류 발생", e);
				response.put("status", "error");
				response.put("message", "서버 오류 발생: " + e.getMessage());
				throw e; // 트랜잭션을 롤백합니다.
			}
			
			return response;
		}
}
