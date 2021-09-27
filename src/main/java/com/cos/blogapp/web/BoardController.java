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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 붙은 변수의 생성자를 만듬
@Controller // 컴퍼넌트 스캔 (스프링) IoC
public class BoardController { // ioc 컨테이너의 BoardController를 메모리에 띄운다
	
	// DI (생성자 주입)
	private final BoardRepository boardRepository;
	private final HttpSession session;
	
	@PutMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody BoardSaveReqDto dto, BindingResult bindingResult) { // 제네릭에 ?를 넣으면 리턴 시에 타입이 결정됨, @RequestBody는 버퍼로 있는 그대로 받는다, 파싱할 수 있다.
		// dto 바로 옆에 BindingResult가 있어야한다
		User principal = (User) session.getAttribute("principal");
		
		// 인증
		if(principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		// 권한
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyAsyncNotFoundException("해당 글을 찾을 수 없습니다."));
		if (principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당 글을 수정할 권한이 없습니다.");
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
		

		
		Board board = dto.toEntity(principal);
		board.setId(id); // update의 핵심, 같은 primary key 일때 업데이트가 된다
		
		boardRepository.save(board);
		
		return new CMRespDto<>(1, "업데이트 성공", null);
	}
	
	@GetMapping("/board/{id}/updateForm") // 데이터를 들고 올 때는 주소가 필요하다. (board 모델의 id번글의 수정하기 화면을 주세요)
	public String boardupdateForm(@PathVariable int id, Model model) { //서비스 만들 때 인증과 권한은 이 함수에 필요없다, 모델에 접근하지 않아서
		// 게시글 정보를 가지고 가야함.
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyNotFoundException(id + "번호의 게시글을 찾을 수 없습니다.")); // Optional은 선택권을 준다.
		model.addAttribute("boardEntity", boardEntity);
		
		return "board/updateForm";
	}
	
	// API(AJAX) 요청
	// DELETE FROM board WHERE id = ?, html body가 없다
	@DeleteMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> deleteByid(@PathVariable int id) { // 오브젝트로 받으면 json(같은 문자열)으로 리턴한다

		// AOP 처리 가능
		// 인증이 된 사람만 함수 접근 가능!! (로그인 된 사람)
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		// 권한이 있는 사람만 함수 접근 가능 (principal.id == {id})
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyAsyncNotFoundException("해당글을 찾을 수 없습니다"));
		if (principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다");
		}

		try {
			boardRepository.deleteById(id); // 오류 발생??? (id가 없으면)
		} catch (Exception e) {
			throw new MyAsyncNotFoundException(id + "를 찾을 수 없어서 삭제할 수 없어요.");
		}
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
		return "board/saveForm";
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
