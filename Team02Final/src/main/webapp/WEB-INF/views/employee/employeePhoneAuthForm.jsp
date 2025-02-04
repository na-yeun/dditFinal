<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/img/favicon/favicon.ico" />
<title>work2gether</title>
<%@include file="employeePreScript.jsp"%>
</head>

<body id="login-body" data-contextPath="${pageContext.request.contextPath}">
	<%@include file="employeeMessageSwal.jsp"%>
	<div class="main-div">
		<div class="logo">
			<img id="logoImg" alt="logo"
				src="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/img/layouts/logo.png" />
		</div>
		<div class="header">
			<h4 class="sms-title" id="smsLabel">문자 인증</h4>
		</div>
		<div class="sms-body">
			<form id="sms-form">
				<input type="text" name="empName" placeholder="이름을 입력해주세요."
					class="form-control" /> <input type="text" name="empMail"
					id="accountMail" placeholder="이메일을 입력해주세요." class="form-control" />
				<input type="text" name="empPhone" placeholder="핸드폰 번호를 입력해주세요."
					class="form-control" />

				<button type="submit" class="btn btn-primary">문자전송</button>
				<p id="guide-phone-auth"></p>
			</form>


			<form id="auth-check-form" method="post"
				action="/work2gether/a001/empauth/checkAuthCode">
				<input type="text" name="authCode" id="auth-input"
					placeholder="인증번호 요청을 해주세요." class="form-control"
					disabled="disabled" />
				<button type="submit" class="btn btn-primary">인증번호 확인</button>
				<p id="phone-auth-result"></p>
			</form>
			<div class="links">
				<a href="login">로그인화면으로 돌아가기</a>
			</div>
		</div>

	</div>

	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/js/app/employee/employeePhoneAuthForm.js"></script>
</body>
</html>
