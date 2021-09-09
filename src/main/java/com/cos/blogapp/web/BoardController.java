package com.cos.blogapp.web;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 붙은 변수의 생성자를 만듬
@Controller // 컴퍼넌트 스캔 (스프링) IoC
public class BoardController {
	
	// DI (생성자 주입)
	private final BoardRepository boardRepository;
	
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
