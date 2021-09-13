package com.cos.blogapp.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 생략 가능 JapRepository가 있어서
public interface BoardRepository extends JpaRepository<Board, Integer> { // 제네릭 자리에 Board를 넣으면, Board 타입으로 매핑 해준다
// JpaReopository를 상속하면 IoC 컨테이너에 올라간다
	// IoC 컨테이너에 들어간 것은 생성자주입을 통해 가져온다
}
