package com.cos.blogapp.domain.board;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data  // getter, setter, toString
@Entity
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; //PK (자동증가 번호)
	@Column(nullable = false, length = 50)
	private String title; // 아이디
	@Lob
	private String content;
	
	@JoinColumn(name = "userId") // 이름을 정해줌
	@ManyToOne(fetch = FetchType.EAGER) // LAZY : 지연로딩, EAGER : 기본전략, 한 건이면 씀 
	private User user; // 오브젝트 변환, Vo가 필요없다
	
	// 양방향 매핑 , 기본전략 :LAZY
	// mappedBy 에는 FK의 주인의 변수이름을 추가한다.
	@JsonIgnoreProperties({"board"}) // comments 객체 내부의 필드를 제외시키는 법
	// @JsonIgnore comments 자체를 제외시킨다.
	@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
	@OrderBy("id desc")
	private List<Comment> comments;
}
