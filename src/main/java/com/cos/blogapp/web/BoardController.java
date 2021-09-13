package com.cos.blogapp.web;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 붙은 변수의 생성자를 만듬
@Controller // 컴퍼넌트 스캔 (스프링) IoC
public class BoardController { // ioc 컨테이너의 BoardController를 메모리에 띄운다
	
	// DI (생성자 주입)
	private final BoardRepository boardRepository;
	private final HttpSession session;
	
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		User principal = (User) session.getAttribute("principal");
		
		// 인증체크
		if(principal == null) { // 로그인 안됨
			return Script.href("/loginForm", "잘못된 접근입니다");
		}
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
			
//		User user = new User();
//		user.setId(3);
//		boardRepository.save(dto.toEntity(user)); // 밑의 코드랑 동일하다
		dto.setContent(dto.getContent().replaceAll("<p>", ""));
		dto.setContent(dto.getContent().replaceAll("</p>", ""));
		boardRepository.save(dto.toEntity(principal));
		return Script.href("/", "글쓰기 성공"); 
	}
	
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saverForm";
	}
	
	@GetMapping({"/board"}) // /board(모델명), 페이지를 쿼리스트링으로 받는 게 좋다
	public String home(Model model, int page) {
//		// 첫번째 방법 , Integer는 Wrapping 클래스
//		if(page == null) {
//			System.out.println("page값이 null입니다.");
//			page = 0;
//		}
		
		// Pageable : 현재 페이지나, 끝페이지 등 전부 계산 해준다
		Pageable pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());
		
		// Sort.by(Sort.Direction.DESC, "id")
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest); //동기화된 데이터는 Entitiy 붙임, 복수는 s 붙임
		model.addAttribute("boardsEntity", boardsEntity);
		// System.out.println(boardsEntity.get(0).getUser().getUsername());
		return "board/list";
	}
	
}
