<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
   
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/attendance/attendanceSystem.css">
    <h4 id="page-title">근태 관리</h4>
    <hr>

    <div class="filter-container">
	  <button type="button" class="search-btn btn btn-outline-primary" id="reload-btn">검색 초기화</button>
	
        <!-- 직급 -->
        <label for="posi"><strong>직급</strong>
            <select id="selectPosi" class="form-select">

            </select>
        </label>
        <!-- 부서 -->
        <label for="depart"><strong>부서</strong>
            <select id="selectDepart" class="form-select">

            </select>
        </label>



        <!-- 조회 일자 -->
        <label for="atthisId" class="date-filter">
            <strong>조회 날짜(시작)</strong>
            <input type="date" id="startJoinDate" class="form-control">
        </label>
        
        <label for="atthisId" class="date-filter">
        	<strong>조회 날짜(종료)</strong>
             <input type="date" id="endJoinDate" class="form-control">
        </label>

        <div class="button-container d-flex gap-2" style="margin-top:30px;">
            <button  id="downloadBtn" class="btn btn-success btn-sm-custom">
                <i class="fas fa-download"></i> 근태기록 다운로드
            </button>
        </div>  

        <!-- 검색창 -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="사원명을 입력하세요">
            <button class="searchBtn btn btn-primary" style="margin-top:30px;">검색</button>
            <!--       <span class="search-icon"> -->
            <!--         <i class='bx bx-search'></i> -->
            <!--       </span> -->
        </div>
    </div>

    <div class="table">
        <table class="table table-striped" id="attendanceTable">
            <thead>
                <tr>
                    <th data-column="date">
                        <div class="th-content">
                            날짜
                            <span class="arrow">▼</span>
                        </div>
                    </th>
                    <th data-column="name">
                        <div class="th-content">
                            사원명
                            <span class="arrow">▼</span>
                        </div>
                    </th>
                    <th data-column="posi">
                        <div class="th-content">
                            직급
                            <span class="arrow">▼</span>
                        </div>
                    </th>
                    <th data-column="depart">
                        <div class="th-content">
                            부서
                            <span class="arrow">▼</span>
                        </div>
                    </th>
<!--                     <th data-column="stime"> -->
<!--                         <div class="th-content"> -->
<!--                             출근 시간 -->
<!--                             <span class="arrow">▼</span> -->
<!--                         </div> -->
<!--                     </th> -->
                    <th data-column="sStatus">
                        <div class="th-content">
                            출근 상태
                            <span class="arrow">▼</span>
                        </div>
                    </th>
                    <th data-column="eStatus">
                        <div class="th-content">
                            퇴근 상태
                            <span class="arrow">▼</span>
                        </div>
                    </th>
<!--                     <th data-column="etime"> -->
<!--                         <div class="th-content"> -->
<!--                             퇴근 시간 -->
<!--                             <span class="arrow">▼</span> -->
<!--                         </div> -->
<!--                     </th> -->
                    <th data-column="overYN">
                        <div class="th-content">
                            초과 근무 여부
                            <span class="arrow">▼</span>
                        </div>
                    </th>
<!--                     <th data-column="overMin"> -->
<!--                         <div class="th-content"> -->
<!--                             초과 근무 시간(분) -->
<!--                             <span class="arrow">▼</span> -->
<!--                         </div> -->
<!--                     </th> -->
                    
                    
<!--                     <th data-column="cause"> -->
<!--                         <div class="th-content"> -->
<!--                             지각 사유 -->
<!--                             <span class="arrow">▼</span> -->
<!--                         </div> -->
<!--                     </th> -->
<!--                     <th data-column="cause"> -->
<!--                         <div class="th-content"> -->
<!--                             조퇴 사유 -->
<!--                             <span class="arrow">▼</span> -->
<!--                         </div> -->
<!--                     </th> -->
                </tr>
            </thead>

            <tbody id="innerTbody">
                <!-- 근태 기록이 들어가야 함-->
                <c:if test="${not empty list }">
                	<c:forEach items="${list }" var="att">
                		<tr class="rowOne" data-emp-id="${att.emp.empId}" data-atthis-id="${att.atthisId}">
	                        <td class="list-td">${att.atthisId.substring(0, 4)}-${att.atthisId.substring(4, 6)}-${att.atthisId.substring(6, 8)}</td>
	                        <td class="list-td">${att.emp.empName}</td>
	                        <td class="list-td">${att.posiName}</td>
	                        <td class="list-td">${att.departName}</td>
<%-- 	                        <td class="list-td">${att.hahisTime}</td> --%>
	                        <td class="list-td">
	                        	<c:if test="${att.attstaIdIn eq '정상출근' or att.attstaIdIn eq '출장'}">
									<p class="badge bg-primary">${att.attstaIdIn}</p>
								</c:if>
								<c:if test="${att.attstaIdIn eq '지각'}">
									<p class="badge bg-warning">${att.attstaIdIn}</p>
								</c:if>
								<c:if test="${att.attstaIdIn eq '무단결근'}">
									<p class="badge bg-danger">${att.attstaIdIn}</p>
								</c:if>
								<c:if test="${att.attstaIdIn eq '휴가' or att.attstaIdIn eq '반차'}">
									<p class="badge bg-secondary">${att.attstaIdIn}</p>
								</c:if>
	                        </td>
	                        <td class="list-td">
		                        <c:choose>
								    <c:when test="${empty att.attstaIdOut}">
								        -
								    </c:when>
								    <c:otherwise>
								    	<c:if test="${att.attstaIdOut eq '조퇴'}">
											<p class="badge bg-secondary">${att.attstaIdOut}</p>
										</c:if>
								    	<c:if test="${att.attstaIdOut eq '정상퇴근'}">
											<p class="badge bg-primary">${att.attstaIdOut}</p>
										</c:if>
								    	<c:if test="${att.attstaIdOut eq '연장근무'}">
											<p class="badge bg-success">${att.attstaIdOut}</p>
										</c:if>
								    </c:otherwise>
							    </c:choose>
	                        </td>
<!-- 	                        <td class="list-td"> -->
<%-- 		                        <c:choose> --%>
<%-- 								    <c:when test="${empty att.hleaveTime}"> --%>
<!-- 								        - -->
<%-- 								    </c:when> --%>
<%-- 								    <c:otherwise> --%>
<%-- 								        ${att.hleaveTime} --%>
<%-- 								    </c:otherwise> --%>
<%-- 							    </c:choose> --%>
<!-- 	                        </td> -->
	                        
	                        <!-- 초과근무여부 -->
	                        <td class="list-td">
	                        	<c:if test="${att.atthisOverYn eq 'N'}">
	                        		<span>${att.atthisOverYn}</span>
	                        	</c:if>
	                        	<c:if test="${att.atthisOverYn eq 'Y'}">
	                        		<span class="atthisOver_Y">${att.atthisOverYn}</span>
	                        	</c:if>
	                        </td>
                        <!-- 초과근무시간 -->
<!--                         <td> -->
<%--                         <c:if test="${att.atthisOver == 0 }"> --%>
<!--                         	- -->
<%--                         </c:if> --%>
<%--                         <c:if test="${att.atthisOver != 0 }"> --%>
<%--                         ${att.atthisOver} --%>
<%-- 						</c:if> --%>
<!--                         </td> -->
                        
                        <!-- 지각사유 -->
<!--                         <td> -->
<%-- 	                       <c:choose> --%>
<%-- 							    <c:when test="${empty att.atthisCause}"> --%>
<!-- 							        - -->
<%-- 							    </c:when> --%>
<%-- 							    <c:otherwise> --%>
<%-- 							        ${att.atthisCause} --%>
<%-- 							    </c:otherwise> --%>
<%-- 						   </c:choose> --%>
<!--                         </td> -->
                        
                        <!-- 조퇴사유 -->
<!--                         <td> -->
<%-- 	                        <c:choose> --%>
<%-- 							    <c:when test="${empty att.earlyLeaveCause}"> --%>
<!-- 							        - -->
<%-- 							    </c:when> --%>
<%-- 							    <c:otherwise> --%>
<%-- 							        ${att.earlyLeaveCause} --%>
<%-- 							    </c:otherwise> --%>
<%-- 						    </c:choose> --%>
<!--                         </td> -->
                    	</tr>
                	
                	</c:forEach>
                </c:if>
                <c:if test="${empty list }">
                	<td colspan="11">조회할 내역 없음</td>
                </c:if>
            </tbody>
            <tfoot>
			<tr>
				<td colspan="11">
					<div class="paging-area">
						${pagingHtml}
				        <button type="button" id="changeTimeBtn" class="btn btn-primary" style="margin-right:10px;">출*퇴근시간 변경</button>
					</div>		
				</td>
			</tr>
		</tfoot>
        </table>
    </div>
<form id="searchForm" method="get" action="${pageContext.request.contextPath }/${companyId}/hr/attendance">
    <input type="hidden" name="startDate" value="${startDate}" />
    <input type="hidden" name="endDate" value="${endDate}" />
    <input type="hidden" name="department" value="${department}" />
    <input type="hidden" name="position" value="${position}" />
    <input type="hidden" name="searchWord" value="${searchWord}" />
    <input type="hidden" name="page" value="${page}" />
</form>
    
    
    
    
    <div id="modAttendTimeModal" class="modal fade bd-example-modal-sm"
	        tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	        aria-hidden="true">   
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<h4>출퇴근 시간 수정</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="modAttendTimeModalBody" class="modal-body table-responsive">
                
                    
			</div>

				<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="modATBtn" class="btn btn-primary">
				        수정
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>

<div id="attendEmpModal" class="modal fade bd-example-modal"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4>근태 상세 보기</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="attendEmpModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="modAttEmpBtn" class="btn btn-primary">
				        수정
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-secondary" data-bs-dismiss="modal">
				        닫기
				    </button>
			</div>		
		</div>
	</div>
</div>




    <script>
        const contextPath = "${pageContext.request.contextPath}";
        const companyId = "${companyId}";
    </script>
    <script src="${pageContext.request.contextPath }/resources/js/app/attendance/attendanceSystem.js"></script>