package com.cos.blogapp.domain.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; //PK (자동증가 번호)
	// 날짜가 필요하지만 지금은 사용하지 않는다
	@Column(nullable = false)
	private String content;
	
	@JoinColumn(name = "userId")
	@ManyToOne
	private User user;
	
	@JoinColumn(name = "boardId")
	@ManyToOne
	private Board board;

	@Override
	public String toString() {
		return "Comment [id=" + id + ", content=" + content + ", user=" + user + ", board=" + board + "]";
	}
	
	
}
