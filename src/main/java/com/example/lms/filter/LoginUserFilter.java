package com.example.lms.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.example.lms.dto.SysUserDTO;

/**
 * Servlet Filter implementation class LoginUserFilter
 */
@WebFilter("/*") // 모든 요청에 대해 필터가 동작하도록 설정
public class LoginUserFilter extends HttpFilter implements Filter {
       
	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("LoginUserFilter 초기화됨.");
	}

	public void destroy() {
		// 자원 해제 등이 필요하면 여기에 구현
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	        throws IOException, ServletException {
	    
		// 1. HTTP 관련 기능을 사용하기 위해 캐스팅
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// 2. 요청 URI 경로를 가져옵니다. (Context Path를 제외한 부분)
		String contextPath = httpRequest.getContextPath();
		String requestURI = httpRequest.getRequestURI();
		String path = requestURI.substring(contextPath.length());

		// 3. 필터링에서 제외할 경로(Exclude Path) 설정: 로그인, 정적 리소스 등
		if (path.startsWith("/login") || 
		    path.startsWith("/resetUserPasswordForm") ||
		    path.startsWith("/loginForm") ||
		    path.startsWith("/resetUserPassword") ||
			path.startsWith("/resources/") || 
		    path.equals("/") || 
		    path.startsWith("/error") || // 오류 페이지 제외
		    path.startsWith("/api/public/")) { 
		    
			chain.doFilter(request, response);
			return;
		}

		// 4. 세션에서 로그인 사용자 정보를 확인
		HttpSession session = httpRequest.getSession(false);
		
		// 세션 키 'loginUser'로 로그인 상태 확인
		boolean loggedIn = (session != null && session.getAttribute("loginUser") != null);

		if (!loggedIn) {
			// 5. 로그인되지 않은 경우: /login 페이지로 리다이렉트 후 요청 중단
			System.out.println("[LoginUserFilter] 인증되지 않은 접근: " + path + " -> /login으로 리다이렉트");
			httpResponse.sendRedirect(contextPath + "/login");
			return; 
		} else {
            // 6. 로그인된 경우: 권한(Authorization) 확인 로직 실행
            
            // 6-1. 관리자 페이지 접근 확인
            if (path.startsWith("/admin/")) {
                
                SysUserDTO loginSysUserDTO = (SysUserDTO) session.getAttribute("loginUser");
                String authCode = loginSysUserDTO.getAuthCode();
                
                if ( !("A001".equals(authCode)) ) {
                  
                	System.out.println("[LoginUserFilter] 권한 부족 접근: " + path + " (권한: " + authCode + ") -> /main으로 리다이렉트");
                    httpResponse.sendRedirect(contextPath + "/main");
                    return; // 권한 부족 시 요청 처리 중단
                }
            }
            
			chain.doFilter(request, response);
		}
	}
}
