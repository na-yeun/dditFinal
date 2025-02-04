<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/survey/surveyList.css"/>

<h4 id="page-title">설문조사</h4>
<hr/>
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.departCode}" var="departCode"></c:set>
</security:authorize>
<table class="table">
	<thead>
		<tr>
			<th colspan="5">
				<div class="search-area">
					<form:select path="condition.searchType" cssClass="form-control" id="searchType">
						<form:option value="" label="전체"/>
						<form:option value="subject" label="제목" />
						<form:option value="content" label="내용" />
					</form:select>
					<form:input path="condition.searchWord" cssClass="form-control" id="searchWord"/>
					<button type="button" id="search-btn" class="search-btn btn btn-primary">검색</button>
				</div>
			</th>
		</tr>
		<tr>
			<th>순번</th>
			<th>제목</th>
			<th>대상</th>
			<th>시작일</th>
			<th>마감일</th>
		</tr>
	</thead>
	<tbody>
		<!-- 게시물이 없을 때 -->
		<c:if test="${empty list}">
			<tr>
				<td colspan="5">게시글 없음</td>
			</tr>
		</c:if>
		
		<!-- 게시물이 있을 때 -->
		<c:if test="${not empty list}">
			<c:forEach items="${list}" var="survey">
				<!-- 설문조사 대상이 전체 or 나의 부서와 동일 or 로그인한 계정이 관리자라면 조회가능(true)-->
				<c:set value="${(
								(survey.surboardTargetName eq '전체')
								or
								(survey.surboardTarget eq departCode)
								or
								(posiId eq 6)
								or
								(posiId eq 7)
								)?true:false}" var="availability"></c:set>
				
				<tr class="survey-list" data-survey-id="${survey.sboardNo}" data-survey-target="${availability}">
					<td><c:out value="${survey.rnum}"/></td>
					<td class="survey-subject">
						<c:if test="${survey.surboardYn eq 'Y'}">
							<!-- 마감 일자 급박하면 빨간색으로 진행중, 3일 이상 남았으면 파란색으로 진행중 -->
							
							
							<!-- 전체이거나 나의 부서와 동일하면 참여가능(그런데 관리자 계정은 참여가 불가능함) -->
							<c:if test="${(posiId != 6) and (posiId != 7)}">
								<c:if test="${availability eq true}">
									<c:if test="${survey.rowExists gt 0}">
										<span class="badge rounded-pill bg-info">참여완료</span>
									</c:if>
									<c:if test="${survey.rowExists eq 0}">
										<c:if test="${not empty survey.endDateWarning}">
											<span class="badge rounded-pill bg-danger sc-detail-icon" data-start="ing">진행중</span>
										</c:if>
										<c:if test="${empty survey.endDateWarning}">
											<span class="badge rounded-pill bg-primary sc-detail-icon" data-start="ing">진행중</span>
										</c:if>
										<span class="badge rounded-pill bg-success">참여가능</span>
									</c:if>
									
								</c:if>
								<!-- 전체가 아니고 나의 부서와 동일하지 않으면 참여불가능 -->
								<c:if test="${availability eq false}">
									<span class="badge rounded-pill bg-dark">참여불가</span>
								</c:if>
							</c:if>
							
							<b><c:out value="${survey.surboardNm}"/></b>
						</c:if>
						
						<c:if test="${survey.surboardYn eq 'N'}">
							<!-- 아직 시작 이전일 때 -->
							<c:if test="${not empty survey.startDateCheck}">
								<span class="badge rounded-pill bg-warning sc-detail-icon" data-start="before">진행전</span>
							</c:if>
						
							<!-- 종료일 때 -->
							<c:if test="${empty survey.startDateCheck}">
								<span class="badge rounded-pill bg-secondary sc-detail-icon" data-start="end">종료</span>
							</c:if>
							
							<c:out value="${survey.surboardNm}"/>
							
						</c:if>
					</td>
					<td><c:out value="${survey.surboardTargetName}"/></td>
					<td><c:out value="${survey.surboardStdate}"/></td>
					<td><c:out value="${survey.surboardEnddate}"/></td>
				</tr>
			</c:forEach>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">
				<div class="paging-area">
					${pagingHtml}
				</div>
			</td>
		</tr>
		<!-- 팀장, 부장, 대표, 관리자 계정의 경우 등록버튼 활성화 -->
		<c:if test="${(posiId eq 3) or (posiId eq 4) or (posiId eq 6) or (posiId eq 7)}">
			<tr>
				<td colspan="5">
					<div class="insert-btn-area">
						<button id="survey-insert" onclick="getInsertForm(${posiId})" type="button" class="btn btn-primary">등록</button>
					</div>
				</td>
			</tr>
		</c:if>
	</tfoot>
</table>
<form id="searchForm">
	<form:input type="hidden" path="condition.searchType" placeholder="searchType" />
	<form:input type="hidden" path="condition.searchWord" placeholder="searchWord"/>
	<input type="hidden" name="page" placeholder="page"/>
</form>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/survey/surveyList.js"></script>
