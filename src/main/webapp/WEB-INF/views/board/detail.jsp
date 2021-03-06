<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp"%>

<div class="container">
	<!-- 내 글이면 (권한이 있으면) 수정과 삭제 버튼 보이게 if 사용가능 -->
	<c:if test="${sessionScope.principal.id == boardEntity.user.id }">
		<a href="/board/${boardEntity.id}/updateForm" class="btn btn-warning">수정</a>
		<button class="btn btn-danger" onclick="deleteById(${boardEntity.id})">삭제</button>
	</c:if>

	<script>
			async function deleteById(id) {
				// 1. 비동기 함수 호출 -> 비동기를 잘처리하는 방법??????
				let response = await fetch("http://localhost:8080/api/board/" + id, {
					method: "delete"
				}); // HTTP 요청할 때 쓰는 함수, 약속 - 어음
				// await로 어음을 기다렸다 받는다, 동기화되는데 async로 사용하면 비동기가 된다
				// 저장장치가 일하면 await
				
				// 2.코드
				// json() 함수는 문자열을 자바스크립트 오브젝트로 변환해준다.
				let parseResponse = await response.json(); // 자바스크립트 오브젝트로 파싱한다
				console.log(parseResponse);
				
 				if(parseResponse.code == 1) { // ajax는 자바스크립트에서 분기 시켜줘야 한다
					alert("삭제 성공");
					location.href="/";
				} else {
					// 이 부분에서 msg 출력으로 변경됨
					alert(parseResponse.message);
					location.href="/";
				} 
				
				//alert("삭제성공");
				//location.href="/";
			}
		</script>

	<br /> <br />
	<div>
		글 번호 : ${boardEntity.id}</span> 작성자 : <span><i>${boardEntity.user.username}
		</i></span>
	</div>
	<br />
	<div>
		<h3>${boardEntity.title}</h3>
	</div>
	<hr />
	<div>
		<div>${boardEntity.content}</div>
	</div>
	<hr />

	<div class="card">
		<!-- 댓글 쓰기 시작 -->
		<form action="/api/board/${boardEntity.id }/comment" method="post" >
			<div class="card-body">
				<textarea id="ta-content" name="content" class="form-control" rows="1" ></textarea>
			</div>
			<div class="card-footer">
				<button type="submit" id="btn-reply-save" class="btn btn-primary">등록</button>
			</div>
		</form>
		<!-- 댓글 쓰기 끝 -->
	</div>
	<br />

	<script>
		$("#ta-content").click(()=>{
			if(globalUserId == ""){
				alert("로그인을 먼저 진행해주세요!!");
				location.href = "/loginForm";
			}
		});
	</script>

	<div class="card">
		<div class="card-header">
			<b>댓글 리스트</b>
		</div>
		<ul id="reply-box" class="list-group">

			<c:forEach var="comment" items="${boardEntity.comments }">
				<li id="reply-${comment.id}"
					class="list-group-item d-flex justify-content-between">
					<div>${comment.content}</div>
					<div class="d-flex">
						<div class="font-italic">작성자 : ${comment.user.username } &nbsp;</div>
						<button class="badge" id="reply-${comment.id}" onclick="deleteCommentById(${comment.id})">삭제</button>
					</div>
				</li>
			</c:forEach>
		</ul>
		
		<script>
			async function deleteCommentById(commentId) {
				let response = await fetch("http://localhost:8080/api/comment/" + commentId, { // I/O가 일어나면
					method:"delete"
				});
				
				let parseResponse = await response.json();
				
				if(parseResponse.code == 1) {
					alert("댓글 삭제 성공");
					//location.reload();
					$("#reply-" + commentId).remove();
				} else {
					alert("댓글 삭제에 실패하였습니다. " + parseResponse.message);
				}
				
			}
		</script>
		
	</div>
	<br />
</div>

<%@ include file="../layout/footer.jsp"%>