<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/employee/employeeAttendancePage.css">
<h4 id="page-title">나의 근태</h4>
<hr/>

<c:if test="${not empty startDate}">
	<c:set var="formattedStartDate" value="${startDate.substring(0, 4)}-${startDate.substring(4, 6)}-${startDate.substring(6, 8)}" />				
</c:if>
<c:if test="${not empty endDate}">
    <c:set var="formattedEndDate" value="${endDate.substring(0, 4)}-${endDate.substring(4, 6)}-${endDate.substring(6, 8)}" />
</c:if>

			
<div id="searchArea" class="search-area">
	조회기간 <input type="date" id="start-date" name="startDate" class="form-control" value="${formattedStartDate}"/>
	~ <input type="date" id="end-date" name="endDate" class="form-control" value="${formattedEndDate}"/>
	
	<button type="button" class="search-btn btn btn-outline-primary" id="weekly-btn">주간</button>
	<button type="button" class="search-btn btn btn-outline-primary" id="monthly-btn">월간</button>
	<button type="button" class="search-btn btn btn-outline-primary" id="year-btn">연간</button>
	
	<button type="button" class="search-btn btn btn-primary" id="search-btn">검색</button>
</div>
			

<table class="table table-sm" id="vacationDetailTable">
	<thead>
		<tr>
			<th>순번</th>
			<th>날짜</th>
			<th>출근시간</th>
			<th>퇴근시간</th>
			<th>지각사유</th>
			<th>초과근무여부</th>
			<th>초과근무시간(분)</th>
			<th>출근상태</th>
			<th>퇴근상태</th>
		</tr>
	</thead>
	
	<tbody>
		<c:if test="${not empty list}">
			<c:forEach items="${list}" var="history">
				<c:set var="formattedAtthisId" value="${history.atthisId.substring(0, 4)}-${history.atthisId.substring(4, 6)}-${history.atthisId.substring(6, 8)}" />
				<c:set var="formattedHahisTime" value="${history.hahisTime.substring(11, 16)}" />
				<c:set var="formattedHleaveTime" value="${history.hleaveTime.substring(11, 16)}" />
				<tr>
					<td>${history.rnum}</td>
					<td>${formattedAtthisId}</td>
					<td>${formattedHahisTime}</td>
					<td>${formattedHleaveTime}</td>
					<td>${history.atthisCause }</td>
					<td>${history.atthisOverYn}</td>
					<td>${history.atthisOver}</td>
					
					<c:if test="${history.attstaIdIn eq 1}">
						<td> <p class="badge bg-primary">${history.inStatusName}</p></td>
					</c:if>
					<c:if test="${history.attstaIdIn eq 2}">
						<td> <p class="badge bg-warning">${history.inStatusName}</p></td>
					</c:if>
					<c:if test="${history.attstaIdIn eq 3}">
						<td> <p class="badge bg-danger">${history.inStatusName}</p></td>
					</c:if>
					<c:if test="${history.attstaIdIn eq 4}">
						<td> <p class="badge bg-secondary">${history.inStatusName}</p></td>
					</c:if>
					<c:if test="${history.attstaIdIn eq 5}">
						<td> <p class="badge bg-secondary">${history.inStatusName}</p></td>
					</c:if>
					<c:if test="${history.attstaIdIn eq 6}">
						<td> <p class="badge bg-primary">${history.inStatusName}</p></td>
					</c:if>
					
					<c:if test="${empty history.attstaIdOut}">
						<td> </td>
					</c:if>
					
					<c:if test="${history.attstaIdOut eq 7}">
						<td> <p class="badge bg-secondary">${history.outStatusName}</p></td>
					</c:if>
					
					<c:if test="${history.attstaIdOut eq 8}">
						<td> <p class="badge bg-primary">${history.outStatusName}</p></td>
					</c:if>
					
					<c:if test="${history.attstaIdOut eq 9}">
						<td> <p class="badge bg-success">${history.outStatusName}</p></td>
					</c:if>
					
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${empty list}">
			<tr>
				<td colspan="9">조회할 내역 없음</td>
			</tr>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">
				<div class="paging-area">
					${pagingHtml}
				</div>
			</td>
		</tr>
	</tfoot>
	

</table>

<form id="searchForm">
	<input type="hidden" name="startDate" value="${startDate}"/>
	<input type="hidden" name="endDate" value="${endDate}"/>
	<input type="hidden" name="page" />
</form>




<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/employee/employeeVacationPage.js">
</script>
