<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags"  prefix="security"%>

<%--CSS연결--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/project/projectDetail.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/project/projectDetailWork.css" />

<script>
// 내 ID
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
    <c:set value="${principal.account.empId}"  var="empId"></c:set>
    var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
</security:authorize>
</script>

<%-- DHTMLX GANTT --%>
    <link rel="stylesheet" href="https://docs.dhtmlx.com/gantt/codebase/dhtmlxgantt.css">
    <script src="https://docs.dhtmlx.com/gantt/codebase/dhtmlxgantt.js"></script>

<h4 id="page-title">프로젝트 - <strong>${project.projTitle }</strong></h4>
   <div class="row mb-3">
     <div class="col">
         <label for="startdate" class="form-label">시작일</label>
  		<input id="startdate" class="form-control form-control-lg" type="text" value="${project.projSdate }" style="background-color: white" readonly="readonly"/>
     </div>
     <div class="col">
         <label for="enddate" class="form-label">종료일</label>
  		<input id="enddate" class="form-control form-control-lg" type="text" value="${project.projEdate }" style="background-color: white" readonly="readonly"/>
     </div>
   </div>
<hr/>

<style>
	#gantt-chart{
		height: 800px;
		width: auto;
	}
	
	.gantt-error {
	    display: none !important;
	}
	
	body,
	html {
		width: 100%;
		height: 100%;
		margin: unset;
	}
.range_resizer {
    width: 100%; /* 부모 크기 100% */
    height: 20px;
    margin: 10px auto; /* 중앙 정렬 */
    position: relative;
    display: flex;
    align-items: center; /* 자식 요소 수직 정렬 */
}

.total_range {
    width: 100%;
    height: 10px;
    background: gray;
    border-radius: 16px;
    position: relative;
}


.left_resizer, .right_resizer {
    width: 16px;
    height: 16px;
    background: #444;
    border-radius: 50%; /* 원형 */
    position: absolute;
    top: -3px; /* 높이 조정으로 위치 맞춤 */
    cursor: pointer; /* 마우스 커서 */
    z-index: 2;
}

.right_resizer {
    right: 0; /* 부모 컨테이너의 오른쪽 끝 */
}

.range_indicator {
    position: absolute;
    height: 10px;
    background: #555;
    border-radius: 5px;
    z-index: 1;
    top: 0; /* 수직 정렬 */
}
	.today {
		border-left: 2px dashed gray;
		background: unset;
	}

	.gantt_marker .gantt_marker_content {
		background: gray;
	}


	.gantt_popup_button {
		width: 50px;
		min-width: unset;
	}

	.gantt_task_content {
		line-height: 14px;
	}

	.work_item {
		height: 15px;
		margin-top: 30px;
		text-align: center;
		z-index: 1;
		position: absolute;
		border-radius: 2px;
		filter: saturate(180%);
	}

	.gantt_grid_head_cell[data-column-name="work_items"],
	.gantt_grid_head_cell[data-column-name="story_points"] {
		line-height: 15px;
		white-space: normal;
	}

	.gantt_grid_data .gantt_cell {
		border-right: 1px solid #ebebeb
	}

	.status_name {
		text-overflow: ellipsis;
		overflow: hidden;
	}

	.status_column,
	.status_column.odd {
		line-height: 20px;
		display: grid;
		justify-content: space-between;

	}

	.status_progress {
		display: flex;
	}

	.status_progress .bar {
		background: #dedede;
		width: 10px;
		height: 10px;
		margin: 1px;
	}

	.status_progress .bar.filled {
		background: #1197da;
	}
	
	.gantt_task_content, .gantt_task_progress_drag {
	    display: none !important;
	}
	.gantt_tree_content {
		width: 280px;
	    display: flex; /* 플렉스 컨테이너 */
	    align-items: center; /* 수직 중앙 정렬 */
	    justify-content: space-between; /* 텍스트를 왼쪽 정렬 */	
	    padding-right: 5px; /* 버튼이 오른쪽으로 붙지 않도록 여백 추가 */
	}
	
	.gantt_tree_content span {
	    flex-grow: 1; /* 텍스트 영역 확장 */
	    text-align: left; /* 텍스트 왼쪽 정렬 */
	}
	
	.gantt_tree_content button , #workSellIcon button{
	    padding: 0;
	    color: white;
	    cursor: pointer;
	    font-size: 18px;
	    width: 24px; /* 버튼 크기 명확히 설정 */
	    height: 24px;
	    background-color: #8592a3; /* 버튼 색상 */
	    display: flex;
	    align-items: center;
	    justify-content: center;
	    border: none; /* 테두리 제거 */
	}
	
	.gantt_tree_content button:hover {
	    background-color: #696cff; /* 호버 시 색상 변경 */
	}
	
	.gantt_task_progress{
		display: none;
	}
	
	.gantt_task_progress_wrapper:hover {
	  opacity: 1.0;
	  cursor : pointer;
	}
	
	.color-option {
	  width: 24px;
	  height: 24px;
	  border: none;
	  border-radius: 4px; /* 약간 둥근 모서리 */
	  cursor: pointer;
	  outline: none;
	  transition: transform 0.2s, box-shadow 0.2s;
	}
	
	.color-option:hover {
	  transform: scale(1.1);
	  outline: 2px solid rgb(45, 114, 255);
	}
	
	.color-option:focus {
	  outline: 2px solid rgb(45, 114, 255);
	}
	
	/* 드롭다운 메뉴 스타일 */
	.dropdown-menu-color {
	    width: 330px; /* 버튼 크기에 맞게 자동 너비 */
	    padding: 10px;
	    border-radius: 8px;
	    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); /* 드롭다운 그림자 */
   	    border: 1px solid rgba(0, 0, 0, 0.15);
	}
		
	#selectedColor, #newTaskColor:hover {
	  border-radius: 4px;
	}

	/* 레이블 왼쪽 여백 추가 */
	.col-form-label {
	    padding-left: 12px !important; /* 레이블에 왼쪽 여백 추가 */
	    text-align: center; /* 레이블 정렬을 왼쪽으로 */
	    white-space: nowrap; /* 텍스트 줄바꿈 방지 */
	}
	
	/* 입력 필드 간격 조정 */
	.form-control {
	    margin-left: 0; /* 입력 필드 기본 여백 유지 */
	    font-size: 0.9rem; /* 텍스트 크기 조정 */
	}
	
	#selectedColor:hover, #newTaskColor:hover{
	  cursor:pointer;
	  transform: scale(1.1);
	  outline: 2px solid rgb(45, 114, 255);
	}
	
	#selectedColor:focus, #newTaskColor:hover{
	  outline: 2px solid rgb(45, 114, 255);
	}
	
	.dropdown-menu-color-modal {
	    width: 330px; /* 버튼 크기에 맞게 자동 너비 */
	    padding: 10px;
	    border-radius: 8px;
	    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); /* 드롭다운 그림자 */
        position: absolute;
	    top: 60px; /* Adjust this value to move it further down */
	    left: 10px;
	    z-index: 1050;
	    display: none; /* Default hidden */
	    padding: 0.5rem;
	    background-color: #fff;
	    border: 1px solid rgba(0, 0, 0, 0.15);
	}
	
/* 기본적으로 버튼 숨김 */
.gantt_row .hover-buttons {
    visibility: hidden; /* 보이지 않게 설정 */
    opacity: 0; /* 투명도 0으로 설정 */
    transition: visibility 0.2s, opacity 0.2s ease-in-out; /* 부드러운 전환 효과 */
}

/* gantt_row에 hover 시 버튼 보이기 */
.gantt_row:hover .hover-buttons {
    visibility: visible; /* 보이게 설정 */
    opacity: 1; /* 투명도 1로 설정 */
}

.gantt_row {
    position: relative; /* 상대 위치 지정 */
}

.hover-buttons {
    position: absolute; /* 절대 위치 지정 */
    right: 10px; /* 오른쪽 끝에서 10px 간격 */
    top: 50%; /* 세로 가운데 정렬 */
    transform: translateY(-50%); /* 세로 가운데 정렬 */
}

.gantt_row:hover .hover-buttons {
    display: flex; /* hover 시 버튼 표시 */
}



.mt-70{
     margin-top: 70px;
}

.mb-70{
     margin-bottom: 70px;
}

.card {
    box-shadow: 0 0.46875rem 2.1875rem rgba(4,9,20,0.03), 0 0.9375rem 1.40625rem rgba(4,9,20,0.03), 0 0.25rem 0.53125rem rgba(4,9,20,0.05), 0 0.125rem 0.1875rem rgba(4,9,20,0.03);
    border-width: 0;
    transition: all .2s;
}

.card {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    border: 1px solid rgba(26,54,126,0.125);
    border-radius: .25rem;
}

.card-body {
    flex: 1 1 auto;
    padding: 1.25rem;
}
.vertical-timeline {
    width: 100%;
    position: relative;
    padding: 1.5rem 0 1rem;
}

.vertical-timeline::before {
    content: '';
    position: absolute;
    top: 0;
    left: 67px;
    height: 100%;
    width: 4px;
    background: #e9ecef;
    border-radius: .25rem;
}

.vertical-timeline-element {
    position: relative;
    margin: 0 0 1rem;
}

.vertical-timeline--animate .vertical-timeline-element-icon.bounce-in {
    visibility: visible;
    animation: cd-bounce-1 .8s;
}
.vertical-timeline-element-icon {
    position: absolute;
    top: 0;
    left: 60px;
}

.vertical-timeline-element-icon .badge-dot-xl {
    box-shadow: 0 0 0 5px #fff;
}

.badge-dot-xl {
    width: 18px;
    height: 18px;
    position: relative;
}
.badge:empty {
    display: none;
}


.badge-dot-xl::before {
    content: '';
    width: 10px;
    height: 10px;
    border-radius: .25rem;
    position: absolute;
    left: 50%;
    top: 50%;
    margin: -5px 0 0 -5px;
    background: #fff;
}

.vertical-timeline-element-content {
    position: relative;
    margin-left: 90px;
    font-size: .8rem;
}

.vertical-timeline-element-content .timeline-title {
    font-size: .8rem;
    text-transform: uppercase;
    margin: 0 0 .5rem;
    padding: 2px 0 0;
    font-weight: bold;
}

.vertical-timeline-element-content .vertical-timeline-element-date {
    display: block;
    position: absolute;
    left: -90px;
    top: 0;
    padding-right: 10px;
    text-align: right;
    color: #adb5bd;
    font-size: .7619rem;
    white-space: nowrap;
}

.vertical-timeline-element-content:after {
    content: "";
    display: table;
    clear: both;
}


</style>
<div>

	<div class="nav-align-top">
	  <ul class="nav nav-tabs" role="tablist">
	    <li class="nav-item">
	      <button type="button" class="nav-link active" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-home" aria-controls="navs-top-home" aria-selected="true">타임라인</button>
	    </li>
	    <li class="nav-item">
	      <button type="button" class="nav-link" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-profile" aria-controls="navs-top-profile" aria-selected="false">프로젝트 참가자</button>
	    </li>
	    <li class="nav-item">
	      <button type="button" class="nav-link" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-messages" aria-controls="navs-top-messages" aria-selected="false">작업내역</button>
	    </li>
	  </ul>
	  <div class="tab-content">
	    <div class="tab-pane fade show active" id="navs-top-home" role="tabpanel">
	    	<div class="tab-pane fade show active" id="navs-pills-top-home" role="tabpanel">
	    		<div class="card overflow-hidden mb-6" style="height: 800px;">
	    		<button class="btn btn-primary" onclick="openModal('',event);">일감 등록</button>
				  <div class="card-body" id="vertical-example">
		    		<div id="gantt-chart">
			    		<div class="range_resizer" title="Change the displayed date range">
							<div class="total_range">
								<div class="range_indicator"></div>
								<div class="left_resizer"></div>
								<div class="right_resizer"></div>
							</div>
						</div>
				
						<div id="gantt_here" style="width:100%; height:100%;"></div>
		    		</div>
				  </div>
				</div>
			</div>
	    </div>
	    <div class="tab-pane fade" id="navs-top-profile" role="tabpanel">
	    	<div class="nav-align-top">
			  <ul class="nav nav-pills mb-4" role="tablist">
			    <li class="nav-item">
			      <button type="button" class="nav-link active" role="tab" data-bs-toggle="tab" data-bs-target="#navs-pills-top-projectMemberList" aria-controls="navs-pills-top-projectMemberList" aria-selected="true">참가자 조회</button>
			    </li>
			    <li class="nav-item">
			      <button type="button" class="nav-link" role="tab" data-bs-toggle="tab" data-bs-target="#navs-pills-top-projectMemberAdd" aria-controls="navs-pills-top-projectMemberAdd" aria-selected="false">참가자 관리</button>
			    </li>
			  </ul>
			  <div class="tab-content">
			    <div class="tab-pane fade show active" id="navs-pills-top-projectMemberList" role="tabpanel">
					<div>
						<div class="table-responsive">
						  <table class="table table-hover">
						    <thead>
						      <tr>
						        <th>역할</th>
						        <th>부서</th>
						        <th>이름</th>
						        <th>직급</th>
						        <th>투입일</th>
						        <th>투입 종료일</th>
						      </tr>
						    </thead>
						    <tbody id="projectMemberTable">
						    	<c:forEach items="${memberList}" var="mem">
		 							<tr>
								        <td><span>${mem.projectRolenm}</span></td>
								        <td>${mem.departName}</td>
								        <td>${mem.empName }</td>
								        <td>${mem.codeComment }</td>
								        <td>${mem.joinDate}</td>
								        <td>${mem.leaveDate}</td>
								        <td>
								          <div class="dropdown">
								            <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown"><i class="bx bx-dots-vertical-rounded"></i></button>
								            <div class="dropdown-menu">
								              <a class="dropdown-item" href="javascript:void(0);" onclick="modifyProjectMember('${mem.projectMemberid}');"><i class="bx bx-edit-alt me-1"></i>수정</a>
								              <a class="dropdown-item" href="javascript:void(0);" onclick="deleteProjectMember('${mem.projectMemberid}');"><i class="bx bx-trash me-1"></i>삭제 </a>
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
				<div class="tab-pane fade" id="navs-pills-top-projectMemberAdd" role="tabpanel">
				    <div class="container-fluid">
				        <div class="row">
				            <div class="col-12 mb-3">
				                <h5>프로젝트 참가자 등록</h5>
				            </div>
				        </div>
				        <div class="row">
				            <!-- 왼쪽: 조직도 -->
				            <div class="col-md-4">
				        	    <h6>팀 리스트</h6>
				                <div class="org-tree">
				                    <ul id="refOrgTree"></ul>
				                </div>
				            </div>
				            <!-- 오른쪽: 선택된 참가자 -->
				            <div class="col-md-6">
				                <h6>선택된 참가자</h6>
				                <div class="reference-list" style="height: 600px; width: 800px;">
				                    <div class="table-responsive" id="vertical-example">
				                        <table class="table table-hover">
				                            <thead>
				                                <tr>
				                                    <th>팀</th>
				                                    <th>이름</th>
				                                    <th>직급</th>
				                                    <th>투입일</th>
				                                    <th>투입종료일</th>
				                                    <th>역할</th>
				                                </tr>
				                            </thead>
				                            <tbody id="projectMember-form">
				                                <!-- 참가자 데이터 -->
				                            </tbody>
				                        </table>
				                    </div>
				                </div>
				            </div>
				        </div>
				        <div class="row mt-3">
				            <div class="col text-end">
				                <button type="button" class="btn btn-primary" id="confirmRefBtn">등록</button>
				                <button type="button" class="btn btn-secondary" onclick="clearForm();">초기화</button>
				            </div>
				        </div>
				    </div>
				</div>
			  </div>
			</div>
	    </div>
	    <div class="tab-pane fade" id="navs-top-messages" role="tabpanel">
	    	<div class="row d-flex justify-content-center mt-70 mb-70">
          <div class="col-md-11">
                    <h5 class="card-title">작업내역</h5>
                    <div id="taskHistoryLog">
                    <c:forEach items="${taskList }" var="task">
                        <div class="vertical-timeline-item vertical-timeline-element">
                            <div>
                                <span class="vertical-timeline-element-icon bounce-in">
                                    <i class="badge badge-dot badge-dot-xl badge-success"></i>
                                </span>
                                <div class="vertical-timeline-element-content bounce-in">
                                <br/>
                                    <h4 class="timeline-title">${task.taskMethod }</h4>
                                    <p>${task.empName}님께서 <strong>"${task.taskTitle}"</strong>을(를) <strong>'${task.taskMethod}'</strong>하셨습니다.</p>
                                    <span class="vertical-timeline-element-date">${task.taskDate }</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    </div>
                 </div>
             </div>        
         </div>
	  </div>
	</div>
</div>

<!-- OffCanvas -->

<button style="display:none;" class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasScroll" aria-controls="offcanvasScroll"></button>
<div class="offcanvas offcanvas-end" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="offcanvasScroll" aria-labelledby="offcanvasScrollLabel">
  <div class="offcanvas-header">
    <h4 id="offcanvasScrollLabel" class="offcanvas-title">

    </h4>
    <div style="display: none;">
    <div style="display: flex; align-items: center;"> 
	       <div id="selectedColor" onclick="offCanvasColorClick();" style="width: 20px; height: 20px; margin-right: 8px; border: 1px solid #ccc; border-radius: 4px;">
	          
	       </div>
	   </div>
       <div class="dropdown-menu dropdown-menu-color p-2" aria-labelledby="colorDropdown">
           <div class="d-flex flex-wrap justify-content-between" style="gap: 6px;">
               <button class="color-option offcanvas-color" style="background-color: #8c77e6;" data-color="#8c77e6"></button>
               <button class="color-option offcanvas-color" style="background-color: #4787ff;" data-color="#4787ff"></button>
               <button class="color-option offcanvas-color" style="background-color: #41c685;" data-color="#41c685"></button>
               <button class="color-option offcanvas-color" style="background-color: #5cb6d9;" data-color="#5cb6d9"></button>
               <button class="color-option offcanvas-color" style="background-color: #f2c338;" data-color="#f2c338"></button>
               <button class="color-option offcanvas-color" style="background-color: #f35955;" data-color="#f35955"></button>
               <button class="color-option offcanvas-color" style="background-color: #727d90;" data-color="#727d90"></button>
               <button class="color-option offcanvas-color" style="background-color: #5a45ba;" data-color="#5a45ba"></button>
               <button class="color-option offcanvas-color" style="background-color: #0e4cdd;" data-color="#0e4cdd"></button>
               <button class="color-option offcanvas-color" style="background-color: #1d7348;" data-color="#1d7348"></button>
               <button class="color-option offcanvas-color" style="background-color: #1e6989;" data-color="#1e6989"></button>
               <button class="color-option offcanvas-color" style="background-color: #b23d05;" data-color="#b23d05"></button>
               <button class="color-option offcanvas-color" style="background-color: #ba2322;" data-color="#ba2322"></button>
               <button class="color-option offcanvas-color" style="background-color: #4f5c73;" data-color="#4f5c73"></button>
           </div>
       </div>
    </div>
       
    <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
  </div>
  <div class="offcanvas-body my-auto mx-0 flex-grow-0">
  <form id="taskDetailForm" method="post">
	 <div class="dropdown" style="display:none;">
          <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
              상태 선택
          </button>
          <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
              <li><a class="dropdown-item" href="#"><span class="badge bg-secondary">배정 대기중</span></a></li>
              <li><a class="dropdown-item" href="#"><span class="badge bg-secondary">예정</span></a></li>
              <li><a class="dropdown-item" href="#"><span class="badge bg-primary">진행중</span></a></li>
              <li><a class="dropdown-item" href="#"><span class="badge bg-danger">검토중</span></a></li>
              <li><a class="dropdown-item" href="#"><span class="badge bg-warning">수정중</span></a></li>
              <li><a class="dropdown-item" href="#"><span class="badge bg-success">완료</span></a></li>
          </ul>
      </div>
            <br/>

            <div class="input-group input-group-merge">
                <span class="input-group-text">일감 설명</span>
                <textarea class="form-control" aria-label="With textarea">${task.taskContent}</textarea>
            </div>
            <br/>

            <div class="accordion accordion-header-primary" id="accordionStyle1">
                <div class="accordion-item card">
                    <h2 class="accordion-header">
                        <button type="button" class="accordion-button collapsed" data-bs-toggle="collapse" data-bs-target="#accordionStyle1-1" aria-expanded="true">
                            세부사항
                        </button>
                    </h2>
                    <div id="accordionStyle1-1" class="accordion-collapse collapse show" data-bs-parent="#accordionStyle1">

                            <!-- Text Input -->
                            <div class="form-group row mb-3">
                                <label for="taskStatus" class="col-md-3 col-form-label text-md-end ps-4">일감 상태</label>
                                <div class="col-md-8">
									  <select class="form-select" id="taskStatus" aria-label="Default select example">
									    <option>상태 선택</option>
									    <option value="배정중">배정중</option>
									    <option value="예정">예정</option>
									    <option value="진행중">진행중</option>
									    <option value="검토중">검토중</option>
									    <option value="수정중">수정중</option>
									    <option value="완료">완료</option>
									  </select>
                                </div>
                            </div>

                            <!-- Week Input -->
                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">담당자</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="text"  id="projectMemberName" required="required" />
                                </div>
                            </div>
                            
                            <div class="form-group row mb-3">
                                <div class="col-md-4">
                                    <input class="form-control" name="projectMemberid" id="projectMemberid" type="text" required="required"  />
                                </div> 
                            </div>

                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">일감 제목</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="text" value="" id="html5-week-input" required="required" />
                                </div>
                            </div>


                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">시작 날짜</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="date" value="" id="html5-week-input" required="required" />
                                </div>
                            </div>

                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">종료 날짜</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="date" value="" id="html5-week-input" required="required" />
                                </div>
                            </div>


                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">진행률</label>
                                <div class="col-md-8">
                                    <select class="form-select" id="taskProgress">
                                        <option value="1">0%</option>
                                        <option value="10">10%</option>
                                        <option value="20">20%</option>
                                        <option value="30">30%</option>
                                        <option value="40">40%</option>
                                        <option value="50">50%</option>
                                        <option value="60">60%</option>
                                        <option value="70">70%</option>
                                        <option value="80">80%</option>
                                        <option value="90">90%</option>
                                        <option value="100">100%</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Color Input -->
                            <div class="form-group row mb-3">
                                <label for="html5-color-input" class="col-md-3 col-form-label text-md-end ps-4">Color</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="color" value="" id="html5-color-input" />
                                </div>
                            </div>
                    </div>
                </div>
            </div>


            <br/>
            <button type="submit" class="btn btn-primary mb-2">수정</button>
            <button type="button" style="background-color: #ff3e1d;">삭제</button>
            <button type="button" class="btn btn-secondary mb-2" data-bs-dismiss="offcanvas">취소</button>
	
            <br/>
            <br/>
            <br/>
  	</form>
  </div>
</div>


<!-- Modal -->
<div class="modal fade" id="largeModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div style="display: flex; align-items: center;">
           <div id="newTaskColor" onclick="ModalColorClick();" style="width: 20px; height: 20px; background-color: #8c77e6; margin-right: 8px; border: 1px solid #ccc; border-radius: 4px;"></div>
               <br/>
               <br/>
                <!-- 드롭다운 메뉴 -->
              <div class="dropdown-menu dropdown-menu-color-modal p-2" aria-labelledby="colorDropdown">
                  <div class="d-flex flex-wrap justify-content-between" style="gap: 6px;">
                      <button class="color-option modal-color" style="background-color: #8c77e6;" data-color="#8c77e6"></button>
                      <button class="color-option modal-color" style="background-color: #4787ff;" data-color="#4787ff"></button>
                      <button class="color-option modal-color" style="background-color: #41c685;" data-color="#41c685"></button>
                      <button class="color-option modal-color" style="background-color: #5cb6d9;" data-color="#5cb6d9"></button>
                      <button class="color-option modal-color" style="background-color: #f2c338;" data-color="#f2c338"></button>
                      <button class="color-option modal-color" style="background-color: #f35955;" data-color="#f35955"></button>
                      <button class="color-option modal-color" style="background-color: #727d90;" data-color="#727d90"></button>
                      <button class="color-option modal-color" style="background-color: #5a45ba;" data-color="#5a45ba"></button>
                      <button class="color-option modal-color" style="background-color: #0e4cdd;" data-color="#0e4cdd"></button>
                      <button class="color-option modal-color" style="background-color: #1d7348;" data-color="#1d7348"></button>
                      <button class="color-option modal-color" style="background-color: #1e6989;" data-color="#1e6989"></button>
                      <button class="color-option modal-color" style="background-color: #b23d05;" data-color="#b23d05"></button>
                      <button class="color-option modal-color" style="background-color: #ba2322;" data-color="#ba2322"></button>
                      <button class="color-option modal-color" style="background-color: #4f5c73;" data-color="#4f5c73"></button>
                  </div>
              </div>
        </div>
        <h5 class="modal-title" id="exampleModalLabel1"></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form method="post" id="addTaskForm">
      <div class="modal-body">
        <div class="row mb-3">
          <div class="col">
            <label for="taskNm" class="form-label">작업 명</label>
            <input type="text" id="taskNm" name="taskNm" class="form-control" placeholder="작업 명을 입력해 주세요." required>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col">
			  <label for="taskContent" class="form-label">작업 설명</label>
			  <textarea class="form-control" name="taskContent" id="taskContentModal" rows="3" maxlength="500" ></textarea>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col">
            <label for="taskSdate" class="form-label">작업 시작일</label>
            <input type="date" id="taskSdate" name="taskSdate" class="form-control" required>
          </div>
          <div class="col">
            <label for="taskEdate" class="form-label">작업 종료일</label>
            <input type="date" id="taskEdate" name="taskEdate" class="form-control" required>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col" style="display: none;">
            <input type="text" id="taskParentid" name="taskParentid" class="form-control" >
            <input type="text" id="taskModifyMemId" name="taskModifyMemId" class="form-control" value="${empId}">
          </div>
        </div>
	             <!-- Color Input -->
	     <div class="form-group row mb-3" style="display:none;">
	         <label for="html5-color-input" class="col-md-3 col-form-label text-md-end ps-4">Color</label>
	         <div class="col-md-8">
	             <input class="form-control" type="text" value="#8c77e6" name="taskColor" id="taskColor" required="required" />
	         </div>
	     </div>
      </div>
      <div class="modal-footer">
		<button type="button" class="btn btn-outline-dark btn-sm" onclick="addInputData();">데이터삽입</button>
        <button type="submit" class="btn btn-primary" onclick="addNewTask()">등록</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
      </div>
      </form>
    </div>
  </div>
</div>



<div class="modal fade" id="basicModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel2">일감 담당자 검색</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col mb-6">
          	<div class="input-group input-group-merge">
			    <span class="input-group-text" id="basic-addon-search31"><i class="bx bx-search"></i></span>
			    <input type="text" class="form-control" placeholder="이름을 입력하세요." aria-describedby="basic-addon-search31" />
			    <button class="btn btn-primary">검색</button>
		    </div>
          </div>
        </div>
        <br/>
        <div class="row g-6">
        <div class="card overflow-hidden mb-6" style="height: 300px;">
		  <div class="card-body" id="vertical-ex2">
		  	<div class="table-responsive">
			  <table class="table table-hover">
			    <thead>
			      <tr>
			        <th>부서</th>
			        <th>이름</th>
			        <th>직급</th>
			        <th>역할</th>
			      </tr>
			    </thead>
			    <tbody>
			    	<c:forEach items="${memberList}" var="mem">
							<tr>
					        <td>${mem.departName}</td>
					        <td id="projectMember-selecter" onclick="changeOffcanvasInput('${mem.empId}', '${mem.empName}')">${mem.empName }</td>
					        <td>${mem.codeComment }</td>
					        <td><span>${mem.projectRolenm}</span></td>
					      </tr>
			    	</c:forEach>  
			    </tbody>
			  </table>
			</div>
		  </div>
		</div>

        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal -->
<form id="projectMemberModifyForm">
<div class="modal fade" id="projectMemberModifyModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel1">참가자 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
	        <div class="row g-6">
	          <div class="col mb-0">
	            <label for="name" class="form-label">이름</label>
	            <input type="text" id="name" class="form-control" readonly style="background-color: white;"  required="required">
	          </div>
	          <div class="col mb-0">
	            <label for="department" class="form-label">부서</label>
	            <input type="text" id="department" class="form-control" readonly style="background-color: white;" required="required">
	          </div>
	          <div class="col mb-0">
	            <label for="position" class="form-label">직급</label>
	            <input type="text" id="position" class="form-control" readonly style="background-color: white;" required="required">
	          </div>
	        </div>
	        <div class="row">
	          <div class="col mb-6">
	            <label for="rolenm" class="form-label">역할</label>
	            <input type="text" id="rolenm" class="form-control" placeholder="역할을 입력해주세요." required="required">
	          </div>
	        </div>
	        <div class="row g-6">
	          <div class="col mb-0">
	            <label for="joinDate" class="form-label">투입일</label>
	            <input type="date" id="joinDate" class="form-control" required="required">
	          </div>
	          <div class="col mb-0">
	            <label for="leaveDate" class="form-label">투입 종료일</label>
	            <input type="date" id="leaveDate" class="form-control" required="required" >
	          </div>
	          <div class="col mb-0" style="display: none;">
	            <label for="projectMemberid" class="form-label"></label>
	            <input type="text" id="projectMemberid" class="form-control" readonly style="background-color: white;" required="required" >
	          </div>
	        </div>
	      </div>
	      <div class="modal-footer">
	        <button type="submit" class="btn btn-primary">수정</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
	      </div>
    </div>
  </div>
</div>
</form>


<script>
	const projSdate = new Date('${project.projSdate}');
	const projEdate = new Date('${project.projEdate}');
	const projectMemberList = '${memberList}';
	
	const projId = '${project.projId}';    
    // 조직도 갖고오기
    var teamHistoryUrl = '${pageContext.request.contextPath}/${companyId}/teamHistory/teams';
    // 사원 갖고오기
    var empUrl = '${pageContext.request.contextPath}/${companyId}/approval/employees';
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/project/projectDetail.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/project/projectDhtmlx.js"></script>
<script src="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.js"></script>
