<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<%--CSS연결--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/approval/approvalForm.css">
<link rel="stylesheet" href="${pageContet.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.css" />
<style>
	#projectFilter {
	    height: 38px;
	    width: 120px;
	}
	
	#searchWord {
	    height: 38px;
	    width: 300px;
	}

</style>
<script>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal"/>
		<c:set value="${principal.account.empName }" var="empName"></c:set>                        
		<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
		<c:set value="${principal.account.empId}"  var="empId"></c:set>
		var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
	</security:authorize>
</script>

<h4 id="page-title">프로젝트</h4>
<hr/>
<br/>

<div class="nav-align-top">
  <ul class="nav nav-tabs" role="tablist">
    <li class="nav-item">
      <button type="button" class="nav-link active" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-home" aria-controls="navs-top-home" aria-selected="true">프로젝트</button>
    </li>
    <li class="nav-item">
      <button type="button" class="nav-link" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-profile" aria-controls="navs-top-profile" aria-selected="false">프로젝트 팀</button>
    </li>
  </ul>
  <div class="tab-content">
    <div class="tab-pane fade show active" id="navs-top-home" role="tabpanel">
     	<div class="d-flex justify-content-between align-items-center">
			<div class="d-flex justify-content-start align-items-center">
			    <button type="button" id="menuBtn" class="btn btn-primary mb-2 me-1">
			        <img alt="icon" src="${pageContext.request.contextPath}/resources/images/icon/icon-menu.png">
			    </button>
			    <button type="button" id="gridBtn" class="btn btn-primary mb-2">
			        <img alt="icon" src="${pageContext.request.contextPath}/resources/images/icon/icon-grid.png">
			    </button>
			</div>
		 <div class="d-flex align-items-center gap-2">
		    <!-- Shorter '진행상황' select -->
		    <select class="form-select mb-2" id="projectFilter" aria-label="Default select example" style="height: 38px; width: 120px;">
		        <option selected>진행상황</option>
		        <option value="1">예정</option>
		        <option value="2">진행중</option>
		        <option value="3">종료</option>
		    </select>
		    <!-- Longer '검색어를 입력하세요' input -->
		    <input type="text" class="form-control mb-2" id="searchWord" placeholder="검색어를 입력하세요." style="height: 38px; width: 300px;" />
		    <button type="button" class="btn btn-primary mb-2" style="height: 38px; width: 70px; padding: 6px 12px;">검색</button>
		</div>

		  <div class="navbar-nav align-items-center">
		    <div class="nav-item d-flex align-items-center">
		    <security:authorize access="isAuthenticated()">
	        	<security:authentication property="principal" var="principal" />
				<c:choose>
					<c:when test="${principal.account.posiId == '7' || principal.account.posiId == '6' || principal.account.posiId == '5' || principal.account.posiId == '4' || principal.account.posiId == '3'}">
				      <button type="button" class="btn btn-primary" style="height: 38px; padding: 6px 12px;" data-bs-toggle="modal" data-bs-target="#insertProjectModal">프로젝트 추가</button>
					</c:when>
					<c:otherwise>
				      <button type="button" class="btn btn-primary" style="display:none; height: 38px; padding: 6px 12px;" data-bs-toggle="modal" data-bs-target="#insertProjectModal">프로젝트 추가</button>
					</c:otherwise>
				</c:choose>
    		</security:authorize>
		    </div>
		  </div>
		</div>
		
		<br/><br/>
		
		<div id="container">
		<c:forEach items="${project}" var="proj">
			<!-- CASE1 -->
		<div class="row">
		  <div class="col-md">
		    <div class="card mb-4">
		      <div class="row g-0">
		        <div class="col-md-12">
		          <div class="card-body">
		          	<div class="d-flex justify-content-between align-items-center">
					  <div class="d-flex align-items-center gap-2">
					    <h5 class="card-title mb-0">
					    <a href="${pageContext.request.contextPath}/${companyId}/project/${proj.projId}">${proj.projTitle}</a>
					    </h5>
					    <c:if test="${proj.projStatus eq '예정'}">
						    <span class="badge bg-secondary">${proj.projStatus}</span>
					    </c:if>
					    <c:if test="${proj.projStatus eq '진행중'}">
						    <span class="badge bg-success">${proj.projStatus}</span>
					    </c:if>
					    <c:if test="${proj.projStatus eq '종료'}">
						    <span class="badge bg-dark">${proj.projStatus}</span>
					    </c:if>
					  </div>
					  <div class="dropdown">
					    <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
					      <i class="bx bx-dots-vertical-rounded"></i>
					    </button>
					    <div class="dropdown-menu">
					      <a class="dropdown-item" href="javascript:void(0);" onclick="openModifyProjectModal(`${proj.projId}`)">
					        <i class="bx bx-edit-alt me-1"></i>수정
					      </a>
					      <a class="dropdown-item" href="javascript:void(0);" onclick="deleteProject('${proj.projId}')">
					        <i class="bx bx-trash me-1"></i>삭제
					      </a>
					    </div>
					  </div>
					</div>
					<br/>
		            <p class="card-text">${proj.projContent}</p>
					<div class="row align-items-center mb-2">
					  <!-- 프로젝트 진행기간 -->
					  <div class="col-auto">
					    <label for="start-date" class="form-label">프로젝트 진행기간</label>
					    <div class="d-flex align-items-center gap-2">
					      <input class="form-control" type="date" value="${proj.projSdate}" id="start-date" style="width: 150px; background-color: white;" readonly/>
					      <span>~</span>
					      <input class="form-control" type="date" value="${proj.projEdate}" id="end-date" style="width: 150px; background-color: white;" readonly/>
					    </div>
					  </div>
					
					  <!-- 담당자 -->
					  <div class="col-auto ms-auto">
					    <label for="projectManager" class="form-label">담당자</label>
					    <input type="text" class="form-control" id="projectManager" value="${proj.projectDto.empName}" readonly style="width: 100px; text-align:center; background-color: white;" />
					  </div>
					</div>
		
		            <label for="datePercent" class="form-label">기간별 진행도</label>
		            <div class="progress mb-2" style="height: 16px;">
					  <div class="progress-bar" id="datePercent" role="progressbar" style="width: ${proj.calculateProgress}%;" aria-valuenow="${proj.calculateProgress}" aria-valuemin="0" aria-valuemax="100">${proj.calculateProgress}%</div>
					</div>
		            <label for="workPercent" class="form-label">업무별 진행도</label>
		            <div class="progress mb-2" style="height: 16px;">
					  <div class="progress-bar" id="workPercent" role="progressbar" style="width: ${proj.taskCalculateProgress}%;" aria-valuenow="${proj.taskCalculateProgress}" aria-valuemin="0" aria-valuemax="100">${proj.taskCalculateProgress}%</div>
					</div>
		          </div>
		        </div>
		      </div>
		    </div>
		  </div>
		</div>
		</c:forEach>
		</div>
    </div>
    <div class="tab-pane fade" id="navs-top-profile" role="tabpanel">
		<button type="button" class="btn btn-primary" id="referenceBtn" data-bs-toggle="modal" data-bs-target="#referenceModal">팀 등록</button>
		
		<div class="accordion accordion-header-primary" id="accordionStyle1">
		<c:forEach items="${teamGrouped}" var="teamGroup">
    <div class="accordion-item card">
        <h2 class="accordion-header">
            <button type="button" class="accordion-button collapsed" data-bs-toggle="collapse" data-bs-target="#accordionStyle1-${teamGroup.key}" aria-expanded="false">
                ${teamGroup.key} <!-- 팀 ID 표시 -->
            </button>
        </h2>
        <div id="accordionStyle1-${teamGroup.key}" class="accordion-collapse collapse" data-bs-parent="#accordionStyle1">
            <div class="accordion-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th></th>
                                <th>부서</th>
                                <th>이름</th>
                                <th>직급</th>
                                <th>팀 배정일</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${teamGroup.value}" var="teamHistory">
                                <tr>
                                    <td>
                                        <input type="checkbox">
                                    </td>
                                    <td>
                                        ${teamHistory.teamHistoryDTO.departName}
                                    </td>
                                    <td>
                                        ${teamHistory.teamHistoryDTO.empName}
                                    </td>
                                    <td>
                                        ${teamHistory.teamHistoryDTO.codeComment}
                                    </td>
                                    <td>
                                        <input type="date" readonly class="form-control-plaintext" value="${teamHistory.teamAssignmentdate}">
                                    </td>
                                    <td>
                                        <div class="dropdown">
                                            <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                                                <i class="bx bx-dots-vertical-rounded"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item" href="javascript:void(0);">
                                                    <i class="bx bx-trash me-1"></i>삭제
                                                </a>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</c:forEach>

		
	  </div>		
		
    </div>
  </div>
</div>

<!-- 참조자 설정 모달임-->
<!-- <div class="modal fade" id="referenceModal" tabindex="-1" aria-hidden="true"> -->
<div class="modal fade" id="referenceModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5>팀 등록</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!-- 왼쪽: 조직도 -->
                    <div class="col-6">
						<div style="height: 550px;">
	                        <div class="org-tree" id="vertical-example">
	                            <ul id="refOrgTree"></ul>
	                        </div>
						</div>
                    </div>
                    <!-- 오른쪽: 선택된 참조자 -->
                    <div class="col-6"> 
                    	<div class="mb-3">
						  <label for="teamId" class="form-label">팀 이름</label>
						  <input type="text" class="form-control" id="teamId" placeholder="홍길동팀" maxlength="100" aria-describedby="defaultFormControlHelp" />
						</div>
                        <h6>선택된 참가자</h6>
                        <div id="referenceList" class="reference-list">
                        	<div class="table-responsive">
							  <table class="table table-hover">
							    <thead>
							      <tr>
							        <th>부서</th>
							        <th>이름</th>
							        <th>직급</th>
							      </tr>
							    </thead>
								    <tbody id="projectMember-form">
							     
								    </tbody>
							  </table>
							</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="confirmRefBtn">등록</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
            </div>
        </div>
    </div>
</div>

<!-- Large Modal -->
<form id="insertProjectForm">
<div class="modal fade" id="insertProjectModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel3">프로젝트 생성</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col mb-6">
            <label for="projTitle" class="form-label">프로젝트 이름</label>
            <input type="text" id="projTitle" name="projTitle" class="form-control" maxlength="100"  placeholder="프로젝트 이름을 입력하세요." required="required">
          </div>
        </div>
        <br/>
        <div class="row">
          <div class="col mb-6">
            <label for="projContent" class="form-label">프로젝트 설명</label>
            <input type="text" id="projContent" name="projContent" class="form-control" maxlength="200"  placeholder="프로젝트에 대한 설명을 입력하세요." required="required">
          </div>
        </div>
        <br/>
        <div class="row g-6">
          <div class="col mb-0">
            <label for="projSdate" class="form-label">프로젝트 시작일</label>
            <input type="date" id="projSdate" name="projSdate" class="form-control" required="required">
          </div>
          <div class="col mb-0">
            <label for="projEdate" class="form-label">프로젝트 종료일</label>
            <input type="date" id="projEdate" name="projEdate" class="form-control" required="required">
          </div>
        </div>
      </div>
      <div class="modal-footer">
    	<button type="button" class="btn btn-outline-dark btn-sm" id="dataInputBtn">데이터삽입</button>
        <button type="submit" class="btn btn-primary">등록</button>
        <button type="button" class="btn btn-label-secondary" data-bs-dismiss="modal">취소</button>
      </div>
    </div>
  </div>
</div>
</form>

<!-- Large Modal -->
<form id="modifyProjectForm">
<div class="modal fade" id="modifyProjectModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel3">프로젝트 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col mb-6">
            <label for="projTitle" class="form-label">프로젝트 이름</label>
            <input type="text" id="modifyprojTitle" name="projTitle" class="form-control" maxlength="100"  placeholder="프로젝트 이름을 입력하세요." required="required">
          </div>
        </div>
        <br/>
        <div class="row">
          <div class="col mb-6">
            <label for="projContent" class="form-label">프로젝트 설명</label>
            <input type="text" id="modifyprojContent" name="projContent" class="form-control" maxlength="200"  placeholder="프로젝트에 대한 설명을 입력하세요." required="required">
          </div>
        </div>
        <br/>
        <div class="row">
          <div class="col mb-6">
            <label for="projStatus" class="form-label">프로젝트 상태</label>
            <select id="projStatus" name="projStatus" class="form-control" required="required">
            	<option value="" label="선택"/>
            	<option value="EXPECT" label="예정"/>
            	<option value="ING" label="진행중"/>
            	<option value="END" label="종료"/>
            </select>
          </div>
        </div>
        <br/>
        <div class="row g-6">
          <div class="col mb-0">
            <label for="projSdate" class="form-label">프로젝트 시작일</label>
            <input type="date" id="modifyprojSdate" name="projSdate" class="form-control" required="required">
          </div>
          <div class="col mb-0">
            <label for="projEdate" class="form-label">프로젝트 종료일</label>
            <input type="date" id="modifyprojEdate" name="projEdate" class="form-control" required="required">
            <input type="text" id="modifyprojId" name="projId" class="form-control" style="display: none;" readonly="readonly">
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="submit" class="btn btn-primary">확인</button>
        <button type="button" class="btn btn-label-secondary" data-bs-dismiss="modal">취소</button>
      </div>
    </div>
  </div>
</div>
</form>

<script src="${pageContet.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.js"></script>
<script>
    // 조직도 갖고오기
    var orgTreeUrl = '${pageContext.request.contextPath}/${companyId}/approval/orgTree';
    
    // 사원 갖고오기
    var empUrl = '${pageContext.request.contextPath}/${companyId}/approval/employees';
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/teamHistory/teamHistoryForm.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/project/projectList.js"></script>