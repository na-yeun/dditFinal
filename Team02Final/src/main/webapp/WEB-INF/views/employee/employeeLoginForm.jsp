<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/img/favicon/favicon.ico" />
<title>work2gether</title>
<%@include file="employeePreScript.jsp" %>

</head>

<body id="login-body" data-contextPath="${pageContext.request.contextPath}">
	<%@include file="employeeMessageSwal.jsp" %>
	<div class="main-div">
		<div class="logo">
			<img id="logoImg" alt="logo" src="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/img/layouts/logo.png" />
		</div>
		<div>
			<form method="post" id="loginForm">
				<div class="form-group">
					<div class="input-area">
						<div class="input-group">
							<input type="text" id="emp-mail-mapping" class="form-control" placeholder="이메일을 입력해주세요." name="accountMail" aria-describedby="basic-addon13" required>
							<span class="input-group-text" id="basic-addon13">@gmail.com</span>
						</div>
						<input maxlength="30" type="password" id="emp-pass-mapping" class="form-control passwordCapsLock" placeholder="비밀번호를 입력해주세요." name="accountPass" required/>
					</div>
					<div class="button-area">
						<button type="submit">LOGIN</button>
					</div>
				</div>
			</form>
			<div id="pre-btn-area">
				<button type="button" id="emp-btn" class="btn btn-outline-dark btn-sm">임직원</button>
				<button type="button" id="manage-btn" class="btn btn-outline-dark btn-sm">관리자</button>
				<button type="button" id="provider-btn" class="btn btn-outline-dark btn-sm">프로바이더</button>
			</div>
		</div>
		<div class="links">
			<a href="javascript:void(0);" onclick="openFindPassModal();">비밀번호 찾기</a>
			 | 
			<a href="javascript:void(0);" onclick="openJoinModal();">사원인증</a>
		</div>
	</div>
	
	<!-- 데모페이지 이동 -->
	<a href="${pageContext.request.contextPath }/contract/all/groupWareDemo" 
	   id="demoIcon">
	   	<i class='bx bxs-grid' ></i>
	</a>

	
	<!-- 비밀번호 확인 모달 -->
	<div class="modal fade" id="passFindModal" tabindex="-1" aria-labelledby="joinModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="joinModalLabel">비밀번호 찾기</h4>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div style="text-align: right;">
					<button type="button" class="btn btn-outline-dark btn-sm" id="search-pass-pre-btn" style="margin-right:5px; margin-top:5px;">데이터삽입</button>
				</div>
				<form id="find-modal-form" method="get">
					<div class="modal-body">
						<input type="text" id="search-pass-name" name="empName" placeholder="이름을 입력해주세요." class="form-control" required/>
						<input type="email" id="search-pass-mail" name="empMail" placeholder="이메일을 입력해주세요." class="form-control" required/>
						<input type="tel" id="search-pass-tel" oninput="validateInput(this,11)" id="empPhone" name="empPhone" placeholder="핸드폰 번호를 입력해주세요." class="form-control" required/>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">확인</button>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">닫기</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- 회원가입모달 -->
	<div class="modal fade" id="joinModal" tabindex="-1" aria-labelledby="joinModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="joinModalLabel">사원인증</h4>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div style="text-align: right;">
					<button type="button" class="btn btn-outline-dark btn-sm" id="auth-emp-pre-btn" style="margin-right:5px; margin-top:5px;">데이터삽입</button>
				</div>
				<form id="join-modal-form" method="get" action="${pageContext.request.contextPath}/a001/join/mailCheck">
					<div class="modal-body">
						<input type="email" id="auth-mail" name="accountMail" placeholder="이메일을 입력해주세요." class="form-control" required/>
						<input type="password" id="auth-pass" name="accountPass" placeholder="비밀번호를 입력해주세요." class="form-control passwordCapsLock" required/>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">확인</button>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">닫기</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/employee/employeeLoginForm.js"></script>

</body>
</html>
