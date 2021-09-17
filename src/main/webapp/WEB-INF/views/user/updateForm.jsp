<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>

<div class="container">
	<form> <!-- form 의 name은 키값, form 태그는 put을 못 쓰기 때문에 name 값을 지움  -->
	  <div class="form-group">
	    <input type="text" value='${sessionScope.principal.username}'  class="form-control" placeholder="Enter username"  required="required" maxlength="20" readonly="readonly">
	  </div>
	  <div class="form-group">
	    <input type="password" value='${sessionScope.principal.password}'  class="form-control" placeholder="Enter password"  required="required" maxlength="20">
	  </div>
	  <div class="form-group">
	    <input type="email" value='${sessionScope.principal.email}'  class="form-control" placeholder="Enter email"  >
	  </div>
	  <button type="submit" class="btn btn-primary">회원수정</button>
	</form>
</div>

<%@ include file="../layout/footer.jsp" %>