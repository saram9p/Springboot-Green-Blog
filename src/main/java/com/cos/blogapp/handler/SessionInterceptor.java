package com.cos.blogapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.cos.blogapp.domain.user.User;

// default : 인터페이스에 구현체를 쓸 수 있도록 해준다
//adapter : 내가 원하는 함수만 걸러준다
public class SessionInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("preHandle 실행됨");
		
		HttpSession session = request.getSession();
		
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
//			throw new MyNotFoundException("인증되지 않은 사용자입니다.");
//			PrintWriter out = response.getWriter();
//			out.print("야 인증해");
//			out.flush();
			response.sendRedirect("/loginForm");
		}
		return true;
		
	}
}
