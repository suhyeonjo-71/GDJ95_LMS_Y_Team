package com.example.lms.controller.admin.authManagement;

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

import com.example.lms.dto.SysAuthDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.admin.SysAuthService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin") // 공통 경로 매핑
public class SysAuthManagementController {
	
	@Autowired
	private SysAuthService sysAuthService;

	// ================================================================================
	// 1. 사용자 권한 관리 조회 (페이지 진입 시 최초 로딩 + 페이징)
	// ================================================================================
	@GetMapping("/sysAuthManagement") // HTML/Mustache 파일을 위한 기본 경로
	public String sysAuthManagement(Model model,
			@RequestParam(defaultValue = "1") Integer page,	
			@RequestParam(defaultValue = "10") Integer limit,
			HttpSession session) {
		
		SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
    	String loginUserName = sessionUserDto.getUserName();
		
		Integer startRow = (page - 1) * limit;
		
		// 1. 사용자 권한 전체 리스트 조회
		List<SysAuthDTO> sysAuthAllList = sysAuthService.sysAuthAllList(startRow, limit);
		
		// 2. 전체 건수 조회 ( 페이징 처리 )
		Integer totalCount = sysAuthService.sysAuthAllListCnt();
		
		// 3. 총 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);
		
		log.info("totalCount : " + totalCount);
		
		// ⭐️ 누락된 검색 조건을 Model에 추가 (빈 문자열로 초기화) ⭐️
		model.addAttribute("loginUserName", loginUserName);
	    model.addAttribute("searchSysAuthCondition", "");
		model.addAttribute("authList", sysAuthAllList); // ⭐️ 변수명을 mustache와 일치시킴 ⭐️
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		
		return "/admin/sysAuthManagement"; // mustache 파일 경로
	}
	
	// ================================================================================
	// 2. 사용자 권한 등록 (AJAX) - SysAuth와 SysAuthDetail 동시 등록
	// ================================================================================
	@PostMapping("/insertSysAuth")
	@ResponseBody
	public Map<String, Object> insertSysAuth(@RequestBody SysAuthDTO insertSysAuthDTO) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {

			
			// 권한코드나 권한코드명이 이미 존재하는지 확인
			int sysAuthExistChk = sysAuthService.insertSysAuthExistChk(insertSysAuthDTO);
			
			if ( sysAuthExistChk > 0 ) {
				
				// 권한코드가 존재하므로 세부권한만 등록처리한다.
				// 세부권한코드나 세부권한코드명이 이미 존재하는지 확인
				int insertSysAuthDetailExistChk = sysAuthService.insertSysAuthDetailExistChk(insertSysAuthDTO);
				
				if ( insertSysAuthDetailExistChk > 0 ) {
					
					// 존재하므로 등록 불가
					response.put("status", "fail");
					response.put("message", "세부 권한 등록에 실패했습니다. (코드 중복 등) 다시 시도해주세요.");
				} else {
					
					// 세부 권한(SysAuthDetail) 등록 시도
					int authDetailResult = sysAuthService.insertSysAuthDetail(insertSysAuthDTO);

					if (authDetailResult == 1) {

						// 세부 권한 등록이 성공하면 성공으로 간주
						response.put("status", "success");
						response.put("message", "세부 권한이 성공적으로 등록되었습니다.");
					} else {
						
						response.put("status", "fail");
						response.put("message", "세부 권한 등록에 실패했습니다. (코드 중복 등) 다시 시도해주세요.");
					}
				}
			} else {
				
				//권한 코드가 존재 하지 않는다면 새로 등록 처리 한다.
				int authResult = sysAuthService.insertSysAuth(insertSysAuthDTO);
				
				if ( authResult > 0 ) {
					
					// 권한코드가 존재하므로 세부권한만 등록처리한다.
					// 세부권한코드나 세부권한코드명이 이미 존재하는지 확인
					int insertSysAuthDetailExistChk = sysAuthService.insertSysAuthDetailExistChk(insertSysAuthDTO);
					
					if ( insertSysAuthDetailExistChk > 0 ) {
						
						// 존재하므로 등록 불가
						response.put("status", "fail");
						response.put("message", "권한코드는 등록이 가능하나 세부 권한 등록에 실패했습니다. (코드 중복 등) 다시 시도해주세요.");
					} else {
						
						// 세부 권한(SysAuthDetail) 등록 시도
						int authDetailResult = sysAuthService.insertSysAuthDetail(insertSysAuthDTO);

						if (authDetailResult == 1) {

							// 세부 권한 등록이 성공하면 성공으로 간주
							response.put("status", "success");
							response.put("message", "권한코드 및 세부 권한 등록이 성공적으로 등록되었습니다.");
						} else {
							
							response.put("status", "fail");
							response.put("message", " 권한 코드 등록에 실패하였습니다. (문자 오류 등) 다시 시도해주세요.");
						}
					} 
				} else {
					
					//권한코드 등록 실패 예외
					response.put("status", "fail");
					response.put("message", "권한 코드 등록에 실패하였습니다. (문자 오류 등) 다시 시도해주세요.");
				}
			}
		} catch (Exception e) {
			
			log.error("권한 등록 중 오류 발생", e);
			response.put("status", "error");
			response.put("message", "권한 등록 중 예상치 못한 서버 오류가 발생했습니다. 잠시 후 다시 시도하거나 관리자에게 문의해주세요.");
		}
		
		return response;
	}
	
	// ================================================================================
	// 3. 시스템 사용자 권한 코드 상세 정보 조회 (AJAX)
	// ================================================================================
	@GetMapping("/getSysAuthDetail") // ⭐️ URL 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> getSysAuthAllDetail(@RequestParam String authDetailCode) {
		
		Map<String, Object> response = new HashMap<>();
		
		// 단일 결과를 조회하는 것으로 가정하고 List 대신 단일 DTO로 받습니다.
		SysAuthDTO sysAuthDetail = sysAuthService.selectSysAuthAllDetail(authDetailCode); // 서비스 메서드명 변경 가정
		
		if (sysAuthDetail != null) {
			
			response.put("status", "success");
			response.put("data", sysAuthDetail);
		} else {
			
			response.put("status", "fail");
			response.put("message", "해당 권한 코드를 찾을 수 없습니다.");
		}
		
		return response;
	}
	
	// ================================================================================
	// 4. 시스템 사용자 권한 코드 상세 정보 수정 (AJAX)
	// ================================================================================
	@PostMapping("/updateSysAuth")
	@ResponseBody
	public Map<String, Object> updateSysAuth(@RequestBody SysAuthDTO updateSysAuthDTO) {
	    
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 1. 검증: 사용자 테이블에 해당 세부 권한 코드가 사용 중인지 확인 (수정/삭제 불가 검증)
	        // updateSysAuthDTO.getAuthDetailCode()는 WHERE 절에 쓰일 기존 코드
	        int updateRemoveValidate = sysAuthService.updateRemoveSysAuthDetailValidate(updateSysAuthDTO.getAuthDetailCode());
	    
	        if (updateRemoveValidate > 0) {
	            // 수정불가
	            response.put("status", "fail");
	            response.put("message", "사용자 테이블에서 사용 중인 권한 코드입니다. 수정할 수 없습니다.");
	        } else {
	            // 2. 수정 가능: 안전한 업데이트 순서 (ON UPDATE CASCADE 적용 기준)
	            
	            // 💡 2-1. 권한 코드 수정 (tb_sysauth) - 부모 테이블 먼저 업데이트
	            // tb_sysauth의 auth_code(PK)가 변경되면, DB의 CASCADE 설정에 따라
	            // tb_sysauth_detail의 auth_code(FK)가 자동으로 연쇄 변경됩니다.
	            int updateSysAuthResult = sysAuthService.updateSysAuth(updateSysAuthDTO); 
	            
	            // 💡 2-2. 세부 권한 코드 수정 (tb_sysauth_detail) - PK 및 상세 이름만 업데이트
	            // 이 서비스는 auth_code를 제외한 다른 필드(auth_detail_code, auth_detail_name)를 업데이트합니다.
	            int updateSysAuthDetailResult = sysAuthService.updateSysAuthDetail(updateSysAuthDTO);
	            
	            // Note: updateSysAuthDetailResult와 updateSysAuthResult는 각각 0 또는 1이 나올 수 있으며,
	            // 변경된 내용이 없다면 0이 나올 수 있습니다.
	            
	            if (updateSysAuthDetailResult >= 0 && updateSysAuthResult >= 0) {
	                // 두 업데이트가 모두 오류 없이 실행되었다면 성공으로 간주 (트랜잭션이 성공적으로 커밋됨)
	                response.put("status", "success");
	                response.put("message", "권한 정보가 성공적으로 수정되었습니다.");
	            } else {
	                // 이 블록에 도달하면 논리적 오류이거나 예상치 못한 DB 상태입니다. (실제로는 거의 발생하지 않음)
	                response.put("status", "fail");
	                response.put("message", "수정된 항목이 없거나 수정에 실패했습니다.");
	            }
	        }
	    } catch (Exception e) {
	        log.error("권한 수정 중 오류 발생", e);
	        // @Transactional에 의해 자동 롤백됩니다.
	        response.put("status", "error");
	        response.put("message", "서버 오류가 발생했습니다: " + e.getMessage()); 
	    }
	    
	    return response;
	}
	
	// ================================================================================
	// 5. 사용자 권한 코드 검색 조회 (AJAX - 리스트 갱신용)
	// ================================================================================
	@GetMapping("/searchSysAuth") // ⭐️ URL 충돌 방지 및 명확하게 변경 ⭐️
	@ResponseBody
	public Map<String, Object> searchSysAuth(@RequestParam String searchSysAuthCondition,
			@RequestParam(value = "currentPage", defaultValue = "1") int pageNo) {
		
		Map<String, Object> response = new HashMap<>();
		
		int limit = 10;
		int startRow = (pageNo - 1) * limit;
		
		// MyBatis는 여러 파라미터를 받을 때 Map이나 DTO에 담아서 전달해야 합니다.
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("searchSysAuthCondition", searchSysAuthCondition);
		searchParams.put("startRow", startRow);
		searchParams.put("limit", limit);
		
		List<SysAuthDTO> searchSysAuthInfoList = sysAuthService.searchSysAuthInfoList(searchParams); // 서비스 메서드 시그니처 변경 가정
		
		int totalCount = sysAuthService.searchSysAuthInfoListCnt(searchSysAuthCondition);
		
		response.put("status", "success");
		response.put("authList", searchSysAuthInfoList); // ⭐️ userList -> authList로 변경 ⭐️
		response.put("totalCount", totalCount);	
		response.put("currentPage", pageNo);
		
		return response;
	}
	
	// ================================================================================
		// 6. 다수 사용자 권한 코드 삭제 처리 (AJAX)
		// ================================================================================
		@PostMapping("/removeSysAuth")
		@ResponseBody
		public Map<String, Object> removeSysAuth(@RequestBody Map<String, List<String>> requestBody) { 
			
			Map<String, Object> response = new HashMap<>();
			List<String> authDetailCodeList = requestBody.get("authDetailCodeList");
			int successCount = 0;
		    int failCount = 0;
		    List<String> inUseCodes = new ArrayList<>(); // 사용 중인 코드 리스트
			
			if (authDetailCodeList == null || authDetailCodeList.isEmpty()) {
				
				response.put("status", "fail");
				response.put("message", "삭제할 권한 코드가 지정되지 않았습니다.");
				return response;
			}
			
			try {
				
				for (String authDetailCode : authDetailCodeList) {
	                
					// 1. 검증: 사용자 테이블에서 사용 중인지 확인
					int updateRemoveValidate = sysAuthService.updateRemoveSysAuthDetailValidate(authDetailCode);
					
					if (updateRemoveValidate > 0) {
						// 사용 중인 경우: 실패 처리 목록에 추가하고 다음 코드로 넘어감
						log.warn("권한 코드 {}는 사용자 테이블에서 사용 중이라 삭제에 실패했습니다.", authDetailCode);
		                inUseCodes.add(authDetailCode);
		                failCount++;
						continue; // 2번 삭제 로직으로 가지 않고 다음 반복으로 점프
					}
					
					// 2. 삭제 처리 로직 시작 (사용 중이 아닌 경우에만 실행됨)
					
					// 2-1. 삭제 전에 auth_code 가져오기 (부모 테이블 삭제용)
					String authCode = sysAuthService.selectBeforeRemoveAuthCd(authDetailCode);
					
					if (authCode == null) {
		                
						// 해당 세부 코드가 존재하지 않거나 부모 코드를 찾을 수 없음
		                log.warn("권한 코드 {}에 연결된 부모 코드(authCode)를 찾을 수 없습니다.", authDetailCode);
		                failCount++;
		                continue; // 다음 코드로 넘어감
		            }
					
					// 2-2. 세부 권한 테이블부터 삭제 (tb_sysauth_detail)
					int removeSysAuthDetailResult = sysAuthService.removeSysAuthDetail(authDetailCode);
					
					if (removeSysAuthDetailResult == 1) {
	                    // ⭐️ 세부 권한 삭제 성공 시 바로 성공 카운트 증가 ⭐️
	                    successCount++; 
						
						// 2-3. 권한 테이블 삭제 (tb_sysauth) - 데이터 무결성 검증 후 삭제
						// 해당 authCode를 사용하는 다른 Detail이 남아있는지 체크 (남아있지 않으면 0 반환)
						if ( sysAuthService.selectBeforeRemoveAuthCdValidate(authCode) == 0 ) {
							
							// 해당 authCode를 사용하는 Detail이 더 이상 없으므로, 부모 권한도 삭제
							sysAuthService.removeSysAuth(authCode); 
							log.info("부모 권한 코드 {}에 연결된 Detail이 모두 삭제되어 부모 권한도 삭제했습니다.", authCode);
						}
					} else {
		                // DB에서 삭제 실패 (권한 코드는 사용 중이 아니었으나, DB에서 오류 발생 등)
		                log.error("권한 코드 {}의 세부 권한 삭제 중 DB 오류 발생.", authDetailCode);
		                failCount++;
		            }
				}
				
		        // ⭐️ 최종 응답 메시지 구성 ⭐️
		        String message = "";
		        
		        if (successCount > 0) {
		            message += successCount + "개의 권한 코드가 성공적으로 삭제되었습니다.";
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
				log.error("권한 삭제 중 서버 오류 발생", e);
				response.put("status", "error");
				response.put("message", "서버 오류 발생: ");
			}
			
			return response;
		}

}