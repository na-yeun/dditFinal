<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<script>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal"/>
		<c:set value="${principal.account.empName }" var="empName"></c:set>                        
		<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
		<c:set value="${principal.account.empId}"  var="empId"></c:set>
		var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
	</security:authorize>
</script>

<h4 id="page-title">문의 게시글 작성</h4>
<hr/>
	<div class="table-responsive">
	<form:form method="post" enctype="multipart/form-data" modelAttribute="newQuestion" id="question-form">
		<table class="table table-borderless">
			<tr>
				<td colspan="2">
					<div class="mb-4">
					  <label for="exampleFormControlInput1" class="form-label">제목</label>
					  <form:input type="text" path="quTitle" cssClass="form-control" placeholder="제목을 입력하세요"/>
					  <form:errors path="quTitle" element="span" cssClass="text-danger"/>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="mb-4">
					  <label for="exampleFormControlSelect1" class="form-label">문의 카테고리 선택</label>
					  <form:select path="goryId" class="form-select" id="exampleFormControlSelect1" aria-label="Default select example">
						<option value="" label="문의하실 내용의 주제를 선택해 주세요.">
						<c:forEach items="${categoryList}" var="category">
							<option value="${category.goryId}">${category.goryNm }</option>
						</c:forEach>
					  </form:select>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="mb-3">
					  <label for="formFileMultiple" class="form-label">첨부파일</label>
					  <input name="uploadFiles" class="form-control" type="file" multiple/>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="form-check form-switch">
					  <label class="form-check-label" for="questionSecretynCheckbox">문의 게시글 비공개 여부</label>
					  <input id="questionSecretynCheckbox" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault">
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
	                <!-- 숨겨둔 textarea (서버로 전송할 값) -->
	                <form:textarea path="quContent" cssClass="form-control"
	                               id="quContent" style="display:none;"/>
					<form:errors path="quContent" element="span" cssClass="text-danger"/>
	
	                <!-- Toast UI Editor가 표시될 영역 -->
	                <div id="editor"></div>
				</td>
			</tr>
			<tr>
	            <td colspan="2">
                <form:input type="hidden" path="quSecretyn" id="questionSecretynHidden" value="N" />
	            </td>
	        </tr>
			<tr>
				<td style="text-align: right;">
				    <button type="button" class="btn btn-outline-dark btn-sm" id="dataInputBtn">데이터삽입</button>
					<input type="submit" value="등록" class="btn btn-primary" />
					<button type="button" class="btn btn-secondary" id="goListBtn">목록</button>
				</td>
			</tr>
		</table>
	</form:form>
	</div>
<%-- 에디터안에 이미지를 삽입시 자동적으로 이벤트가 일어나는 비동기 스크립트--%>
<script>
    var uploadImageUrl = '${pageContext.request.contextPath}/${companyId}/question/atch/uploadImage';

    const checkbox = document.getElementById("questionSecretynCheckbox");
    const hiddenInput = document.getElementById("questionSecretynHidden");

    checkbox.addEventListener("change", () => {
        hiddenInput.value = checkbox.checked ? "Y" : "N";
    });    
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/question/questionForm.js"></script>