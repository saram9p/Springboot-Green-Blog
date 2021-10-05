package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.service.UserService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

	// DI
	private final UserService userService;
	private final HttpSession session;
	
	// /WEB-INF/views/user/login.jsp
	// /WEB-INF/views/login.jsp
	
	// @RequestBody : json으로 받을 거라면 써야한다
	@PutMapping("/user/{id}") // where 절에 걸린다
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody UserUpdateDto dto, BindingResult bindingResult) {
		// 유효성
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}
		
		// 인증
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		
		// 권한
		if(principal.getId() != id) {
			throw new MyAsyncNotFoundException("회원정보를 수정할 권한이 없습니다.");
		}
				
		userService.회원정보수정(principal, dto);
		
		// 세션 동기화 해주는 부분
		principal.setEmail(dto.getEmail());
		session.setAttribute("principal", principal); // 세션 값 변경
		
		return new CMRespDto<>(1, "성공", null);
	}
	
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

		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		User userEntity = userService.로그인(dto);
		
		if(userEntity == null) { // username, password 잘못 기입

			return Script.back("아이디 혹은 비밀번호를 잘못 입력하였습니다"); // historyback

	}else {
		// 세션 날라가는 조건 : 1. session.invalidate(), 2. 브라우저를 닫으면 날라감
		session.setAttribute("principal", userEntity); // 세션에 담은 이유는 인증하기 위해서
	}
		
		return Script.href("/", "로그인 성공"); // href
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
		
		userService.회원가입(dto);
		
		return Script.href("loginForm"); // 리다이렉션 (300)
	}
	
}



