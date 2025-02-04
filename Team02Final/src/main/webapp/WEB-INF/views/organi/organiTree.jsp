<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
<script src="https://cdn.jsdelivr.net/npm/apextree"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/organi/organiNode.css">


<h4 id="page-title">조직도</h4>
<hr/>

<div id="searchName">
	 
		<input type="text" id="empName" name="empName" placeholder="사원명 검색" class="form-control" style="margin-left:70px; width:200px;"/>
		<button class="btn btn-primary" id="searchBtn">검색</button>
</div>

	

<!-- <h4>조직도</h4> -->
<div class="d-flex justify-content-center align-items-center"
	style="height: 85vh;">
	<div id="organiTree"></div>   <!--  조직도 자리  -->
</div>

<div id="employeeListModal" class="modal fade bd-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
	<h3 class="modal-title" id="exampleModalLabel">직원 조회 결과</h3>    
    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div id="employeeListModalBody" class="modal-body">
        <!-- 비동기로 내용이 채워질 영역 -->
      </div>
    </div>
  </div>
</div>

   	<!-- 직원 정보가 모달로 띄워짐 -->
<div id="employeeModal" class="modal fade bd-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <div class="modal-header">
      <span style="cursor: pointer;" id="backToList">
        <i class="bx bx-arrow-back" style="font-size: 2rem; color: #555;"></i>
    </span>
        <h3 class="modal-title" id="exampleModalLabel">직원 상세 정보</h3>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div id="modalBody" class="modal-body">
        <!-- 비동기로 내용이 채워질 영역 -->
      </div>
    </div>
  </div>
</div>

   
   




<script> 

document.addEventListener("DOMContentLoaded", () => {
    // 서버에서 전달받은 DEPARTMENT 데이터를 JavaScript 객체 배열로 변환
    
    const departmentList = [
        <c:forEach items="${organiList}" var="dept" varStatus="status">
            {
                id: "${dept.departCode}",      // 부서 코드
                name: "${dept.departName}",    // 부서 이름
                parentId: "${dept.departParentcode}" // 상위 부서 코드
            }<c:if test="${!status.last}">,</c:if>  // 마지막이 아니면 , 를 붙여 (자바스크립트 내장요소)
            // 배열 문법 , 구분
        </c:forEach>
    ];

    /**
     * 부서 데이터를 트리 구조로 변환하는 함수
     * - 최상위 부서를 기준으로 하위 부서를 연결
     */
    function createTree(data) {
        const tree = [];   // 최상위 노드를 담을 배열
        const map = {};    // 모든 노드의 ID를 키로 저장
        
        // 모든 부서 데이터를 map 객체에 저장
        data.forEach(dept => {
            map[dept.id] = { ...dept, children: [] }; // 초기 children 배열 추가
            // ...dept 는 dept 객체의 모든 속성을 복사하여 새로운 객체 생성 (자바스크립트의 Spread Operator)
        });
        // 상위 부서에 자식 노드 연결
        data.forEach(dept => {
            if (dept.parentId) {
                // parentId가 있는 경우 상위 부서의 children에 현재 노드 추가
                map[dept.parentId].children.push(map[dept.id]);
            } else {
                // 최상위 노드 (parentId가 null)
                tree.push(map[dept.id]);
            }
        });
        return tree;
    }

    // 트리 구조 생성
    const treeData = createTree(departmentList);  // createTree 는 무조건 최상의노드가 null 이어야 함. 
    // 트리 렌더링 옵션
    const options = {
        width: 900,            // 트리의 너비
        height: 530,            // 트리의 높이
        nodeWidth: 150,         // 각 노드의 너비
        nodeHeight: 90,        // 각 노드의 높이
        childrenSpacing: 50,    // 부모와 자식 간의 간격
        siblingSpacing: 40,     // 형제 노드 간의 간격
        direction: 'top'        // 트리 방향: 위에서 아래로
    };

    // 트리 렌더링
    const tree = new ApexTree(document.getElementById('organiTree'), options);
    tree.render(treeData[0]); // 최상위 노드를 기준으로 렌더링
});
</script>

<script>const companyId = "${companyId}"
		const contextPath = "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath }/resources/js/organi/organiTree.js"></script>
