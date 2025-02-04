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

<h4 id="page-title">부서 관리</h4>
<hr/>

<div class="justify-content-center align-items-center"
	style="height: 85vh;">
	<div>
		<p style="color:red;">부서(팀)을 클릭하면 추가, 수정, 삭제가 가능합니다.</p>
	</div>
		
	
	<div id="organiTree" title="부서(팀)을 클릭하면 추가, 수정, 삭제가 가능합니다."></div>   <!--  조직도 자리  -->
</div>

<script>
	const companyId = "${companyId}"
	const contextPath = "${pageContext.request.contextPath}";
</script>


<script src="${pageContext.request.contextPath }/resources/js/organi/organiSystem.js"></script>


