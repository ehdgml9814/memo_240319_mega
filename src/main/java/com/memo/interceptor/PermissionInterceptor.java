package com.memo.interceptor;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // spring bean(기본으로 작동)
public class PermissionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws IOException {
		
		// 요청 url path를 꺼낸다
		String uri = request.getRequestURI();
		log.info("[@@@@@@ prehandle] uri:{}", uri);
		
		// 로그인 여부 꺼내기(session)
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");
		
		// 비 로그인 상태에서 /post 요청 => 로그인 페이지로 이동, 컨트롤러 수행 방지
		if (userId == null && uri.startsWith("/post")) {
			response.sendRedirect("/user/sign-in-view");
			return false; // 원래 요청에 대한 컨트롤러 수행 막음 
		}
		
		// 로그인 상태에서 /user 요청 => 글목록 페이지로 이동, 컨트롤러 수행 방지
		if (userId != null && uri.startsWith("/user")) {
			response.sendRedirect("/post/post-list-view");
			return false; // 원래 요청에 대한 컨트롤러 수행 막음 
		}
		
		return true; // 컨트롤러 수행
	}
	
	@Override
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler, 
			ModelAndView mav) {
		
		// view, model 객체가 있다는 것은 html이 해석되기 전
		log.info("[$$$$$$$ postHandle]");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, 
			Exception ex) {
		
		// html이 완성된 상태(해석 완료)
		log.info("[####### afterCompletion]");
	}
}
