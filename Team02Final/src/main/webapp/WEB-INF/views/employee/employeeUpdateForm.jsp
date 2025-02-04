<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	#empMail{
		width: 50%;
	}
	
	#basic-addon13{
		width: 475.88px;
	}
	
	.d-flex{
		display: flex;
	}
</style>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/employee/employeeUpdateForm.css"/>
</head>
<body>
	<c:if test="${not empty errorMap}">
	    <script>
	        let msg = ``;
	        <c:forEach var="entry" items="${errorMap}">
	            <c:if test="${entry.key eq 'empImage' or entry.key eq 'empSignimage'}">
	                msg += `이미지 형식을 확인해주세요.`; // empImage 관련 오류 메시지 추가
	            </c:if>
	        </c:forEach>
	
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
	<h4 id="page-title">개인정보수정</h4>
	<hr/>
		<table>
			<form:form id="joinForm" method="post" modelAttribute="myEmp" enctype="multipart/form-data">
				<tr>
					<td class="tdname">구글 계정 변경</td>
					<td>
						<a class="google-oauth-a" href="${pageContext.request.contextPath}/google-oauth">
							<img id="googleImg" alt="google-logo" src="${pageContext.request.contextPath}/resources/images/google-logo.png" />
							<span id="googleInfo">구글 계정 수정하기</span>
						</a>
						<p id="accountInfo">연결된 계정을 변경하고 싶으신 경우, 계정 인증을 다시 진행해주세요.</p>
					</td>
				</tr>
				<tr>
					<td class="tdname">사번</td>
					<td>
						<form:input path="empId" class="form-control" readonly="true"/>
						<form:errors path="empMail" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">이메일</td>
					<td>
						<script>
							document.addEventListener("DOMContentLoaded", () => {
								let myEmpMail = `${myEmpMail}`;
								let emailId = myEmpMail.split("@")[0];
								
								document.querySelector('#empMail').value = emailId;
							})
						</script>
						<div class="d-flex">
							<input name="empMail" id="empMail" class="form-control" readonly/>
							<span class="input-group-text" id="basic-addon13">@gmail.com</span>
						</div>
						<form:errors path="empMail" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">비밀번호 재설정</td>
					<td>
						<input maxlength="30" type="password" name="empPass" id="empPass" class="form-control"/>
						<form:errors path="empPass" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">비밀번호 확인</td>
					<td>
						<input maxlength="30" type="password" id="empPasscheck" class="form-control"/>
						<p id="password-guide">비밀번호를 확인해주세요.</p>
					</td>
				</tr>
				<tr>
					<td class="tdname">이름</td>
					<td>
						<form:input path="empName" class="form-control"/>
						<form:errors path="empName" cssClass="text-danger"/>
					</td>
				</tr>
				<c:if test="${not empty myEmp.empBirth}">
					<script>
						document.addEventListener("DOMContentLoaded", () => {
							let empBirthArea = document.querySelector('#empBirth');
							
							let empBirth = `${myEmp.empBirth}`;
							if (/^\d{8}$/.test(empBirth)) {
								empBirth=`\${empBirth.substring(0, 4)}-\${empBirth.substring(4, 6)}-\${empBirth.substring(6, 8)}`;
							}
							empBirthArea.value = empBirth;
						})
						
					</script>
				</c:if>
				<tr>
					<td class="tdname">생년월일</td>
					<td>
						<input type="date" id="empBirth" name="empBirth" class="form-control"/>
						<form:errors path="empBirth" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">성별</td>
					<td>
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
					</td>
				</tr>
				<tr>
					<td class="tdname">주소</td>
					<td>
						<div style="display: flex; align-items: center; gap: 10px;">
							<form:input path="empAddr1" id="roadAddress" class="form-control" readonly="true" placeholder="클릭하면 주소 검색창이 열립니다." title="클릭하면 주소 검색창이 열립니다."/>
							<a onclick="getAddrForm()" class="btn btn-outline-primary" id="">주소검색</a>
						</div>
						<form:errors path="empAddr1" cssClass="text-danger"/>
						<span id="guide" style="color: #999; display: none"></span>
					</td>
				</tr>
				<tr>
					<td class="tdname">상세주소</td>
					<td>
						<form:input path="empAddr2" id="extraAddress" class="form-control"/>
						<form:errors path="empAddr2" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">핸드폰번호</td>
					<td>
						<form:input oninput="validateInput(this,11)" path="empPhone" class="form-control"/>
						<form:errors path="empPhone" cssClass="text-danger"/>
					</td>
				</tr>
				
				<tr>
					<td class="tdname">프로필이미지 변경</td>
					<td>
						<input type="file" name="empImage" class="form-control" accept="image/*" id="empImg"/>
						<form:errors path="empImage" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">프로필이미지</td>
					<td class="imgtd">
						<c:if test="${not empty myEmp.base64EmpImg}">
							<img id="my-emp-img" src="data:image/*;base64,${myEmp.base64EmpImg}"/>
							<button type="button" class="btn-close" id="my-emp-del-btn"></button>
						</c:if>
						<c:if test="${empty myEmp.base64EmpImg}">
							<button hidden class="btn-close" id="my-emp-del-btn"></button>
						</c:if>
					</td>
				</tr>
				<tr>
					<td class="tdname">도장이미지 변경</td>
					<td>
						<input type="file" name="empSignimage" class="form-control" accept="image/*" id="signImg"/>
						<form:errors path="empSignimage" cssClass="text-danger"/>
					</td>
				</tr>
				<tr>
					<td class="tdname">도장이미지</td>
					<td class="imgtd">
						<c:if test="${not empty myEmp.base64EmpSignimg}">
							<img id="my-sign-img" src="data:image/*;base64,${myEmp.base64EmpSignimg}"/>
							<button type="button" class="btn-close" id="my-sign-del-btn"></button>
						</c:if>
						<c:if test="${empty myEmp.base64EmpImg}">
							<button hidden class="btn-close" id="my-emp-del-btn"></button>
						</c:if>
					</td>
				</tr>
			</form:form>
			<tr>
				<td class="tdname" colspan="2">
					<button type="button" id="submit-btn" class="btn btn-primary">수정</button>
				</td>
			</tr>
		</table>

		
		<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/app/employee/employeeUpdateForm.js"></script>

	</div>
</body>
</html>