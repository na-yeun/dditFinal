<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/index.css">

<!-- CSS 파일 -->
    <link rel="stylesheet" href="https://unpkg.com/tippy.js@6/dist/tippy.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/fullcalendar@5.8.0/main.min.css" />

    <!-- Popper.js와 Tippy.js -->
    <script src="https://unpkg.com/@popperjs/core@2"></script>
    <script src="https://unpkg.com/tippy.js@6"></script>

    <!-- FullCalendar JS -->
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>

<div class="row">
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal" />
		<c:set value="${principal.account.empName}" var="empName"></c:set>
		<c:set value="${principal.account.base64EmpImg}" var="empImg"></c:set>
		<c:set value="${principal.account.empId }" var="empId"></c:set>
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>
		<c:set value="${principal.account.departCode}" var="departCode"></c:set>
		<c:set value="${principal.account.posiId}" var="posiId"></c:set>
		<c:set value="${principal.account.myComment}" var="myComment"></c:set>
	</security:authorize>
	
<input type="hidden" id="hiddenEmpId" value="${empId}"/> 
	<input type="hidden" id="hiddenDeptCode" value="${departCode}"/> 
	<input type="hidden" id="hiddenPosiId" value="${posiId}"/>	
	<!-- 사용자 정보 -->
	<div class="col-md-6 row top-main">
		<div class="col-md-6">
			<div class="card myprofile">
				<div class="card-header">
					<h4 class="card-title mb-0">👤나의 프로필👤</h4>
				</div>
				<div class="card-body profile-body">
					<div class="profile-info">
						<c:if test="${not empty empImg}">
							<img src="data:image/*;base64,${empImg}" alt="프로필 이미지"
								class="rounded-circle myimg" />
						</c:if>
						<c:if test="${empty empImg}">
							<img src="${pageContext.request.contextPath}/resources/images/profile-img.jpg"
								alt="기본 프로필 이미지" class="rounded-circle myimg" />
						</c:if>
						<table class="my-info-table">
							<tr>
								<td>🗨️소속🗨️</td>
								<td><strong class="myDept"></strong></td>
							</tr>
							<tr>
								<td>🗨️이름🗨️</td>
								<td>${empName}</td>
							</tr>
						</table>
						<p id="comment-p-tag">
							<span id="my-comment-area">${myComment}</span>
							<a href="javascript:void(0);" onclick="editMyComent()">
								<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
							</a>
						</p>
						
					</div>
					<div class="attendance-info">
						<table class="table">
							<tr>
								<td>출근</td>
								<td><span id="startTime"></span></td>
							</tr>
							<tr>
								<td>퇴근</td>
								<td><span id="endTime"></span></td>
							</tr>
						</table>
						<div class="mt-3">
							<button id="startWorkBtn" class="btn btn-primary btn-sm">출근</button>
							<button id="endWorkBtn" class="btn btn-secondary btn-sm">퇴근</button>
							<button id="endWorkLogoutBtn" class="btn btn-danger btn-sm">퇴근
								및 로그아웃</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- to do list -->
		<div class="col-md-6">
			<div class="card todo-list">
				<div class="card-header">
					<h4 class="card-title mb-0">✅오늘의 할 일✅</h4>
				</div>
				<div class="card-body">
					<div class="list-group">
						<c:if test="${not empty mainPageData.toDoListList}">
							<c:forEach items="${mainPageData.toDoListList}" var="mydo" varStatus="status">
								<c:if test="${mydo.todoStatus eq 'N'}"> <!-- 완료 아닐 때 -->
									<c:if test="${mydo.todoPriority eq 1}"> <!-- 중요도 낮을 때 -->
										<div class="todo-one todo-row" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}">
											<span data-content="${mydo.todoContent}">${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
									<c:if test="${mydo.todoPriority eq 2}"> <!-- 중요도 보통일 때 -->
										<div class="todo-one todo-normal" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}">
											<span data-content="${mydo.todoContent}">${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
									<c:if test="${mydo.todoPriority eq 3}"> <!-- 중요도 높을 때 -->
										<div class="todo-one todo-high" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}">
											<span data-content="${mydo.todoContent}">🚨 ${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
								</c:if>
								
								<c:if test="${mydo.todoStatus eq 'Y'}"> <!-- 완료일 때 -->
									<c:if test="${mydo.todoPriority eq 1}">
										<div class="todo-one todo-row todo-done" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}" checked>
											<span data-content="${mydo.todoContent}">${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
									<c:if test="${mydo.todoPriority eq 2}">
										<div class="todo-one todo-normal todo-done" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}" checked>
											<span data-content="${mydo.todoContent}">${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
									<c:if test="${mydo.todoPriority eq 3}">
										<div class="todo-one todo-high todo-done" id="${mydo.todoNo}" data-priority="${mydo.todoPriority}">
											<input type="checkbox" class="todo-check" id="todo-check-${mydo.todoNo}" checked>
											<span data-content="${mydo.todoContent}">🚨 ${mydo.todoContent}</span>
											<a href="javascript:void(0);" onclick="editToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/edit-icon.png"/>
											</a>
											<a href="javascript:void(0);" onclick="deleteToDo('${mydo.todoNo}')">
												<img class="icon" alt="삭제" src="${pageContext.request.contextPath}/resources/images/delete-icon.png"/>
											</a>
										</div>
									</c:if>
									
								</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${empty mainPageData.toDoListList}">
							
						</c:if>
					</div>
					<div class="todo-btn">
						<button class="btn btn-outline-primary btn-sm mt-3" id="todo-plus-btn">추가</button>
					</div>
				</div>
			</div>
		</div>
				
		<div class="col-md-6">
			<div class="card team-status">
				<div class="card-header">
					<h4 class="card-title mb-0">❌금일 팀 휴가자❌</h4>
				</div>
				<div class="card-body">
					<c:if test="${not empty vacationList}">
						<c:forEach items="${vacationList}" var="vacation">
							<p><b>${vacation.posiName}</b> ${vacation.empName}</p>
						</c:forEach>
					</c:if>
					<c:if test="${empty vacationList}">
						<p>금일 휴가자 없음</p>
					</c:if>
				</div>
			</div>
		</div>
		<div class="col-md-6">
		    <div class="card team-status">
		        <div class="card-header">
		            <h4 class="card-title mb-0">📃나의 프로젝트📃</h4>
		        </div>
		        <div class="card-body">
		            <c:choose>
		                <c:when test="${not empty mainPageData.projectList}">
		                    <c:forEach items="${mainPageData.projectList}" var="proj" varStatus="status">
		                        <c:if test="${status.index < 3}">
		                            <div style="display: flex; justify-content: space-between; align-items: center;">
		                                <a href="${pageContext.request.contextPath}/${companyId}/project/${proj.projId}" style="text-decoration: none;">
		                                    ${proj.projTitle}
		                                </a>
		                                <c:if test="${proj.projectDto.codeComment eq '예정'}">
		                                    <span class="badge bg-secondary">${proj.projectDto.codeComment}</span>
		                                </c:if>
		                                <c:if test="${proj.projectDto.codeComment eq '진행중'}">
		                                    <span class="badge bg-success">${proj.projectDto.codeComment}</span>
		                                </c:if>
		                                <c:if test="${proj.projectDto.codeComment eq '종료'}">
		                                    <span class="badge bg-dark">${proj.projectDto.codeComment}</span>
		                                </c:if>
		                            </div>
		                            <br/>
		                        </c:if>
		                    </c:forEach>
		                </c:when>
		                <c:otherwise>
		                    <p>아직 참가된 프로젝트가 없습니다.</p>
		                </c:otherwise>
		            </c:choose>
		        </div>
		    </div>
		</div>
	</div>

	<!-- 캘린더 -->
	<div class="col-md-6 row top-main">
		<div class="col-md-12">
			<div class="card calendar-container">
				<div class="card-header">
					<%-- <h4 class="card-title mb-0">📆${mainPageData.nowYear}년
						${mainPageData.nowMonth}월📆</h4> --%>
						<button class="btn btn-outline-primary btn-sm float-end"
						onclick="location.href='${pageContext.request.contextPath}/${companyId}/schedule/cal'">전체보기</button>
				<br>
				</div>
				<div class="card-body">	<!-- 캘린더 내용 -->
					<div>
						<main class="calendar-container">
							<div id="calendarMain"></div>
						</main>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- to do list 모달 -->
<div class="modal fade" id="todolistModal" tabindex="-1" aria-labelledby="todolistModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="todolistModalLabel">오늘 할 일</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form id="todo-plus-form" action="${pageContext.request.contextPath}/${companyId}/todo">
	      <div class="modal-body">
	      	<div style="text-align: right;">
				<button type="button" class="btn btn-outline-dark btn-sm" id="insert-todo-btn">데이터삽입</button>
			</div>
	      	<p class="todo-list-info">추가하시는 할 일은 내일이 되면 자동으로 삭제됩니다.</p>
	      	<table class="table">
	      		<tr style="display:none;">
	      			<td>할 일 ID</td>
	      			<td>
	      				<input id="todoNo" type="text" class="form-control" name="todoNo">
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>할 일</td>
	      			<td>
	      				<input id="todoContent" type="text" class="form-control" name="todoContent" placeholder="30자 이내로 작성해주세요.">
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>중요도</td>
	      			<td>
	      				<select id="todoPriority" class="form-control" name="todoPriority">
	      					<option value>중요도를 선택해주세요.</option>
	      					<option value="1">낮음</option>
	      					<option value="2">보통</option>
	      					<option value="3">높음</option>
	      				</select>
	      			</td>
	      		</tr>
	      	</table>
	      </div>
	      <div class="modal-footer">
	        <button type="submit" class="btn btn-primary">저장</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
	      </div>
      </form>
    </div>
  </div>
</div>

<!-- my comment 수정 폼 -->
<div class="modal fade" id="myCommentModal" tabindex="-1" aria-labelledby="myCommentModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="myCommentModalLabel">멘트 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form id="my-comment-form" action="${pageContext.request.contextPath}/${companyId}/main/comment">
	      <div class="modal-body">
	      	<table class="table">
	      		<tr>
	      			<td>멘트</td>
	      			<td>
	      				<input id="myComment" type="text" class="form-control" name="myComment" placeholder="30자 이내로 작성해주세요.">
	      			</td>
	      		</tr>
	      	</table>
	      </div>
	      <div class="modal-footer">
	        <button type="submit" class="btn btn-primary">저장</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
	      </div>
      </form>
    </div>
  </div>
</div>



<script> 
//js에서 사용하기 위해 뽑아버림 (민재)
	const contextPath="${pageContext.request.contextPath}";
	const companyId="${companyId}";
	const empId="${empId}";
	const myComment="${myComment}";
	
	
</script>
<!-- 출근 퇴근 퇴근및 로그아웃 근태 처리 스크립트 파일 (민재) -->
<script src="${pageContext.request.contextPath }/resources/js/app/mainpage/startEndWork.js"></script>
<script src="${pageContext.request.contextPath }/resources/js/app/mainpage/sidebar.js"></script>
<script src="${pageContext.request.contextPath }/resources/js/app/mainpage/todolist.js"></script>
<script src="${pageContext.request.contextPath }/resources/js/app/mainpage/myComment.js"></script>
<script src="${pageContext.request.contextPath }/resources/js/app/schedule/scheduleCalendarMain.js"></script>





