package com.example.lms.controller.user;

import java.net.Authenticator.RequestorType;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.lms.controller.login.LoginController;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.common.authorization.ViewAuthorizationValidateService;
import com.example.lms.service.user.UserService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 2025. 11. 25.
 * Author - jm
 * 사용자 컨트롤러
 */
@Slf4j
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ViewAuthorizationValidateService viewAuthorizationValidateService;
	
		// 내정보 - 개인정보관리
		@GetMapping("/myInfo/userInfo")
		public String mUserInfo(HttpSession session, Model model) {
			
			
			// 세션불러오기
			SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");
			String loginUserName = loginSysUserDTO.getUserName();
			
			
			// 사이드 메뉴 권한 필터 처리
			String authCode = loginSysUserDTO.getAuthCode();
			Map<String, Boolean> menuValidateRsltMap = viewAuthorizationValidateService.MenuAuthorizationValidate(authCode);
			
			Boolean useAdmin = menuValidateRsltMap.get("useAdmin");
			Boolean useStudent = menuValidateRsltMap.get("useStudent"); 
			Boolean useProfessor = menuValidateRsltMap.get("useProfessor");
			Boolean useStaff = menuValidateRsltMap.get("useStaff");
			
			// 세션에 있는 userNo, userId로 조회함
			// MyBatis는 SQL 쿼리의 결과를 읽어올 때, 결과 셋(ResultSet)의 컬럼 이름을 Map의 **키(Key)**에 담아 값을 담아준다
			// 즉 별칭을 userNo를 해주면 userNo라는 키에 user_no 라는 값을 담아주는것.
			Map<String, Object> userInfoMap = userService.userInfoMap(loginSysUserDTO);
			
			//모델 반환값
			model.addAttribute("loginUserName", loginUserName);
			model.addAttribute("userInfo", userInfoMap);
			log.info("userInfoMap 조회 완료: {}", userInfoMap);
			model.addAttribute("useAdmin", useAdmin);
			model.addAttribute("useStudent", useStudent);
			model.addAttribute("useProfessor", useProfessor);
			model.addAttribute("useStaff", useStaff);
			
			return "/myInfo/userInfo";
		}
	

		// 내정보 - 개인정보관리 - 개인 정보 변경
		@PostMapping("/updateInfo") 
		public String updateUserInfo( HttpSession session, @ModelAttribute SysUserDTO updatedUserDTO, 
		    RedirectAttributes redirectAttributes ) {

		    // 세션에서 로그인 정보 불러오기 (userNo, userId 확보)
		    SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");
		    
		    // 수정의 기준이 되는 userNo, userId를 DTO에 설정
		    updatedUserDTO.setUserNo(loginSysUserDTO.getUserNo());
		    updatedUserDTO.setUserId(loginSysUserDTO.getUserId());

		    // 서비스 호출
		    Integer updateResult = userService.updateUserInfo(updatedUserDTO);
		    
		    if (updateResult == 1) {
		        
		        log.info("UserController$$updateUserInfo : 개인 정보 수정 성공");
		        
		        // 1. 성공 메시지 전달
		        redirectAttributes.addFlashAttribute("message", "개인 정보가 성공적으로 변경되었습니다.");
		        
		        // 임시 방편으로 변경된 정보만 갱신 (가장 이상적인 방법은 서비스 레이어에서 DB 재조회 후 세션 갱신)
		        // loginSysUserDTO.setUserName(updatedUserDTO.getUserName()); -- 1208 잠시 주석처리함 버그생김
		        // ... 변경된 모든 필드를 loginSysUserDTO에 반영해야 합니다.
		        
		        // 3. 원래 페이지(상세 정보 뷰)로 리다이렉트
		        return "redirect:/myInfo/userInfo";
		        
		    } else {
		        
		        log.warn("UserController$$updateUserInfo : 개인 정보 수정 실패. DB 업데이트 행 수: {}", updateResult);
		        
		        // 1. 실패 메시지 전달
		        //    뷰 단에서 이 'error' 메시지를 받아서 모달 등으로 띄워야 합니다.
		        redirectAttributes.addFlashAttribute("error", "개인 정보 변경에 실패했습니다. 입력 정보를 확인해 주세요.");
		        
		        // 2. 현재 편집 화면에 유지
		        //    '/myInfo/userInfo' 페이지에서 `?edit=true`와 같은 파라미터로 편집 모드를 유지한다고 가정
		        return "redirect:/myInfo/userInfo?edit=true"; 
		    }
		}

		// 내정보 - 개인정보 관리 - 비밀번호 변경 (AJAX 비동기 처리)
		@PostMapping("/modifyPassword")
		@ResponseBody // AJAX 요청이므로 JSON 응답을 위해 @ResponseBody 사용
		// AJAX 요청 본문(Body)의 JSON 데이터를 Map으로 받음 (currentPassword, userNewPassword)
		public ResponseEntity<?> mModifyUserInfoPassword( HttpSession session, @RequestBody Map<String, Object> passwordData ) {

			// 세션에서 로그인 정보 불러오기 (userNo, userId 확보)
		    SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");
		    String loginUserName = loginSysUserDTO.getUserName();
		    

		    // Map에 필요한 정보 추가 (Mapper 파라미터와 일치하도록)
		    // passwordData에 현재 비밀번호는 'userPassword' 키로, 새 비밀번호는 'userNewPassword' 키로 들어있다고 가정
		    passwordData.put("userNo", loginSysUserDTO.getUserNo());
		    passwordData.put("userId", loginSysUserDTO.getUserId());
		    
		    // 서비스 호출
		    Integer modfiyValidate = userService.modifyUserInfoPassword(passwordData);
		    
		    if (modfiyValidate == 1) {
		    	
		        log.info("UserController$$modifyUserInfoPassword : 비밀번호 변경 완료");
		        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
		    } else {
		    	
		        log.warn("UserController$$modifyUserInfoPassword : 비밀번호 변경 실패 (현재 비밀번호 불일치 등)");
		        return ResponseEntity.badRequest()
		                             .body(Map.of("message", "현재 비밀번호가 일치하지 않거나 변경에 실패했습니다."));
		    }
		}
}
