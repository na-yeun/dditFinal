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

<body id="join-body" data-contextPath="${pageContext.request.contextPath}">
	<%@include file="employeeMessageSwal.jsp"%>
	<c:if test="${not empty errorMap}">
	    <script>
	        let msg = ``;
	        <c:forEach var="entry" items="${errorMap}">
	            <c:if test="${entry.key eq 'empImage' or entry.key eq 'empSignimage'}">
	                msg += `이미지 형식을 확인해주세요.`; // empImage 관련 오류 메시지 추가
	            </c:if>
	        </c:forEach>
	
	        console.log("msg", msg);
	
	        Swal.mixin({
	            toast: true,
	            position: 'center',
	            showConfirmButton: false,
	            timer: 3000,
	            timerProgressBar: true,
	            didOpen: (toast) => {
	                toast.addEventListener('mouseenter', Swal.stopTimer);
	                toast.addEventListener('mouseleave', Swal.resumeTimer);
	            }
	        }).fire({
	            icon: 'error',
	            title: msg // 메시지 출력
	        });
	    </script>
	</c:if>
	
	<div class="join-main-div">
		<div class="logo">
			<img id="logoImg" alt="logo" src="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/img/layouts/logo.png" />
		</div>

		<div>
			<c:if test="${empty sessionScope.oAuth}">
				<a class="google-oauth-a" href="${pageContext.request.contextPath}/google-oauth">
					<img id="googleImg" alt="google-logo" src="${pageContext.request.contextPath}/resources/images/google-logo.png" />
					<span id="googleInfo">구글 계정 인증하기</span>
				</a>
				<p id="google-oauth-introduction">사내 관리자를 통해 등록한 이메일 계정을 사용해주세요. 다른 계정 사용시 오류가 발생할 수 있습니다.</p>
			</c:if>
			<c:if test="${not empty sessionScope.oAuth}">
				<img id="googleImg" alt="google-logo" src="${pageContext.request.contextPath}/resources/images/google-logo.png" />
				<span id="googleInfo"> 구글 계정 인증완료 </span>
				<p id="google-oauth-introduction">   </p>
			</c:if>
		</div>

		<form:form id="joinForm" method="post" modelAttribute="myEmp" enctype="multipart/form-data">
			<div class="mb-4 row">
				<label for="empMail" class="col-md-2 col-form-label">이메일</label>
				<div class="col-md-10">
					<form:input path="empMail" class="form-control" disabled="true"/>
					<form:input path="empMail" class="form-control" hidden="true"/>
					<form:errors path="empMail" cssClass="text-danger"/>
				</div>
			</div>

			<div class="mb-4 row" hidden>
				<label for="empId" class="col-md-2 col-form-label">사원번호</label>
				<div class="col-md-10">
					<form:input path="empId" class="form-control"/>
					<form:errors path="empId" cssClass="text-danger"/>
				</div>
			</div>
			
			<div class="mb-4 row">
				<label for="empName" class="col-md-2 col-form-label">사원이름</label>
				<div class="col-md-10">
					<form:input path="empName" class="form-control" disabled="true"/>
					<form:input path="empName" class="form-control" hidden="true"/>
					<form:errors path="empName" cssClass="text-danger"/>
				</div>
			</div>
			
			<div class="mb-4 row">
				<label for="empBirth" class="col-md-2 col-form-label">생년월일</label>
				<div class="col-md-10">
					<form:input path="empBirth" class="form-control" disabled="true"/>
					<form:input path="empBirth" class="form-control" hidden="true"/>
					<form:errors path="empBirth" cssClass="text-danger"/>
				</div>
			</div>
			
<!-- 			<div class="mb-4 row"> -->
<!-- 				<label for="empGender" class="col-md-2 col-form-label">성별</label> -->
<!-- 				<div class="col-md-10"> -->
<%-- 					<form:input path="empGender" class="form-control" disabled="true"/> --%>
<%-- 					<form:errors path="empGender" cssClass="text-danger"/> --%>
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="mb-4 row">
				<label for="empGender" class="col-md-2 form-check-label">성별</label>
				<div class="col-md-10">
					<div class="form-check form-check-inline">
						<form:radiobutton path="empGender" value="M"
							class="form-check-input" id="empGenderMale" />
						<label class="form-check-label" for="empGenderMale">남</label>
					</div>
					<div class="form-check form-check-inline">
						<form:radiobutton path="empGender" value="F"
							class="form-check-input" id="empGenderFemale" />
						<label class="form-check-label" for="empGenderFemale">여</label>
					</div>
					<form:errors path="empGender" cssClass="text-danger" />
				</div>
			</div>

			<div class="mb-4 row">
				<label for="empPass" class="col-md-2 col-form-label">비밀번호</label>
				<div class="col-md-10">
					<input maxlength="30" type="password" name="empPass" class="form-control" id="empPass"/>
					<form:errors path="empPass" cssClass="text-danger"/>
				</div>
			</div>
			<div class="mb-4 row">
				<label for="empPasscheck" class="col-md-2 col-form-label">비밀번호확인</label>
				<div class="col-md-10">
					<input maxlength="30" type="password" id="empPasscheck" class="form-control"/>
					<p id="password-guide">비밀번호를 확인해주세요.</p>
				</div>
			</div>
			
			<div class="mb-4 row">
				<label for="empAddr1" class="col-md-2 col-form-label">주소</label>
				<div class="col-md-10">
					<div style="display: flex; align-items: center; gap: 10px;">
						<form:input path="empAddr1" id="roadAddress" class="form-control" onclick="getAddrForm()" readonly="true" title="클릭하면 주소 검색창이 열립니다." placeholder="클릭하면 주소 검색창이 열립니다."/>
						<a onclick="getAddrForm()" class="btn btn-outline-primary" id="search-btn">검색</a>
					</div>
					<form:errors path="empAddr1" cssClass="text-danger"/>
					<span id="guide" style="color: #999; display: none"></span>
				</div>
			</div>
			<div class="mb-4 row">
				<label for="empAddr2" class="col-md-2 col-form-label">상세주소</label>
				<div class="col-md-10">
					<form:input path="empAddr2" id="extraAddress" class="form-control"/>
					<form:errors path="empAddr2" cssClass="text-danger"/>
				</div>
			</div>
			
			<div class="mb-4 row">
				<label for="empPhone" class="col-md-2 col-form-label">핸드폰번호</label>
				<div class="col-md-10">
					<form:input oninput="validateInput(this,11)" path="empPhone" class="form-control"/>
					<form:errors path="empPhone" cssClass="text-danger"/>
				</div>
			</div>
			
			<div class="mb-4 row">
				<label for="empImage" class="col-md-2 col-form-label">프로필 이미지</label>
				<div class="col-md-10">
					<input type="file" name="empImage" class="form-control" accept="image/*"/>
					<form:errors path="empImage" cssClass="text-danger"/>
				</div>
			</div>
			<div class="mb-4 row">
				<label for="empSignimage" class="col-md-2 col-form-label">도장이미지</label>
				<div class="col-md-10">
					<input type="file" name="empSignimage" class="form-control" accept="image/*"/>
					<form:errors path="empSignimage" cssClass="text-danger"/>
				</div>
			</div>
			<div class="mb-4 row" hidden>
				<label for="empStatus" class="col-md-2 col-form-label">재직상태</label>
				<div class="col-md-10">
					<form:input path="empStatus" class="form-control"/>
					<form:errors path="empStatus" cssClass="text-danger"/>
				</div>
			</div>
			<div class="mb-4 row" hidden>
				<label for="posiId" class="col-md-2 col-form-label">직급 번호</label>
				<div class="col-md-10">
					<form:input path="posiId" class="form-control"/>
					<form:errors path="posiId" cssClass="text-danger"/>
				</div>
			</div>
			
			<div class="mb-4 row" hidden>
				<label for="departCode" class="col-md-2 col-form-label">부서번호</label>
				<div class="col-md-10">
					<form:input path="departCode" class="form-control"/>
					<form:errors path="departCode" cssClass="text-danger"/>
				</div>
			</div>
			<div>
			    <button type="submit" class="btn btn-primary">회원인증</button>
			    <button type="reset" class="btn btn-warning">취소</button>
			</div>
 
		</form:form>

	</div>
	<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/app/employee/employeeJoinForm.js">
	
		
	</script>
</body>

</html>