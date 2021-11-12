package com.cos.blogapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cos.blogapp.handler.SessionInterceptor;

// 서버가 최초 실행될 때 IOC 컨테이너에서 WebMvcConfigurer 타입을 찾아내서 실행시킴
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	public WebMvcConfig() {
	}
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SessionInterceptor()) // 익명 함수 쓰지말고 따로 클래스를 빼서 쓰는 것이 좋다
		.addPathPatterns("/api/**"); // 글로벌 하게 처리	
	}
	
}
