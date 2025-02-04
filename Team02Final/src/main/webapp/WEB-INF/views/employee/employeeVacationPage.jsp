<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<style>
#nowcount{
	color: red;
}

th{
	text-align: center;
}

td{
	text-align: center;
}


#searchArea {
    display: flex;
    gap: 10px;
    align-items: center;
    justify-content: flex-end;
    padding-top: 10px;
    padding-bottom: 10px;
}

#searchDateForm {
    display: flex;
    align-items: center;
    gap: 10px;
}

.form-control {
    width: auto; /* input 필드의 너비 조정 */
}

#vacationStatusTable{
	border: 1px solid gray;
}



</style>
<h4 id="page-title">나의 휴가</h4>
<hr/>

<c:if test="${not empty startDate}">
	<c:set var="formattedStartDate" value="${startDate.substring(0, 4)}-${startDate.substring(4, 6)}-${startDate.substring(6, 8)}" />				
</c:if>
<c:if test="${not empty endDate}">
    <c:set var="formattedEndDate" value="${endDate.substring(0, 4)}-${endDate.substring(4, 6)}-${endDate.substring(6, 8)}" />
</c:if>

<div>
	<h5>휴가 현황</h5>
	<table class="table table-sm" id="vacationStatusTable">
		<thead>	
			<tr>
				<th>연도</th>	
				<th>부여개수</th>	
				<th>추가개수</th>	
				<th>사용개수</th>	
				<th>현재개수</th>	
				<th>병가사용개수</th>	
			</tr>
		</thead>
		
		
		<tbody>
			<tr>
				<td>${vacationStatus.vstaCode}</td>
				<td>${vacationStatus.vstaAllcount}</td>
				<td>${vacationStatus.vstaAppend}</td>
				<td>${vacationStatus.vstaUse}</td>
				<td id="nowcount">${vacationStatus.vstaNowcount}</td>
				<td>${vacationStatus.vstaSickcount}</td>
			</tr>
		</tbody>
	
	</table>
	<hr/>

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
					<td>${history.vacStartdate} ~ ${history.vacEnddate}</td>
					<td>${history.vacName}</td>
					<td>${history.vacDocId}</td>
				</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty list}">
				<tr>
					<td colspan="6">조회할 내역 없음</td>
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


</div>