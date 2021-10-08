package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 댓글삭제(int id, User principal) {
		Comment commentEntity = commentRepository.findById(id)
			.orElseThrow(()-> new MyAsyncNotFoundException("없는 댓글 번호 입니다."));
		
		if(principal.getId() != commentEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당 게시글을 삭제할 수 없는 유저입니다.");
		}
		
		commentRepository.deleteById(id);
		
	}
	
	@Transactional(rollbackFor = MyNotFoundException.class)
	public void 댓글등록(int boardId, CommentSaveReqDto dto, User principal) {
		// 하나의 트랜잭션
		
		// 1. Comment 객체에 값 추가하기, id : X, content : DTO 값, user: 세션 값, board: boardId로 findById하세요(오류 검사하기 좋음, 유효성 검사하기 좋음)
		Board boardEntity = boardRepository.findById(boardId)
				.orElseThrow(()-> new MyNotFoundException("해당 게시글을 찾을 수 없습니다."));
		
		// 2. Comment 객체 만들기 (빈객체 생성)
		Comment comment = new Comment();
		comment.setContent(dto.getContent());
		comment.setUser(principal);
		comment.setBoard(boardEntity);
		
		// 3. save 하기
		commentRepository.save(comment);
		
	}
	
}
