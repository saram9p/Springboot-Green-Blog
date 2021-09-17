package com.cos.blogapp.web;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

	// DI
	private final UserRepository userRepository;
	private final HttpSession session;
	
	// /WEB-INF/views/user/login.jsp
	// /WEB-INF/views/login.jsp
	
	@GetMapping("/user/{id}")
	public String userInfo(@PathVariable int id) { // 주소로 값을 받음
		// 기본은 userRepository.findById(id) 디비에서 가져와야 함.
		// 편법은 세션값을 가져올 수도 있다.
		
		return "user/updateForm"; // 안에 Form 태그가 있어서 Form 을 적음
	}
	
	@GetMapping("/logout")
	public String logout() {
		session.invalidate(); // 세션 무효화 (sessionId에 있는 값을 비우는 것)
		return "redirect:/"; // 
	}
	
	//  /WEB-INF/views/user/login.jsp
	@GetMapping("/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@PostMapping("/login")
	public @ResponseBody String login(@Valid LoginReqDto dto, BindingResult bindingResult) {
		
		// 1. 유효성 검사 실패 - 자바스크립트 응답(경고창, 뒤로가기)
		// 2. 정상 - 로그인 페이지
		
		// System.out.println("에러사이즈: " + bindingResult.getFieldErrors().size());
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		// 1. username, password 받기
		System.out.println(dto.getUsername());
		System.out.println(dto.getPassword());
		// 2. DB -> 조회
		String encPassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		User userEntity =  userRepository.mLogin(dto.getUsername(), encPassword);
		
		
		if(userEntity == null) { // username, password 잘못 기입

				return Script.back("아이디 혹은 비밀번호를 잘못 입력하였습니다"); // historyback

		}else {
			// 세션 날라가는 조건 : 1. session.invalidate(), 2. 브라우저를 닫으면 날라감
			session.setAttribute("principal", userEntity); // 세션에 담은 이유는 인증하기 위해서
			return Script.href("/", "로그인 성공"); // href
		}
	}
	
	@PostMapping("/join")
	public @ResponseBody String join(@Valid JoinReqDto dto, BindingResult bindingResult) { // username=love&password=1234&email=love@nate.com
		// if if if 으로 막는 것은 노가다
		// @Vaild 가 체크해서  bindingResult 에 담음
		System.out.println("에러사이즈: " + bindingResult.getFieldErrors().size());
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		String encpassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		
		dto.setPassword(encpassword);
		userRepository.save(dto.toEntity());
		return Script.href("loginForm"); // 리다이렉션 (300)
	}
	
}



