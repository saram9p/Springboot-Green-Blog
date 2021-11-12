package com.cos.blogapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {

	// 생성자 주입(DI)
	private final BoardRepository boardRepository;

	// 1. 통으로 옮긴다
	// 2. 필요없는 것은 컨트롤러로 뺀다
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 게시글수정(int id, User principal, BoardSaveReqDto dto) {

		// 권한
		Board boardEntity = boardRepository.findById(id) // id로 다시 셀렉트 해야 함
				.orElseThrow(() -> new MyAsyncNotFoundException("해당 글을 찾을 수 없습니다."));
		if (principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당 게시글의 주인이 아닙니다."); // 실패는 핸들러한테 던지면 된다, 성공은 컨트롤러가 하고
		}

		// 영속화된 데이터를 변경하면!!
		boardEntity.setTitle(dto.getTitle());
		boardEntity.setContent(dto.getContent());
		
		//Board board = dto.toEntity(principal);
		//board.setId(id); // update의 핵심, 같은 primary key 일때 업데이트가 된다
		//boardRepository.save(board);
	} // 트랜잭션 종료(더티체킹(수정만))

	public Board 게시글수정페이지이동(int id) {
		// 게시글 정보를 가지고 가야함.
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyNotFoundException(id + "번호의 게시글을 찾을 수 없습니다.")); // Optional은 선택권을 준다.
		return boardEntity;
	}
	
	// 트랜잭션 어노테이션 (트랜잭션을 시작하는 것)
	// rollbackFor (함수내부에 하나의 write라도 실패하면 전체를 rollback 하는 것)
	// 주의 : RuntimeException을 던져야 동작한다.
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 게시글삭제(int id, User principal) {

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
	}
	
	public Board 게시글상세보기(int id) {
		// select * from board where id = :id
		
		// 1. orElse 는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안 내용 리턴)
//		Board boardEntity = boardRepository.findById(id) // DB에서 들고 온 데이터는 Entity 를 붙임, 여러건을 들고 올때는 s 를 붙임
//				.orElse(new Board(100, "글없어요", "글없어요", null)); 
		
		// 2, orElseThrow
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotFoundException(id +" 못찾았어요")); // 중괄호를 안 적으면 무조건 return 코드가 된다
		return boardEntity;
	}
	
	@Transactional(rollbackFor = MyNotFoundException.class) // 이게 붙어야 롤백이 된다
	public void 게시글등록(User principal, BoardSaveReqDto dto) {
		
//		User user = new User();
//		user.setId(3);
//		boardRepository.save(dto.toEntity(user)); // 밑의 코드랑 동일하다
		dto.setContent(dto.getContent().replaceAll("<p>", ""));
		dto.setContent(dto.getContent().replaceAll("</p>", ""));
		boardRepository.save(dto.toEntity(principal));
	}

	@Transactional
	public Page<Board> 게시글목록보기(int page) {
//		// 첫번째 방법 , Integer는 Wrapping 클래스
//		if(page == null) {
//			System.out.println("page값이 null입니다.");
//			page = 0;
//		}
		
		// Pageable : 현재 페이지나, 끝페이지 등 전부 계산 해준다
		// 시간이 오래 걸리지 않아서 이것도 옮김
		Pageable pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());
		
		// Sort.by(Sort.Direction.DESC, "id")
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest); //동기화된 데이터는 Entitiy 붙임, 복수는 s 붙임
		return boardsEntity;
	}
}
