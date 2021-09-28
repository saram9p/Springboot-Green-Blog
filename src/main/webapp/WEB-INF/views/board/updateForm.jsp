<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>

<div class="container">
	<form onsubmit="update(event, ${boardEntity.id})"> <!-- ﻿el 표현식은 자바스크립트에 넣으면 안된다 (왜냐하면 자바스크립트를 파일로 빼서 만들거기 때문에)-->
		<div class="form-group">
			<input type="text"  id="title"  value="${boardEntity.title}"  class="form-control" placeholder="Enter title">
		</div>
		<div class="form-group">
			<textarea id="content" class="form-control" rows="5" >${boardEntity.content}</textarea>
		</div>
		<button type="submit" class="btn btn-primary">수정하기</button>
	</form>
</div>
<script>
	async function update(event, id) { //매개변수, async 붙인 이유 cpu가 놀지 말라고
		console.log(event);
		event.preventDefault();
		// 주소 : PUT board/3
		// UPDATE board SET title = ?, content = ? WHERE id = ?
		let boardUpdateDto = {
				title: document.querySelector("#title").value,
				content: document.querySelector("#content").value
		};
		
		console.log(boardUpdateDto);
		console.log(JSON.stringify(boardUpdateDto));
		// JSON.stringify(자바스크립트 오브젝트) => 리턴 json 문자열
		// JSON.parse(제이슨 문자열) => 리턴 자바스크립트 함수
		
		// 같은 도메인이면 자바스크립트를 다 허용한다 // 내서버는 localhost 생략가능, await 이유 응답을 기다리려고
		let response = await fetch("http://localhost:8080/board/" + id, {
			method: "put",
			body: JSON.stringify(boardUpdateDto),
			headers: {
				"Content-Type": "application/json; charset=utf-8" // utf-8 : 1~3의 가변길이, 한글은 3바이트
			}//무슨 데이터가 날라오는지 모르니까 contenttype으로 데이터 타입을 알려준다
		});
		
		let parseResponse = await response.json(); // 나중에 스프링함수에서 리턴될때 머가 리턴되는지 확인해보자!!
		
		// response.text()로 변경해서 확인해보자	.
		console.log(parseResponse);	
		
		if(parseResponse.code == 1) {
			alert("업데이트 성공");
			location.href = "/board/" + id
		} else {
			alert("업데이트 실패: " + parseResponse.message);
		}
		
	}

	$('#content').summernote({
		height : 350
	});
</script>
<%@ include file="../layout/footer.jsp" %>


    