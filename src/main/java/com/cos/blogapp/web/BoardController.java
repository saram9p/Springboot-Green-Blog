package com.cos.blogapp.web;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 붙은 변수의 생성자를 만듬
@Controller // 컴퍼넌트 스캔 (스프링) IoC
public class BoardController { // ioc 컨테이너의 BoardController를 메모리에 띄운다
	
	// DI (생성자 주입)
	private final BoardRepository boardRepository;
	private final HttpSession session;
	

	// DELETE FROM board WHERE id = ?, html body가 없다
	@DeleteMapping("/board/{id}")
	public @ResponseBody String deleteByid(@PathVariable int id) {
		boardRepository.deleteById(id);
		return "ok";
	}
	
	// UPDATE board SET title = ?, content = ? WHERE id =?
	//@PutMapping("/board/{id}")
	
	// RestFul API 주소 설계 방식
	// RestFul-API 의 특징 1. 동사를 적지 마라
	//@GetMapping("/user/1/board/3") // 주소를 말하는데로 씀
	
	// 쿼리스트링, 패스var => 디비 where 에 걸리는 친구들!!
	// 1.컨트롤러 선정, 2. Http Method 선정, 3. 받을 데이터가 있는지!! (body, 쿼리스트링, 패스 var)
	// 4. 디비에 접근을 해야하면 Model 접근하기 orElse Model에 접근할 필요가 없다.
	@GetMapping("/board/{id}") // id는 주소에 걸려있는 데이터
	public String detail(@PathVariable int id, Model model) {
		// select * from board where id = :id
		
		// 1. orElse 는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안 내용 리턴)
//		Board boardEntity = boardRepository.findById(id) // DB에서 들고 온 데이터는 Entity 를 붙임, 여러건을 들고 올때는 s 를 붙임
//				.orElse(new Board(100, "글없어요", "글없어요", null)); 
		
		// 2, orElseThrow
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotFoundException(id +" 못찾았어요")); // 중괄호를 안 적으면 무조건 return 코드가 된다
		
		model.addAttribute("boardEntity", boardEntity);
		return "board/detail";
	}
	
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		User principal = (User) session.getAttribute("principal");
		
		// 인증, 권한 체크 (공통로직)
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
	
	// /board?page=2
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
