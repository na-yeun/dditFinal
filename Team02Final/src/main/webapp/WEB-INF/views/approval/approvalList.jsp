<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%--css--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/approval/approvalList.css"/>

<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal"/>
    <c:set value="${principal.account.companyId }" var="companyId"/>
</security:authorize>

<h3 id="page-title">결재문서함</h3>
<hr/>
<!-- 검색 영역 -->
<div class="search-area">
    <form:select path="condition.searchType" cssClass="form-control" id="searchType">
        <form:option value="" label="전체"/>
        <form:option value="title" label="제목" />
        <form:option value="docId" label="문서번호" />
        <form:option value="writer" label="기안자" />
    </form:select>
    <form:input path="condition.searchWord" cssClass="form-control" id="searchWord"/>
    <button type="button" id="search-btn" class="search-btn btn btn-primary">검색</button>
</div>

<!-- 탭 메뉴 -->
<ul class="nav nav-tabs mt-3" role="tablist">
    <li class="nav-item">
        <a class="nav-link active" data-bs-toggle="tab" href="#draft" role="tab">기안 문서</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-bs-toggle="tab" href="#approval" role="tab">결재 대기</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-bs-toggle="tab" href="#progress" role="tab">결재 현황</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-bs-toggle="tab" href="#reference" role="tab">참조 문서</a>
    </li>
</ul>

<!-- 탭 컨텐츠 -->
<div class="tab-content">
    <div class="tab-pane fade show active" id="draft">
        <div class="document-list">
            <table class="table">
                <thead>
                <tr>
                    <th>순번</th>
                    <th>문서번호</th>
                    <th>제목</th>
                    <th>기안자</th>
                    <th>기안일자</th>
                    <th>결재상태</th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${empty list}">
                    <tr>
                        <td colspan="6">문서가 없습니다.</td>
                    </tr>
                </c:if>
                <c:if test="${not empty list}">
                    <c:forEach items="${list}" var="document">
                        <tr class="document-list" data-doc-id="${document.docId}">
                            <td>${document.rnum}</td>
                            <td>${document.docId}</td>
                            <td>${document.docTitle}</td>
                            <td>${document.empName}</td>
                            <td>${document.createdDate}</td>
                            <td>
                                    <span class="badge rounded-pill
                                        ${document.docStatus eq '1' ? 'bg-secondary' :
                                        document.docStatus eq '2' ? 'bg-primary' :
                                        document.docStatus eq '3' ? 'bg-info' :
                                        document.docStatus eq '4' ? 'bg-danger' :
                                        'bg-success'}">
                                            ${document.docStatusName}
                                    </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
                </tbody>
            </table>
        </div>
        <div class="paging-area"></div>
    </div>
    <div class="tab-pane fade" id="approval">
        <div class="document-list"></div>
        <div class="paging-area"></div>
    </div>
    <div class="tab-pane fade" id="progress">
        <div class="document-list"></div>
        <div class="paging-area"></div>
    </div>
    <div class="tab-pane fade" id="reference">
        <div class="document-list"></div>
        <div class="paging-area"></div>
    </div>
</div>

<%--검색할때 용도로 담아두는 히든 창--%>
<form id="searchForm">
    <input type="hidden" name="searchType" />
    <input type="hidden" name="searchWord" />
    <input type="hidden" name="page" />
    <input type="hidden" name="tabId" />
</form>

<%--script--%>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalDocument/approvalList.js"></script>

<input type="hidden" id="companyId" value="${companyId}" />
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}" />
