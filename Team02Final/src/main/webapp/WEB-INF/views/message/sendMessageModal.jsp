<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<script
	src="${pageContext.request.contextPath }/resources/js/app/message/sendMessageModal.js"></script>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/lib/dist/themes/default/style.min.css" />

<style>
\
/* 중요: 빨간 느낌표 */
.icon.red-exclamation {
	color: red;
	font-size: 20px;
	font-weight: bold;
}

/* 중요하지 않음: 빈 느낌표 */
.icon.grey-exclamation {
	color: grey;
	font-size: 20px;
	font-weight: bold;
}

#search-input {
	width: 70%;
}

.tree-wrapper {
	position: relative; /* 모달 내부에 위치를 설정 */
	max-height: 300px; /* 최대 높이를 제한 */
	overflow-y: auto; /* 내용이 넘칠 경우 세로 스크롤 추가 */
	padding: 10px; /* 내부 패딩 추가 (선택사항) */
	border: 1px solid #dee2e6; /* 테두리 추가 (선택사항) */
	border-radius: 5px; /* 모서리 둥글게 (선택사항) */
	background-color: #fff; /* 배경색 설정 */
}

#secondaryModal .modal-dialog {
	position: absolute; /* 위치를 고정 */
	top: 5%; /* 첫 번째 모달의 위에서 10% */
	left: 5%; /* 왼쪽에서 5% */
	transform: none;
}

#modal-body-second{
	position: relative;
	padding: 20px; /* 기본 패딩 유지 */
	overflow-y: auto; /* 내부 스크롤 활성화 */
}
#modal-body-message{
	position: relative;
	padding: 20px; /* 기본 패딩 유지 */
	overflow-y: auto; /* 내부 스크롤 활성화 */
}
#modal-content-second {
	position: relative;
}
#modal-content-message{
	position: relative;
}

#modal-dialog-div {
	max-width: 500px; /* 모달 너비 제한 */
	margin: auto;
}

#treeDirectory {
	max-height: 400px;
	overflow-y: auto;
	border: 1px solid #ccc;
	padding: 10px;
}
body.modal-open .sidebar {
    pointer-events: none; /* 클릭 방지 */
}
.modal-static {
    pointer-events: none;
    overflow: hidden;
    backdrop-filter: blur(2px);
}
.modal-backdrop {
    z-index: 1040 !important; /* 백드롭의 z-index */
}

.modal {
    z-index: 1050 !important; /* 모달의 z-index */
}

body.modal-open .sidebar {
    pointer-events: none; /* 클릭 방지 */
    opacity: 0.5; /* 비활성화된 시각적 효과 */
    transition: opacity 0.3s ease-in-out;
}

body.modal-open {
    overflow: hidden; /* 스크롤 방지 */
}

/* 추가: 백드롭이 없는 경우에도 사이드바를 잠금 */
body.modal-static {
     pointer-events: none;
    overflow: hidden;
}

</style>    
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>    
<!-- 쪽지 쓰기 Modal -->
<div class="modal fade" id="modalScrollable" tabindex="-1"
	aria-labelledby="modalScrollableTitle" aria-hidden="true"  data-bs-backdrop="static" data-bs-keyboard="false">
	<div class="modal-dialog modal-dialog-scrollable" role="document">
		<div class="modal-content" id="modal-content-message">
			<div class="modal-header">
				<h5 class="modal-title" id="modalScrollableTitle">
					<i class="bi bi-envelope"></i> 쪽지쓰기
				</h5>
				<input type="hidden" id="hiddenCompanyIdMessage" value="${companyId}"/>
				<input type="hidden" id="contextPathHidden" data-url="${pageContext.request.contextPath}/${companyId}/directory/folder"/>
				
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body" id="modal-body-message">
				<form id="messageForm">
					<div class="mb-3">
						<div class="form-check">
							<input class="form-check-input" type="checkbox" id="semergencyYn"
								name="semergencyYn"> <label class="form-check-label"
								for="semergencyYn">긴급 여부</label>
						</div>
					</div>
					<div class="mb-3">
						<label for="smesTitle" class="form-label">쪽지 제목</label> <input
							type="text" class="form-control" id="smesTitle" name="smesTitle"
							placeholder="제목" maxlength="100" required="required">
					</div>
					<div class="mb-3">
					    <label for="receiverId" class="form-label">수신자</label>
					    <div class="d-flex align-items-center" style="gap: 5px;">
					        <!-- 수신자 태그 영역 -->
					        <div id="receiverTags"
					            class="d-flex flex-wrap align-items-center flex-grow-1"
					            style="gap: 5px; background-color: #f8f9fa; border: 1px solid #ced4da; border-radius: 5px; padding: 8px; min-height: 40px; max-width: calc(100% - 50px);">
					            <!-- 선택된 수신자 태그가 여기에 추가됩니다 -->
					        </div>
					
					        <!-- 버튼 -->
					        <button type="button" id="addReceiverButton" class="btn btn-primary"
					            style="align-self: flex-start; width: 40px; height: 50px; display: flex; align-items: center; justify-content: center;">
					            <i class="bi bi-person-plus-fill"></i>
					        </button>
					    </div>
					</div>
					<div class="mb-3">
						<label for="smesContent" class="form-label">쪽지 내용</label>
						<textarea class="form-control" id="smesContent" name="smesContent"
							rows="4" placeholder="내용을 입력하세요" maxlength="666" required="required"></textarea>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" id="sendModal" class="btn btn-primary">전송</button>
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>


<!-- 주소록 모달 -->
<div class="modal fade" id="secondaryModal" tabindex="-1"
	aria-labelledby="secondaryModalLabel" aria-hidden="true"  data-bs-backdrop="static" data-bs-keyboard="false">
	<div class="modal-dialog" id="modal-dialog-div">
		<div class="modal-content" id="modal-content-second">
			<div class="modal-header">
				<h5 class="modal-title" id="secondaryModalLabel">주소록</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body" id="modal-body-second">
				<div class="search-wrapper mb-3">
					<input type="text" class="form-control" id="search-input"
						placeholder="검색어 입력">
					<button class="btn btn-primary" id="search-button" style="height:20px;">검색</button>
				</div>
				<div id="treeDirectory"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="main-recemapping"
					data-bs-dismiss="modal">확인</button>
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal" id="closeBtn">닫기</button>
			</div>
		</div>
	</div>
</div>
<script>

const contextPathSend = "${pageContext.request.contextPath}";

</script>

