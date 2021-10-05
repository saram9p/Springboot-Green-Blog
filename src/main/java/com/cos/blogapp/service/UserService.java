package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	
	// 이건 하나의 서비스(핵심로직)인가? (principal 값 변경, update 치고, 세션 값 변경(x))
	// 핵심로직 중 너무 긴것은 뺀다.
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 회원정보수정(User principal, UserUpdateDto dto) {
		// 핵심 로직은 왠만하면 서비스에 넣는다
		// 핵심로직
		principal.setEmail(dto.getEmail());
		
		userRepository.save(principal);
		
	}
	
	@Transactional
	public User 로그인(LoginReqDto dto) {
		

		
		// 1. username, password 받기
		System.out.println(dto.getUsername());
		System.out.println(dto.getPassword());
		// 2. DB -> 조회
		User userEntity =  userRepository.mLogin(dto.getUsername(), SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256));
		
		return userEntity;
		
	}
	
	@Transactional
	public void 회원가입(JoinReqDto dto) {

		String encpassword = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		
		dto.setPassword(encpassword);
		userRepository.save(dto.toEntity());
	}
}
