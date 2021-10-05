package com.cos.blogapp.web;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.service.BoardService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 붙은 변수의 생성자를 만듬
@Controller // 컴퍼넌트 스캔 (스프링) IoC
public class BoardController { // ioc 컨테이너의 BoardController를 메모리에 띄운다
	
	// DI (생성자 주입)
	private final BoardService boardService;
	private final HttpSession session;
	
	@PostMapping("/board/{boardId}/comment") // 2개가 섞여 있으면 명시적으로 적는다 : id -> boardId
	public String commentSave(@PathVariable int boardId, CommentSaveReqDto dto) {
		
		User principal = (User) session.getAttribute("principal");

		boardService.댓글등록(boardId, dto, principal);
		
		return "redirect:/board/" + boardId;
	}
	
	// 자바스크립트로 요청
	@PutMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody BoardSaveReqDto dto, BindingResult bindingResult) { // 제네릭에 ?를 넣으면 리턴 시에 타입이 결정됨, @RequestBody는 버퍼로 있는 그대로 받는다, 파싱할 수 있다.
		
		// dto 바로 옆에 BindingResult가 있어야한다
		
		// 인증
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		
		// 유효성 검사
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드: " + error.getField());
				System.out.println("메시지: " + error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}
		
		boardService.게시글수정(id, principal, dto);
		
		return new CMRespDto<>(1, "업데이트 성공", null);
	}
	
	@GetMapping("/board/{id}/updateForm") // 데이터를 들고 올 때는 주소가 필요하다. (board 모델의 id번글의 수정하기 화면을 주세요)
	public String boardupdateForm(@PathVariable int id, Model model) { //서비스 만들 때 인증과 권한은 이 함수에 필요없다, 모델에 접근하지 않아서
		model.addAttribute("boardEntity", boardService.게시글수정페이지이동(id));
		return "board/updateForm";
	}
	
	// API(AJAX) 요청
	// DELETE FROM board WHERE id = ?, html body가 없다
	@DeleteMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> deleteByid(@PathVariable int id) { // 오브젝트로 받으면 json(같은 문자열)으로 리턴한다
		User principal = (User) session.getAttribute("principal");
		// AOP 처리 가능
		// 인증이 된 사람만 함수 접근 가능!! (로그인 된 사람)
		if (principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		boardService.게시글삭제(id, principal);
		return new CMRespDto<String>(1, "성공", null); // @ResponseBody 데이터 리턴!! String = text/plain
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
		model.addAttribute("boardEntity", boardService.게시글상세보기(id));
		return "board/detail";
	}
	
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		// 공통 로직 시작 ---------------------------------------------
		User principal = (User) session.getAttribute("principal");
		
		// 유효성검사
		// 인증 체크 (공통로직)
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
		// 공통 로직 끝 ---------------------------------------------
		
		// 핵심 로직 시작 ------------------------------
		boardService.게시글등록(principal, dto);
		// 핵심 로직 끝 -------------------------------
		
		return Script.href("/", "글쓰기 성공"); 
	}
	
	// /board?page=2
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	@GetMapping({"/board"}) // /board(모델명), 페이지를 쿼리스트링으로 받는 게 좋다
	public String home(Model model, int page) {
		
		model.addAttribute("boardsEntity", boardService.게시글목록보기(page));
		return "board/list";
	}
	
}
