<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/vacation/hrVacation.css">
<h4 id="page-title">휴가 이력</h4>
<hr/>

<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
</security:authorize>

<c:if test="${not empty startDate}">
	<c:set var="formattedStartDate" value="${startDate.substring(0, 4)}-${startDate.substring(4, 6)}-${startDate.substring(6, 8)}" />				
</c:if>
<c:if test="${not empty endDate}">
    <c:set var="formattedEndDate" value="${endDate.substring(0, 4)}-${endDate.substring(4, 6)}-${endDate.substring(6, 8)}" />
</c:if>

<div>
	<div id="searchArea" class="search-area">
		<button type="button" class="search-btn btn btn-outline-primary" id="reload-btn" >검색 초기화</button>
		기간 <input type="date" id="start-date" name="startDate" class="form-control" value="${formattedStartDate}"/>
		~ <input type="date" id="end-date" name="endDate" class="form-control" value="${formattedEndDate}"/>
		
		<button type="button" class="search-btn btn btn-outline-primary" id="weekly-btn">주간</button>
		<button type="button" class="search-btn btn btn-outline-primary" id="monthly-btn">월간</button>
		<button type="button" class="search-btn btn btn-outline-primary" id="year-btn">연간</button>
		<input type="text" class="form-control" id="searchWord" name="searchWord" placeholder="사원명을 입력하세요" class="form-controller"/>
		<button type="button" class="search-btn btn btn-primary" id="search-btn">검색</button>
	</div>
	
	<table class="table table-sm" id="vacationDetailTable">
		<thead>
			<tr>
				<th>순번</th>
				<th>부서</th>
				<th>직급</th>
				<th>사원명</th>
				<th>기간</th>
				<th>종류</th>
				<th>결재번호</th>
			</tr>
		</thead>
		
		<tbody>
			<c:if test="${not empty list}">
				<c:forEach items="${list}" var="history">
				<tr>
					<td>${history.rnum}</td>
					<td>${history.departName }</td>
					<td>${history.posiName }</td>
					<td>${history.empName }</td>
					<td>${history.vacStartdate.substring(0, 4)}-${history.vacStartdate.substring(4, 6)}-${history.vacStartdate.substring(6, 8)} 
						~ ${history.vacEnddate.substring(0, 4)}-${history.vacEnddate.substring(4, 6)}-${history.vacEnddate.substring(6, 8)}</td>

					<td>${history.vacCode}</td>
					<td>
						<c:if test="${not empty history.vacDocId}">
							<a href="${pageContext.request.contextPath}/${principal.account.companyId}/approval/detail/${history.vacDocId}"
							   class="text-primary"
							   style="text-decoration: none;">
									${history.vacDocId}
							</a>
						</c:if>
						<c:if test="${empty history.vacDocId}">
							-
						</c:if>
					</td>
				</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty list}">
				<tr>
					<td colspan="7">조회할 내역 없음</td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="7">
					<div class="paging-area">
						${pagingHtml}
					</div>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<form id="searchForm">
		<input type="hidden" name="searchWord" value="${searchWord }"/>
		<input type="hidden" name="startDate" value="${startDate}"/>
		<input type="hidden" name="endDate" value="${endDate}"/>
		<input type="hidden" name="page" />
	</form>




<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/vacation/hrVacation.js">
</script>


</div>

