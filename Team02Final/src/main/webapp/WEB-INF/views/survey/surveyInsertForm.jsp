<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/survey/surveyInsertForm.css">

<!DOCTYPE html>
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.departCode}" var="departCode"></c:set>
	<c:set value="${principal.account.empId}" var="empId"></c:set>
</security:authorize>

<h4 id="page-title">설문조사 추가</h4>
<hr/>
<div style="text-align: right;">
	<button type="button" class="btn btn-outline-dark btn-sm" id="insert-pre-btn">데이터삽입</button>
</div>

<form id="survey-form" data-emp-id="${empId}">
<div class="form-group">
	<p class="title">제목</p>
	<input type="text" id="surboardNm" class="form-control" required="required" placeholder="ex_ 회사 만족도 조사를 진행합니다.(최대 300자)" maxlength="1000"/>		
</div>

<div id="form-group-date">
	<div class="form-group">
		<p class="title">시작일</p>
		<input type="date" id="surboardStdate" class="form-control date-form" required="required"/>		
	</div>
	
	<div class="form-group">
		<p class="title">종료일</p>
		<input type="date" id="surboardEnddate" class="form-control date-form" required="required"/>
	</div>
</div>

<div class="form-group">
	<p class="title">추가설명</p>
	<textarea id="surboardContent" class="form-control" placeholder="설문조사에 대한 설명을 기재해주세요."></textarea>
</div>

<span class="q-info">질문의 순서를 바꾸려면 드래그 해서 위치를 수정해주세요.</span>

<div class="q-drag-area">
	<div class="q-area" draggable="true">
		<p class="question">
			질문 <span class="q-content-value"></span>
			<span class="collapse-icon-area">
				<svg class="collapse-icon bf-collapse" xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-arrows-collapse" viewBox="0 0 16 16">
				  <path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h13a.5.5 0 0 1 0 1h-13A.5.5 0 0 1 1 8m7-8a.5.5 0 0 1 .5.5v3.793l1.146-1.147a.5.5 0 0 1 .708.708l-2 2a.5.5 0 0 1-.708 0l-2-2a.5.5 0 1 1 .708-.708L7.5 4.293V.5A.5.5 0 0 1 8 0m-.5 11.707-1.146 1.147a.5.5 0 0 1-.708-.708l2-2a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1-.708.708L8.5 11.707V15.5a.5.5 0 0 1-1 0z"/>
				</svg>
				
				
				<svg class="collapse-icon af-collapse hidden" xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-arrows-expand" viewBox="0 0 16 16">
				  <path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h13a.5.5 0 0 1 0 1h-13A.5.5 0 0 1 1 8M7.646.146a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1-.708.708L8.5 1.707V5.5a.5.5 0 0 1-1 0V1.707L6.354 2.854a.5.5 0 1 1-.708-.708zM8 10a.5.5 0 0 1 .5.5v3.793l1.146-1.147a.5.5 0 0 1 .708.708l-2 2a.5.5 0 0 1-.708 0l-2-2a.5.5 0 0 1 .708-.708L7.5 14.293V10.5A.5.5 0 0 1 8 10"/>
				</svg>
			</span>
		</p>
		
		<p class="question-title">질문의 종류를 선택해주세요.</p>
		<select class="form-select mb-0 select-categoty" required="required">
			<option value>--------종류--------</option>
			<option value="S_MULTI">객관형</option>
			<option value="S_SUBJ">서술형</option>
		</select>
	</div>
</div>


<div id="last-fix">
	<div class="btn-area">
		<button type="submit" class="btn btn-primary" id="submit-btn">등록</button>
		<button type="button" class="btn btn-secondary" id="cancel-btn">취소</button>
	</div>
</div>

</form>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/survey/surveyInsertUpdateForm.js"></script>