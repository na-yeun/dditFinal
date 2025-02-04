<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/employee/employeeMyPage.css">
<h4 id="page-title">마이페이지</h4>
<hr/>


<br>
<a class="my-page-a" href="javascript:void(0)" id="edit-myInfo">➡️나의정보수정</a>
<br>
<a class="my-page-a" href="${pageContext.request.contextPath}/a001/mypage/myAttendance">➡️나의 근태</a>
<br>
<a class="my-page-a" href="${pageContext.request.contextPath}/a001/mypage/myVacation">➡️나의 휴가</a>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/employee/employeeMyPage.js">
</script>