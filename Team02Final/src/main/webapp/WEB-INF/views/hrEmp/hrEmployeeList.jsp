<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
   
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
				   		
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/employee/hrEmployee.css">
<h4 id="page-title">사원 관리</h4>
<hr/>
	
<div class="filter-container">
  <!-- 초기화 버튼 -->
  <button type="button" class="search-btn btn btn-outline-primary" id="reload-btn">검색초기화</button>

  <!-- 입사일자 -->
  <label for="startJoinDate">
    <strong>입사일자</strong>
    <input type="date" id="startJoinDate" class="form-control">
  </label>
  <span id="joindate">~</span>
  <label for="endJoinDate">
    <input type="date" id="endJoinDate" class="form-control">
  </label>

  <!-- 부서 -->
  <label for="selectDepart">
    <strong>부서</strong>
    <select id="selectDepart" class="form-select"></select>
  </label>

  <!-- 직급 -->
  <label for="selectPosi">
    <strong>직급</strong>
    <select id="selectPosi" class="form-select"></select>
  </label>

  <!-- 재직 상태 -->
  <label for="selectStatus">
    <strong>재직 상태</strong>
    <select id="selectStatus" class="form-select">
      <option value="">전체</option>
    </select>
  </label>

  <!-- 검색 -->
  <div class="search-container">
    <input type="text" id="searchInput" class="search-input" placeholder="사원명을 입력하세요">
    <button class="searchBtn btn btn-primary">검색</button>
  </div>
</div>

<div class="table">
  <table class="table" id="employeeTable">
    <thead>
      <tr>
        <th>사번</th>
        <th>사원명</th>
        <th>부서</th>
        <th>직급</th>
<!--         <th>입사일자</th> -->
        <th>재직상태</th>
        <th>핸드폰번호</th>
        <th>추가관리</th>
      </tr>
    </thead>
		<tbody id="innerTbody">
			<c:if test="${not empty list }">
				<c:forEach items="${list }" var="emp">
					<tr>
						<td>
							<a href="javascript:void(0);" id="empModifyBtn">
								${emp.empId}
							</a>
						</td>
						<td>${emp.empName}</td>
						<td>${emp.departmentVO.departName}</td>
						<td>${emp.posiName}</td>
		<%-- 				<td>${emp.empJoin.substring(0, 4)}-${emp.empJoin.substring(4, 6)}-${emp.empJoin.substring(6, 8)}</td> --%>
						<td>${emp.empStatus}</td>
						<td>${emp.empPhone.substring(0, 3)}-${emp.empPhone.substring(3, 7)}-${emp.empPhone.substring(7, 11)}</td>
						<td>
							<c:if test="${emp.empStatus != '퇴사'}">
								<div class="dropdown">
									<button type="button" class="btn p-0 dropdown-toggle hide-arrow"
										data-bs-toggle="dropdown">
										<i class="bx bx-dots-vertical-rounded"></i>
									</button>
									<div class="dropdown-menu">
									    <a class="dropdown-item text-primary" href="javascript:void(0);" id="empVacaDetailBtn">
									        <i class="bx bx-edit-alt me-1"></i>휴가 현황
									    </a>
									    <a class="dropdown-item text-danger" href="javascript:void(0);" id="empRemoveBtn">
									        <i class="bx bx-trash me-1"></i>퇴사 처리
									    </a>
									</div>
								</div>
							</c:if>
						</td>
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
				<td colspan="7">
					<div class="paging-area">
						${pagingHtml}
						<div>
							<button type="button" id="addVacation" class="btn btn-success">휴가 일괄등록</button>
						    <button type="button" id="addEmp" class="btn btn-primary">사원 등록</button>				
						</div>
					</div>
				</td>
			</tr>
		</tfoot>
  </table>
</div>
<form id="searchForm" method="get" action="${pageContext.request.contextPath }/${companyId }/hr/employee">
    <input type="hidden" name="startDate" value="${startDate}" />
    <input type="hidden" name="endDate" value="${endDate}" />
    <input type="hidden" name="department" value="${department}" />
    <input type="hidden" name="position" value="${position}" />
    <input type="hidden" name="status" value="${status}" />
    <input type="hidden" name="searchWord" value="${searchWord}" />
    <input type="hidden" name="page" value="${page}" />
</form>





<div id="modEmpModal" class="modal fade bd-example-modal-lg"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4>사원 정보 수정</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="modEmpModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="modEmpBtn" class="btn btn-primary">
				        수정
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>

<div id="empVacaModal" class="modal fade bd-example-modal"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4>휴가 상세현황</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="empVacaModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="addVacaBtn" class="btn btn-primary">
				        휴가 추가부여
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>

<div id="vacation-save" class="modal fade bd-example-modal"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4>휴가 등록</h4>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<table id="vacation-modal-table">
					<tr>
						<td class="modal-body-title">대상</td>
						<td class="modal-body-main">
							 <div class="d-flex align-items-center" style="gap: 5px;">
						        <!-- 수신자 태그 영역 -->
						        <div id="vacation-to-area" class="d-flex flex-wrap align-items-center flex-grow-1">
						            <!-- 선택된 수신자 태그가 여기에 추가 -->
						        </div>
						
						        <!-- 검색 버튼 -->
						        <button type="button" id="vacation-to-btn" class="btn btn-primary">
						            <i class="bi bi-person-plus-fill"></i>
						        </button>
						    </div>
						  
						</td>
					</tr>
					<tr>
						<td class="modal-body-title">종류</td>
						<td class="modal-body-main">
							<select class="form-control" id="vacation-detail">
								<option value>휴가 종류</option>
								<option value="normal">일반 휴가(연차)</option>
								<option value="plus">추가 휴가</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="modal-body-title">개수</td>
						<td class="modal-body-main">
							<input type="number" id="vacation-cnt" class="form-control">
						</td>
					</tr>
				</table>
			
			</div>
			<div class="modal-footer">
				<button type="button" id="vacation-save-btn" class="btn btn-primary">저장</button>
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
			</div>
		
		</div>
	</div>
</div>

<!-- 주소록 모달 -->
<div class="modal fade" id="search-modal" tabindex="-1" data-bs-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">임직원 검색</h4>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
			<!-- 모달 바디 -->
            <div class="modal-body">
                <div class="row g-0">
					<div class="mail-search-area mb-4">
						<div class="seach-title mb-3 d-flex gap-2 align-items-center">
						    <input type="text" id="vacation-search-input-area" class="form-control" placeholder="이름/직책 검색">
						    <button type="button" class="btn btn-primary" id="vacation-search-btn">검색</button>
						</div>
					</div>
				</div>
                <div class="col-12 pe-3">
                	<div id="vacation-tree-directory"></div>
                </div>
            </div>
            <!-- 모달 푸터 -->
            <div class="modal-footer">
            	<div class="d-flex justify-content-end w-100 align-items-center gap-3">
	                <div class="d-flex gap-2">
	                    <button type="button" class="btn btn-primary" id="confirm-vacation-plus-btn">추가</button>
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
	                </div>
            	</div>
        	</div>
		</div>
	</div>
</div>


<script src="${pageContext.request.contextPath }/resources/js/app/employee/hrEmployee.js"></script>
