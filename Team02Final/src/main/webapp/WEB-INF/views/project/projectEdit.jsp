<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form"  prefix="form"%>
<h3 id="page-title">프로젝트 수정</h3>
<hr/>

<form:form id="project-form" method="post" 
           action="${pageContext.request.contextPath}/${companyId}/project/${targetProject.projId}/update" 
           modelAttribute="targetProject" enctype="application/x-www-form-urlencoded">

	<div class="mb-2">
	  <label for="projTitle" class="form-label">프로젝트 이름</label>
	  <form:input type="text" cssClass="form-control" id="projTitle" path="projTitle" placeholder="프로젝트 이름을 입력해주세요." />
	  <form:errors path="projTitle" cssClass="text-danger" />
	</div>
	
	<div class="mb-2">
	  <label for="projContent" class="form-label">프로젝트 내용</label>
	  <form:input type="text" cssClass="form-control" id="projContent" path="projContent" placeholder="어떤 프로젝트인지 간략하게 설명해주세요." />
	  <form:errors path="projContent" cssClass="text-danger" />
	</div>
	
	<div class="mb-2">
	  <label for="projSdate" class="form-label">프로젝트 시작일</label>
	  <form:input type="date" cssClass="form-control" id="projSdate" path="projSdate" />
	  <form:errors path="projSdate" cssClass="text-danger" />
	</div>
	
	<div class="mb-2">
	  <label for="projEdate" class="form-label">프로젝트 종료일</label>
	  <form:input type="date" cssClass="form-control" id="projEdate" path="projEdate" />
	  <form:errors path="projEdate" cssClass="text-danger" />
	</div>                                                                                                                                               
	
	<div>
		<input type="submit" class="btn btn-primary" value="추가"/>
		<button type="button" onclick="getList()" class="btn btn-primary">목록</button>
	</div>
</form:form>

<script>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal"/>
		<c:set value="${principal.account.empName }" var="empName"></c:set>                        
		<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
		<c:set value="${principal.account.empId}"  var="empId"></c:set>
		var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
		var companyId = '${companyId}'
	</security:authorize>
		
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/project/projectEdit.js"></script>