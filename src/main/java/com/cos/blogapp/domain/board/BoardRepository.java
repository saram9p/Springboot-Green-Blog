package com.cos.blogapp.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 생략 가능 JapRepository가 있어서
public interface BoardRepository extends JpaRepository<Board, Integer> { // 제네릭 자리에 Board를 넣으면, Board 타입으로 매핑 해준다

}
