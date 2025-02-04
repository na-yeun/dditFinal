<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<script
	src="${pageContext.request.contextPath }/resources/js/app/message/boxMessage.js"></script>

<style>

.bg-label-info {
    color: #2f7989 !important;
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
input[readonly], textarea[readonly] {
	background-color: white !important; /* 배경색을 하얀색으로 설정 */
	color: black; /* 텍스트 색상 설정 */
	border-color: #ced4da; /* 기본 테두리 색상 유지 */
	cursor: default; /* 커서를 기본 상태로 설정 */
}

#sendSearchBtn:hover {
	background-color: #3c40d0; /* 호버 시 배경색 */
	color: white; /* 호버 시 텍스트 색상 */
}

#iconMessage {
	color: #696cff;
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
	display: flex;
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
<h3 id="page-title">쪽지 보관함</h3>
<hr/>
<div class="search-area" style="text-align: right;">
	<form:select path="condition.searchType" class="form-control" >
		<form:option value="" label="전체" />
		<form:option value="send" label="발신자" />
		<form:option value="title" label="제목" />
		<form:option value="content" label="내용" />
	</form:select>
	<form:input type="path" path="condition.searchWord" id="searchWordInput" class="form-control" />
	<button type="button" class="search-btn btn btn-primary" id="sendSearchBtn">검색</button>
</div>
<hr/>
 <table id="box-table" class="table table-hover"  style="text-align: center;">
	<thead>
	<tr>
		<th>
			 <input type="checkbox" id="selectAllBox" />
		</th>
		<th>중요</th>
		<th>제목</th>
		<th>내용</th>
		<th>보낸사람</th>
		<th>수신날짜</th>
		</tr>
	</thead>
	<tbody>
		<c:if test="${empty boxList}">
            <tr>
                <td colspan="6">보관된 쪽지가 없습니다.</td>
            </tr>
        </c:if>
        <c:if test="${not empty boxList}">
		<c:forEach var="box" items="${boxList}">
			<tr  data-id="${box.mboxId}">
				<td>
					 <input type="checkbox" class="rowCheckboxBox" />
				</td>
				
				<td class="importance-icon"><c:choose>
						<c:when test="${box.mboxEmergencyyn == 'Y'}">
							<span class="icon yellow-star">⭐</span> <!-- 노란 채워진 별 -->
						</c:when>
						<c:otherwise>
							<i class="bi bi-star"></i>
						</c:otherwise>
					</c:choose></td>
				<td class="clickable-title-box" 
					data-yn="${box.mboxEmergencyyn}"
					data-title="${box.mboxTitle}"
					data-content="${box.mboxContent}"
					data-sendhuman="${box.employee.empName}"
				>
				    <a href="javascript:void(0);">
				    	 <c:choose>
		                    <c:when test="${fn:length(box.mboxTitle) > 10}">
		                        ${fn:substring(box.mboxTitle, 0, 10)}...
		                    </c:when>
		                    <c:otherwise>
		                        ${box.mboxTitle}
		                    </c:otherwise>
		                </c:choose>
				    
				    </a>
				</td>
				<td>
				<c:choose>
	                <c:when test="${fn:length(box.mboxContent) > 10}">
	                    ${fn:substring(box.mboxContent, 0, 10)}...
	                </c:when>
	                <c:otherwise>
	                    ${box.mboxContent}
	                </c:otherwise>
	            </c:choose>
				</td>
				<td>
					<span class="badge bg-label-info">${box.employee.empName}</span>
				</td>
				<td class="date-column">${box.mbsendDate}</td>
			</tr>
			
			
		</c:forEach>
		</c:if>
	</tbody>

</table>

<div style="text-align: right;">
	<button id="messageWrite" class="btn btn-primary">쪽지쓰기</button>
	<button type="submit" class="btn btn-danger" id="boxMessageBtn">삭제</button>
</div>

<div class="paging-area">${pagingHTML}</div>
<form id="searchForm">
	<form:input type ="hidden" path="condition.searchType" placeholder="searchType" />
	<form:input type ="hidden" path="condition.searchWord" placeholder="searchWord" />
	<input type ="hidden" name="page" placeholder="page" />
</form> 

<div class="modal fade" id="modalMessageBox" tabindex="-1" aria-labelledby="modalScrollableTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalScrollableTitle">
                    <i class="bi bi-envelope"></i> 받은 쪽지
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="messageForm">
                    <div class="mb-3">
                        <label for="mboxEmergencyyn" class="form-label">긴급 여부</label>
                        <input class="form-check-input" type="checkbox" id="mboxEmergencyynModal" name="mboxEmergencyyn" disabled>
                    </div>
                    <div class="mb-3">
                        <label for="mboxTitle" class="form-label">제목</label>
                        <input type="text" class="form-control" id="mboxTitleModal" name="mboxTitle"  readonly="readonly">
                    </div>
                    <div class="mb-3">
                        <label for="mbsendId" class="form-label">발신자</label>
                         <input type="text" class="form-control" id="mbsendIdModal" name="mbsendId" readonly="readonly">
                        
                    </div>
                    <div class="mb-3">
                        <label for="mboxContent" class="form-label">수신내용</label>
                        <textarea class="form-control" id="mboxContentModal" name="mboxContent" rows="4"  readonly="readonly"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div> 
