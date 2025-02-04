<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>


<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/fullcalendar@5.8.0/main.min.css" />
<script src="${pageContext.request.contextPath }/resources/js/app/schedule/scheduleCalendar.js"></script>
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>



	
	
	
<script>

const contextPathSchedule = "${pageContext.request.contextPath}";
const companyIdS = "${companyId}";
</script>



<style>
#calendarMain .fc-event {
    border: none; /* 테두리 제거 */
    border-radius: 6px; /* 막대기 끝 모서리를 둥글게 */
    padding: 5px;
    font-size: 9px;
    font-weight: 300;
    transition: background-color 0.3s ease, transform 0.2s ease;
    overflow: hidden; /* 내용이 넘치면 숨김 */
    text-overflow: ellipsis; /* 말줄임 표시 */
    white-space: nowrap; /* 한 줄로 표시 */
    position: relative;
}

#calendarMain .fc-event:hover {
    background-color: #1d4ed8; /* 호버 시 배경색 */
    transform: scale(1.03); /* 호버 시 확대 */
    overflow: visible; /* 숨겨진 콘텐츠 표시 */
    white-space: normal; /* 텍스트 줄바꿈 허용 */
    z-index: 1000; /* 다른 요소 위로 띄우기 */
}


#colorBtnSchedule {
    float: right; /* 오른쪽 정렬 */
    bottom: 20px;    /* 화면 하단에서 20px 위로 */
    right: 20px;     /* 화면 오른쪽에서 20px 왼쪽으로 */
    padding: 10px 20px; /* 버튼 크기 조절 */
    cursor: pointer;  /* 커서 변경 */
    margin-bottom: 15px;
}


/* 부모 요소를 오른쪽 정렬 */
#calendar {
	display: flex;
	justify-content: flex-end; /* 오른쪽 정렬 */
	/* margin-top: 5px; */
	margin-bottom: 30px;
	margin-left: 5%;
	margin-right: 5%;
}

/* 버튼 그룹 스타일 */
.tab-container {
	display: inline-flex;
	border-radius: 1px;
	overflow: hidden; /* 모서리 둥글게 */
	float: right;
}

/* 개별 버튼 기본 스타일 */
.tab-button {
	background-color: #2c3e50; /* 비활성 버튼 배경색 */
	color: #ffffff; /* 글자 색상 */
	border: none;
	padding: 10px 20px;
	font-size: 13px;
	cursor: pointer;
	outline: none;
	transition: background-color 0.3s ease;
}

/* 버튼 구분선 스타일 */
.tab-button:not(:last-child) {
	border-right: 1px solid #1c2833;
}

/* 활성화된 버튼 스타일 */
.tab-button.active {
	background-color: #11181f; /* 활성 버튼 색상 */
	font-weight: bold;
}

/* 둥근 모서리 적용 */
.tab-button:first-child {
	border-top-left-radius: 4px;
	border-bottom-left-radius: 4px;
}

.tab-button:last-child {
	border-top-right-radius: 4px;
	border-bottom-right-radius: 4px;
}

.form-select {
	width: 50%;
}

.swal2-container {
    z-index: 1100 !important; /* Bootstrap 모달 기본 z-index는 1050 */
}
.custom-swal-popup {
    z-index: 9999 !important; /* Bootstrap 모달보다 훨씬 높게 설정 */
    border: 1px solid #ccc; 
    border-radius: 10px; 
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
    padding: 20px;
}
.color-picker {
  width: 100px; /* 너비 조정 */
  height: 40px; /* 높이 조정 */
  border-radius: 5px; /* 선택 상자 모서리 둥글게 */
}
.d-flex {
  display: flex;
  gap: 15px; /* 컬러 선택 상자 사이의 간격 */
}
@media (max-width: 768px) {
  .custom-modal {
    max-width: 90%; /* 화면의 90% 너비 */
  }
}
 #modifyEventBtn, #deleteEventBtn {
    display: inline-block  /* 강제로 표시 */
} 


.fc-toolbar-title {
    font-size: 20px;
    font-weight: bold;
    /* color: black; */
}

.fc-button {
    background: transparent; /* 버튼 배경 투명 */
    border: none; /* 테두리 제거 */
    color: white; /* 텍스트 색상 */
    font-size: 16px;
    font-weight: bold;
    padding: 5px 15px;
    cursor: pointer;
}

.fc-button:hover {
    background: #004abf; /* 호버 시 배경색 */
    color: white;
}

.fc-button-active {
    background: #004abf; /* 활성화된 버튼 배경색 */
    color: white;
}
.fc-theme-standard th {
    background-color: #e5edff; /* 요일 배경 */
    color: #7b7b7b; /* 텍스트 색상 */
    font-weight: 600;
    font-size: 14px;
    border: 1px solid #ddd;
}

.fc-daygrid-day {
    background-color: white;
    border: 1px solid #eee;
    padding: 10px;
    font-size: 14px;
}

.fc-day-today {
    background-color: #fff8bd; /* 오늘 날짜 배경색 */
    color: #356eff;
    border: 2px solid #356eff;
}

.fc-daygrid-day:hover {
    background-color: #f9f9f9; /* 호버 시 배경색 */
}

/* 일정 막대기 기본 스타일 */
.fc-event {
  background-color: #ffffff; /* 기본 배경색: 파란색 */
  border: none; /* 테두리 제거 */
  border-radius: 6px; /* 막대기 끝 모서리를 둥글게 */
  padding: 5px; /* 내부 여백 */
  font-size: 12px; /* 글자 크기 */
  font-weight: 500; /* 글자 두께 */
  transition: background-color 0.3s ease, transform 0.2s ease; /* 부드러운 전환 효과 */
}

/* 일정 막대기 호버 스타일 */
.fc-event:hover {
  background-color: #f0f0f0; /* 호버 시 배경색: 더 짙은 파란색 */
  transform: scale(1.03); /* 호버 시 약간 확대 */
}

/* 일정 막대기의 텍스트 가운데 정렬 */
.fc-event-title {
  text-align: center; /* 텍스트 가운데 정렬 */
  overflow: hidden;
  text-overflow: ellipsis; /* 텍스트가 길 경우 말줄임 처리 */
  white-space: nowrap; /* 텍스트를 한 줄로 유지 */
}

/* 일정 막대기 그림자 추가 */
.fc-event {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 효과 */
}

</style>


<body>
	<!-- Bootstrap 모달 -->
	<div class="modal fade" id="basicModal" tabindex="-1"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
				<security:authorize access="isAuthenticated()">
				    <security:authentication property="principal" var="principal" />
				    <c:set value="${principal.account.empId}"  var="empId"></c:set>
				    <security:authentication property="principal" var="principal" />
				    <c:set value="${principal.account.departCode}"  var="departCode"></c:set>
				    <security:authentication property="principal" var="principal" />
				    <c:set value="${principal.account.posiId}"  var="posiId"></c:set>
				</security:authorize>
				
					
					
					<h3 id="page-title">일정</h3>
					
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">

					<form id="eventForm">

						<!-- 일정 유형 선택 -->
						<div class="row g-2 mb-3">
					    <div class="col-12 d-flex align-items-center">
					        <div style="flex-grow: 1;">
					            <label for="scheduleType" class="form-label">일정 유형 선택</label>
					            <select id="scheduleType" class="form-select" required="required">
					                <option value="1">회사</option>
					                <option value="2">부서</option>
					                <option value="3">개인</option>
					            </select>
					        </div>
					        <button id="dataMappingBtnS" class="btn btn-outline-secondary ms-auto" style="font-size: 12px; padding: 5px 10px;">데이터 삽입</button>
					    </div>
					</div>
						<input type="hidden" id="hiddenCompanyId" value="${companyId}">
					<input type="hidden" id="hiddenEmpId" value="${empId}">
					<input type="hidden" id="hiddenRastEmpId" value="${principal.account.empId}">
					<input type="hidden" id="hiddenDeptCode" value="${departCode}"> 
					<input type="hidden"id="hiddenPosiId" value="${posiId}">
						<div class="row g-2">
							<div class="col-md-6">
								<label for="schStart" class="form-label">시작일</label> <input
									type="datetime-local" id="schStart" class="form-control" required="required"/>
							</div>
							<div class="col-md-6">
								<label for="schEnd" class="form-label">종료일</label> <input
									type="datetime-local" id="schEnd" class="form-control" required="required"/>
							</div>
						</div>

						<div class="row g-2 mb-3 align-items-center">
							<div class="col-md-10">
								<label for="eventName" class="form-label">일정명</label> <input
									type="text" class="form-control" id="eventName"
									placeholder="일정명을 입력하세요." maxlength="160" required="required">
							</div>
							<div class="col-md-2 d-flex align-items-center">
								<div class="form-check">
									<label for="allDay" class="form-check-label ms-1">종일</label> <input
										type="checkbox" class="form-check-input" id="allDay">
								</div>
							</div>
						</div>

						<!-- 일정 상세 내용 -->
						<div class="row g-2 mb-3">
							<div class="col-12">
								<label for="eventContent" class="form-label">일정상세내용</label>
								<textarea class="form-control" id="eventContent" rows="4"
									placeholder="일정의 상세 내용을 입력하세요." maxlength="160"required="required"></textarea>
							</div>
						</div>
					<%-- <c:if test="${not empty schetypeId and (schetypeId == '1' or schetypeId == '2')}"> --%>
						<div id="colorSettings" >
						</div>
					<%-- </c:if> --%> 
					</form>
				</div>
				<div class="modal-footer" id="modalFooterSchedule">
					<button type="button" id="addEventBtn" class="btn btn-primary">추가</button>

					<button type="button" id="closeModal" class="btn btn-secondary"
						data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 실제 화면을 담을 영역 -->
	<div id="Wrapper">
		
	<security:authorize access="isAuthenticated()">
	    <security:authentication property="principal" var="principal" />
	    <c:set value="${principal.account.empId}" var="empId"></c:set>
	  
	   <%--  <p>사용자 전화번호: ${principal.realUser.memHp}</p> --%>
	</security:authorize>
		
   
		<h3 id="page-title">일정</h3> 
		<hr />
		<!-- 버튼 그룹 -->
		<div class="tab-container">
			<button class="tab-button acctive" data-view="전체">전체</button>
			<button class="tab-button" data-view="회사">회사</button>
			<button class="tab-button" data-view="부서">부서</button>
			<button class="tab-button" data-view="개인">개인</button>
		</div>

		<br> <br>
		
		<div id="calendar"></div>
		<c:if test="${posiId=='7'}">
		<button id="colorBtnSchedule" class="btn btn-primary">관리</button>
		</c:if>
	</div>
	
<!-- 회사, 부서 일정 블록색깔 변경 모달 -->	
	<div id="colorCustomizationModal" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">색상 설정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form id="colorCustomizationForm">
          <div class="mb-3">
            <label class="form-label">✅회사 일정</label>
          </div>
          <div class="d-flex align-items-center">
            <div class="me-3">
              <label for="companyBackgroundColor" class="form-label">배경색</label>
              <input type="color" id="companyBackgroundColor" class="form-control color-picker">
            </div>
            <div>
              <label for="companyTextColor" class="form-label">글자색</label>
              <input type="color" id="companyTextColor" class="form-control color-picker">
            </div>
          </div>
          <br>
          <div class="mb-3">
            <label class="form-label">✅부서 일정</label>
          </div>
          <div class="d-flex align-items-center">
            <div class="me-3">
              <label for="deptBackgroundColor" class="form-label">배경색</label>
              <input type="color" id="deptBackgroundColor" class="form-control color-picker">
            </div>
            <div>
              <label for="deptTextColor" class="form-label">글자색</label>
              <input type="color" id="deptTextColor" class="form-control color-picker">
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" id="saveColorSettingsBtn" class="btn btn-primary">저장</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>

	

	
	