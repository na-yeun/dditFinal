<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/survey/surveyInsertForm.css">
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.departCode}" var="departCode"></c:set>
	<c:set value="${principal.account.empId}" var="empId"></c:set>
</security:authorize>

<h4 id="page-title">설문조사 수정</h4>
<hr/>

<form id="survey-form" data-sboard-no="${detail.sboardNo}">
<div class="form-group">
	<p class="title">제목</p>
	<input type="text" id="surboardNm" class="form-control" required="required" value="${detail.surboardNm}"/>		
</div>

<div id="form-group-date">
	<div class="form-group">
		<p class="title">시작일</p>
		<input type="date" id="surboardStdate" class="form-control date-form" required="required" value="${detail.surboardStdate}"/>
	</div>
	
	<div class="form-group">
		<p class="title">종료일</p>
		<input type="date" id="surboardEnddate" class="form-control date-form" required="required" value="${detail.surboardEnddate}"/>
	</div>
</div>

<div class="form-group">
	<p class="title">추가설명</p>
	<textarea id="surboardContent" class="form-control" placeholder="ex_ 회사 만족도 조사를 진행합니다.">${detail.surboardContent}</textarea>
</div>

<span class="q-info">질문의 순서를 바꾸려면 드래그 해서 위치를 수정해주세요.</span>

<c:forEach items="${detail.surveyQuestionList}" var="questions" varStatus="num">
	<div class="q-drag-area">
		<div class="q-area" draggable="true" data-surques-no="${questions.surquesNo}">
			<p class="question">
				질문 <span class="q-content-value">: ${questions.surquesContent}</span>
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
				<c:choose>
		            <c:when test="${questions.surquesType eq 'S_MULTI'}">
		                <option value="S_MULTI" selected>객관형</option>
		                <option value="S_SUBJ">서술형</option>
		            </c:when>
		            <c:when test="${questions.surquesType eq 'S_SUBJ'}">
		                <option value="S_MULTI">객관형</option>
		                <option value="S_SUBJ" selected>서술형</option>
		            </c:when>
		            <c:otherwise>
		                <option value="S_MULTI">객관형</option>
		            	<option value="S_SUBJ">서술형</option>
		            </c:otherwise>
	        	</c:choose>
			</select>
		
			<c:if test="${questions.surquesType eq 'S_MULTI' }">
				<div class="question-area">
					<p class="question-title">질문내용을 입력해주세요.</p>
					<input type="text" class="form-control q-content" placeholder="ex_ 근무연차를 선택해주세요." required="required" value="${questions.surquesContent}"/>
					<p class="question-title">중복 선택이 가능한가요?</p>
					<c:choose>
			            <c:when test="${questions.surquesDupleyn eq 'Y'}">
			                <input type="radio" name="dupleyn-${num.count}" value="Y" class="form-check-input duple" checked/>예
			                <input type="radio" name="dupleyn-${num.count}" value="N" class="form-check-input duple dupleyn-check"/>아니오
			            </c:when>
			            <c:when test="${questions.surquesDupleyn eq 'N'}">
			                <input type="radio" name="dupleyn-${num.count}" value="Y" class="form-check-input duple"/>예
			                <input type="radio" name="dupleyn-${num.count}" value="N" class="form-check-input duple dupleyn-check" checked/>아니오
			            </c:when>
			            <c:otherwise>
			                <input type="radio" name="dupleyn-${num.count}" value="Y" class="form-check-input duple"/>예
			                <input type="radio" name="dupleyn-${num.count}" value="N" class="form-check-input duple dupleyn-check" checked/>아니오
			            </c:otherwise>
		        	</c:choose>
				</div>
				<c:forEach items="${questions.surveyItemList}" var="items" varStatus="itemsStatus">
					<div class="item" data-suritem-no="${items.suritemNo}">
						<p class="question-title">질문의 항목을 입력해주세요. <span class="form-info">항목은 추가된 순서대로 왼쪽부터 화면에 출력됩니다.</span></p>
						<input type="text" class="form-control qitem" placeholder="ex_매우만족" required="required" value="${items.suritemContent}"/>
					</div>
					<div class="question-btn-area">
						<c:if test="${!itemsStatus.last}">
							<button type="button" class="btn btn-warning btn-sm delete-item">항목삭제</button>
					 	</c:if>
						<c:if test="${itemsStatus.last}">
				            <button type="button" class="btn btn-primary btn-sm plus-item">항목추가</button>
				            <button type="button" class="btn btn-success btn-sm plus-question">질문추가</button>
				            <button type="button" class="btn btn-danger btn-sm delete-question">질문삭제</button>
				        </c:if>
					</div>
				</c:forEach>
			</c:if>
	
	
			<c:if test="${questions.surquesType eq 'S_SUBJ'}">
				<div class="question-area">
					<p class="question-title">질문내용을 입력해주세요.</p>
					<input type="text" class="form-control q-content" placeholder="ex_ 근무연차를 선택해주세요." value="${questions.surquesContent}"/>
						<c:forEach items="${questions.surveyItemList}" var="subjItem">
							<div class="item" data-suritem-no="${subjItem.suritemNo}">
								<input type="hidden" class="form-control qitem" value="자유롭게 입력해주세요."/>
							</div>
						</c:forEach>
				</div>
				<div class="question-btn-area">
					<button type="button" class="btn btn-success btn-sm plus-question">질문추가</button>
					<button type="button" class="btn btn-danger btn-sm delete-question">질문삭제</button>
				</div>
			</c:if>
		</div>
	</div>
</c:forEach>



<div id="last-fix">
	<div class="btn-area">
		<button type="button" class="btn btn-primary" id="update-btn">수정</button>
		<button type="button" class="btn btn-secondary" id="cancel-btn">취소</button>
	</div>
</div>

</form>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/survey/surveyInsertUpdateForm.js"></script>