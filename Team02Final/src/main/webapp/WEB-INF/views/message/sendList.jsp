<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
<script src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
<script
	src="${pageContext.request.contextPath }/resources/js/app/message/sendMessage.js"></script>
	
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/lib/dist/themes/default/style.min.css" />
<script>
	const sendCompanyId = "${companyId}";
	const snedContextPath = "${pageContext.request.contextPath}";
</script>

<style>

.bg-label-info {
    color: #2f7989 !important;
}

input[readonly], textarea[readonly] {
	background-color: white !important; /* 배경색을 하얀색으로 설정 */
	color: black; /* 텍스트 색상 설정 */
	border-color: #ced4da; /* 기본 테두리 색상 유지 */
	cursor: default; /* 커서를 기본 상태로 설정 */
}

/* 중요: 빨간 느낌표 */
.bi bi-star-fill {
	color: yellow;
	font-size: 20px;
	font-weight: bold;
}

/* 중요하지 않음: 빈 느낌표 */
.bi bi-star {
	color: grey;
	font-size: 20px;
	font-weight: bold;
}


#sendSearchBtn:hover {
	background-color: #3c40d0; /* 호버 시 배경색 */
	color: white; /* 호버 시 텍스트 색상 */
}

/* 테이블 열 너비 조정 */
#send-table th, #send-table td {
	text-align: center; /* 모든 셀 중앙 정렬 */
	padding: 8px; /* 기본 패딩 */
}

/* 중요 열 크기 */
#send-table th:nth-child(2), #send-table td.importance-icon {
	width: 50px; /* 좁게 설정 */
}

/* 체크박스 열 크기 */
#send-table th:nth-child(1), #send-table td input[type="checkbox"] {
	width: 40px; /* 좁게 설정 */
}

/* 제목 열 크기 */
#send-table th:nth-child(3), #send-table td.clickable-title {
	width: 400px; /* 길게 설정 */
}

/* 받는사람과 발신날짜 크기 동일하게 설정 */
#send-table th:nth-child(4), #send-table td:nth-child(4), #send-table th:nth-child(5),
	#send-table td:nth-child(5) {
	width: 150px; /* 동일한 크기 */
}

/* 제목에 텍스트가 길 경우 텍스트가 넘어가지 않도록 처리 */
#send-table td.clickable-title a {
	display: block;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.search-area {
	text-align: right;
	display: flex; /* Flexbox 레이아웃 사용 */
	justify-content: flex-end; /* 오른쪽 정렬 */
	align-items: center; /* 세로 정렬 중앙 */
	gap: 10px; /* 요소 간 간격 설정 */
}

.search-area .form-control {
	width: auto; /* 기본 너비 제거 */
	margin: 0; /* 불필요한 여백 제거 */
	height: 40px; /* 버튼 높이와 일치 */
	padding: 0 10px; /* 적당한 여백 추가 */
}
</style>
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
<h3 id="page-title">쪽지 발신함</h3>
<hr/>

<div class="search-area" style="text-align: right;">
	<form:select path="condition.searchType" class="form-control" id="form-sned">
		<form:option value="" label="전체" />
		<form:option value="receive" label="수신자" />
		<form:option value="title" label="제목" />
	</form:select>
	<form:input type="path" path="condition.searchWord"	id="searchWordInput" class="form-control" />
	<button type="button" class="search-btn btn btn-primary" id="sendSearchBtn">검색</button>
</div>
<hr />

<table id="send-table" class="table table-hover" style="text-align: left;">
    <thead>
        <tr>
            <th><input type="checkbox" id="selectAll" /></th>
            <th>중요</th>
            <th>제목</th>
            <th>받는사람</th>
            <th>발신날짜</th>
        </tr>
    </thead>
    <tbody>
        <!-- 보낸 쪽지가 없는 경우 -->
        <c:if test="${empty sendList}">
            <tr>
                <td colspan="5">보낸 쪽지가 없습니다.</td>
            </tr>
        </c:if>

        <!-- 보낸 쪽지가 있는 경우 -->
        <c:if test="${not empty sendList}">
            <c:forEach var="send" items="${sendList}">
                <tr>
                    <!-- 체크박스 -->
                    <td><input type="checkbox" class="rowCheckbox" /></td>
                    
                    <!-- 중요 표시 -->
                    <td class="importance-icon">
                        <c:if test="${send.semergencyYn == 'Y'}">
                            <span class="icon yellow-star">⭐</span> <!-- 노란 채워진 별 -->
                        </c:if>
                        <c:if test="${send.semergencyYn != 'Y'}">
                            <i class="bi bi-star"></i>
                        </c:if>
                    </td>

                    <!-- 제목 (최대 15자 표시) -->
                    <td class="clickable-title" data-id="${send.smesId}">
                        <a href="javascript:void(0);">
                            <c:if test="${fn:length(send.smesTitle) > 15}">
                                ${fn:substring(send.smesTitle, 0, 15)}...
                            </c:if>
                            <c:if test="${fn:length(send.smesTitle) <= 15}">
                                ${send.smesTitle}
                            </c:if>
                        </a>
                    </td>

                    <!-- 받는 사람 -->
                    <td>
                        <c:if test="${not empty send.receiverNames}">
                            <c:set var="receivers" value="${fn:split(send.receiverNames, ',')}" />
                            <span class="badge bg-label-info">${receivers[0]}</span>
                            <c:if test="${fn:length(receivers) > 1}">
                                외 ${fn:length(receivers) - 1}명
                            </c:if>
                        </c:if>
                        <c:if test="${empty send.receiverNames}">
                            없음
                        </c:if>
                    </td>

                    <!-- 발신 날짜 -->
                    <td class="date-column">${send.smesDate}</td>
                </tr>
            </c:forEach>
        </c:if>
    </tbody>
</table>



<div style="text-align: right;">
	<button id="messageWrite" class="btn btn-primary"  >쪽지쓰기</button>
	<button type="submit" id="sendMessageDelete" class="btn btn-danger">삭제</button>
</div>

<%-- <c:set var="data1" value="<%=UUID.randomUUID().toString()%>" />
<c:set var="data2" value="<%=UUID.randomUUID().toString()%>" />
<button id="ws-btn" class="control" data-url="${pageContext.request.contextPath }/ws/echo/${data1}/${data2}">웹소켓 연결 테스트</button>
<button id="sockjs-btn" class="control" data-url="${pageContext.request.contextPath }/sockjs/echo/${data1}/${data2}">웹소켓(SockJS) 연결 테스트</button>

<input type="text" id="msg-ipt" /><button id="send-btn">전송</button>
<button id="close-btn" class="control invisible">연결 종료</button> --%>






<div class="paging-area">${pagingHTML}</div>
<form id="searchForm">
	<form:input type="hidden" path="condition.searchType" placeholder="searchType" />
	<form:input type="hidden" path="condition.searchWord" placeholder="searchWord" />
	<input type="hidden" name="page" placeholder="page" />
</form>




<div class="modal fade" id="modalMessageSend" tabindex="-1"
	aria-labelledby="modalScrollableTitle" aria-hidden="true">
	<div class="modal-dialog modal-dialog-scrollable" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalScrollableTitle">
					<i class="bi bi-envelope"></i> 보낸 쪽지
				</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<form id="messageForm">
					<div class="mb-3">
						<label for="semergencyYn" class="form-label">긴급 여부</label> <input
							class="form-check-input" type="checkbox" id="semergencyYnModal"
							name="semergencyYn" disabled="disabled">
					</div>
					<div class="mb-3">
						<label for="smesTitle" class="form-label">쪽지 제목</label> <input
							type="text" class="form-control" id="smesTitleModal"
							name="smesTitle" placeholder="제목" readonly="readonly">
					</div>
					<div class="mb-3">
						<label for="receiverId" class="form-label">수신자</label>
						<div class="input-group"
							style="display: flex; align-items: stretch;">
							<div id="receiverTagsModal"
								class="d-flex flex-wrap align-items-center flex-grow-1"
								style="gap: 5px; background-color: #f8f9fa; border: 1px solid #ced4da; border-radius: 5px; padding: 8px; min-height: 40px;">
								<!-- 선택된 수신자 태그가 여기에 추가됩니다 -->
							</div>
						</div>
					</div>
					<div class="mb-3">
						<label for="smesContent" class="form-label">쪽지 내용</label>
						<textarea class="form-control" id="smesContentModal"
							name="smesContent" rows="4" placeholder="내용을 입력하세요"
							readonly="readonly"></textarea>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>


