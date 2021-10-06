package com.cos.blogapp.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BoardControllerTest {

	private final BoardRepository boardRepository;
	
	@GetMapping("/test/board/{id}")
	public Board detail(@PathVariable int id) {
		// 영속성컨텍스트 = Board(User 있음, List<Comment> 없음)
		Board boardEntity = boardRepository.findById(id).get();
		//System.out.println(boardEntity); // 객체 호출하면 자동으로 toString() 함수가 호출된다.
		return boardEntity; // MessageConverter, ﻿메시지 컨버터가 JSON을  만들려고 내부의 Getter를 다 때리기 때문에 Lazy라도 출력된다
	}
}
