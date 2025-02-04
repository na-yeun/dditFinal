<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<style>
	
	.button-group{
		margin-bottom:12px;
	}
	
</style>
<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal"/>
    <c:set value="${principal.account.companyId }" var="companyId"/>
</security:authorize>

<h3 id="page-title">공지사항 게시판 상세</h3>
<hr/>
<table class="table">
    <tbody>
    <tr>
        <th>글번호</th>
        <td>${notice.noticeNo}</td>
        <th>작성일</th>
        <td>${notice.noticeDate}</td>
    </tr>
    <tr>
        <th>제목</th>
        <td>${notice.noticeName}</td>
        <th>작성자</th>
        <td>${notice.employee.empName}</td>
    </tr>
    <tr>
        <th>내용</th>
        <td colspan="3">
<%--            <div class="border p-4 rounded">--%>
                ${notice.noticeContent}
<%--            </div>--%>
        </td>
    </tr>
    <tr>
        <th>첨부파일</th>
        <td colspan="3">
            <c:forEach items="${notice.atchFile.fileDetails}" var="fd">
                <div class="mb-1">
                    <c:url value="/${companyId}/notice/${notice.noticeNo}/atch/${fd.atchFileId}/${fd.fileSn}" var="downUrl" />
                    <a href="${downUrl}" class="text-decoration-none">
                        <i class="fas fa-download me-2"></i>${fd.orignlFileNm} (${fd.fileFancysize})
                    </a>
                </div>
            </c:forEach>
        </td>
    </tr>
    </tbody>
</table>

<!-- 버튼 그룹 -->
<div class="button-group text-end mt-3">
    <security:authorize access="isAuthenticated()">
        <security:authentication property="principal" var="principal" />
        <c:if test="${principal.account.posiId == '7'}">

            <c:url value="/${companyId}/notice/${notice.noticeNo}/edit" var="editUrl" />
            <a href="${editUrl}" class="btn btn-primary">수정</a>

            <!-- 삭제 버튼 폼 -->
            <form class="d-inline-block ms-2 delete-form"
                  action="${pageContext.request.contextPath}/${companyId}/notice/${notice.noticeNo}/delete"
                  method="post">
                <input type="hidden" name="_method" value="DELETE" />
                <button type="submit" class="btn btn-danger">삭제</button>
            </form>

        </c:if>
    </security:authorize>
    <c:url value="/${companyId}/notice" var="listUrl" />
    <a href="${listUrl}" class="btn btn-secondary ms-2">목록</a>
</div>

<script src="${pageContext.request.contextPath}/resources/js/app/notice/noticeDetail.js"></script>