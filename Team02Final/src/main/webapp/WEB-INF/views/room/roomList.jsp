<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
	
<script
	src="${pageContext.request.contextPath }/resources/js/app/room/roomReser.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/resources/css/room/roomList.css">
<style>
 .swal2-container {
    z-index: 1500 !important; 
  }
</style>

<%-- <button type="button"class="btn rounded-pill me-2 btn-primary" data-url="${pageContext.request.contextPath}/roomTime/{roomId}/${myEmp.empId}">나의 예약 조회</button> --%>

<!-- 이건 시설 리스트 -->
<!-- 하나하나의 시설을 삭제할 수 있어야해서 이름이 deleteForm -->
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.companyId }" var="companyId"></c:set>   
</security:authorize>

<h3 id="page-title">시설 예약</h3>
<hr />
	<div class="search-area">
		
	<%-- 	<form:select path="condition.searchType" class="form-select">
			<form:option value="" label="전체" />
			<form:option value="gory" label="방종류" />
			
		</form:select> --%>
		<form:select path="condition.searchWord" id="select-searchWord" class="form-select">
			<form:option value="" label="전체" />
			<form:option value="1" label="회의실" />
			<form:option value="2" label="미팅룸" />
			<form:option value="3" label="휴게실" />
			<form:option value="4" label="기타" /> 
		</form:select>
</div>

	
		<c:if test="${posiId == '7'}">
		<div class="room-foot">
			<button type="button" id="btn-insert"
				class="btn btn-primary">등록하기</button>
		</div>
		</c:if>
		<div class="room-container">
		<c:forEach var="room" items="${roomList}">
			<div class="room-card">
				<img src="data:image/*;base64,${room.base64Img}" class="room-image" />
				<br>
				<div class="room-info">
					<h3 style="font-weight: bold;">
						<c:url value="/${companyId}/roomTime/${room.roomId}"
							var="detailUrl"></c:url>
						<c:choose>
						    <c:when test="${room.roomYn == '1'}">
						        <a href="${detailUrl}">${room.roomName}</a>
						    </c:when>
						    <c:otherwise>
						        <a href="javascript:void(0);" 
						           style="color: gray;  cursor: not-allowed;" 
						           onclick="return false;">${room.roomName}</a>
						    </c:otherwise>
						</c:choose>
					</h3>
					<p>호수: ${room.roomHosu}</p>
					<p>상세: 
					<c:choose>
					    <c:when test="${fn:length(room.roomDetail) > 17}">
					        ${fn:substring(room.roomDetail, 0, 17)}...
					    </c:when>
					    <c:otherwise>
					         ${room.roomDetail}
					    </c:otherwise>
					</c:choose>
					</p>
					<p class="status-p">
						이용 가능 여부:
						<c:choose>
							<c:when test="${room.roomYn == '1'}">
								<span class="status-available">예약 가능</span>
							</c:when>
							<c:when test="${room.roomYn == '2'}">
								<span class="status-unavailable">점검 및 유지보수중</span>
							</c:when>
							<c:when test="${room.roomYn == '3'}">
								<span class="status-unavailable">설비 고장</span>
							</c:when>
							<c:when test="${room.roomYn == '4'}">
								<span class="status-unavailable">전력 및 통신 문제</span>
							</c:when>
							<c:when test="${room.roomYn == '5'}">
								<span class="status-unavailable">청소 및 준비 시간</span>
							</c:when>
							<c:when test="${room.roomYn == '6'}">
								<span class="status-unavailable">회사 행사</span>
							</c:when>
							<c:when test="${room.roomYn == '7'}">
								<span class="status-unavailable">시설 임대</span>
							</c:when>
							<c:otherwise>
								<span class="status-unknown">알 수 없음</span>
							</c:otherwise>
						</c:choose>
					</p>

				</div>
				<!-- 예약 버튼 -->
				
				
					<c:if test="${posiId == '7'}">
				<div class="room-update">
					<button type="button" id="btn-update" class=""
						data-url="${pageContext.request.contextPath}/${companyId}/room/${room.roomId}/edit">관리</button>
				</div>
				</c:if>
				<div class="room-actions">


					<button type="button" class="reserve-button"
						data-url="${pageContext.request.contextPath}/${companyId}/roomTime/${room.roomId}"
						<c:if test="${room.roomYn != '1'}">disabled</c:if>>
						예약하기</button>

					<button type="button" id="status-button" data-bs-toggle="modal"
						data-bs-target="#exLargeModal" data-room-id="${room.roomId}"
						<c:if test="${room.roomYn != '1'}">disabled</c:if>>
						예약현황</button>
					<security:authorize access="isAuthenticated()">
						<security:authentication property="principal" var="principal" />
						<c:set value="${principal.account.empId}" var="empId"></c:set>
					</security:authorize>
					<input type="hidden" id="sessionEmpId" data-em-id="${empId}">
					<input type="hidden" id="sessionPosiId" data-po-id="${posiId}">
				</div>
			</div>
</c:forEach>
</div>


<div class="paging-area">${pagingHTML}</div>

<form id="searchForm">
	<form:input type="hidden" path="condition.searchType" placeholder="searchType"/>
	<form:input type="hidden" path="condition.searchWord" placeholder="searchWord"/>
	<input type="hidden" name="page" placeholder="page" />
</form>


<!-- 나의 예약 조회 Modal -->
<div class="modal fade" id="exLargeModal" tabindex="-1"
	aria-hidden="true">
	<div class="modal-dialog modal-xl" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalLabel4"
					style="font-weight: bold;">예약 현황</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div id="modalContent"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary"
					data-bs-dismiss="modal">닫기</button>

			</div>
		</div>
	</div>
</div>


<%--  <%@ include file="/WEB-INF/views/message/sendMessageModal.jsp" %>  --%>

