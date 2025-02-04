<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<%--css--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/approval/approvalDetail.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/approval/approvalForm.css"/>

<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal"/>
    <c:set value="${principal.account.companyId }" var="companyId"/>
    <c:set value="${principal.account.empId }" var="empId"/>
    <input type="hidden" id="companyId" value="${companyId}" />
</security:authorize>

<div class="approval-detail-container">
    <br/>
    <h3 class="page-title">결재 문서 상세</h3>

    <hr/>
    <!-- 문서 기본 정보 테이블 -->
    <table class="table table-bordered">
        <tbody>
        <tr>
            <th style="width: 15%">문서번호</th>
            <td>${document.docId}</td>
            <th style="width: 15%">문서제목</th>
            <td>${document.docTitle}</td>
        </tr>
        <tr>
            <th>기안자</th>
            <td>${document.empName}</td>
            <th>문서상태</th>
            <td>
                <span class="badge rounded-pill ${document.docStatus eq '1' ? 'bg-secondary' :
                    document.docStatus eq '2' ? 'bg-primary' :
                    document.docStatus eq '3' ? 'bg-info' :
                    document.docStatus eq '4' ? 'bg-danger' :
                    'bg-success'}">
                    ${document.docStatusName}
                </span>
            </td>
        </tr>
        <tr>
            <th>등록일시</th>
            <td>${document.createdDate}</td>
            <th>보존기한</th>
            <td>${document.docPreserve}</td>
        </tr>
        <tr>
            <th>결재 관련 정보</th>
            <td colspan="3">
                <div class="mb-3">
                    <span class="me-4">
                        문서 전결 허용여부:
                        <span class="badge ${document.allowDelegation eq 'Y' ? 'bg-success' : 'bg-danger'}">
                            ${document.allowDelegation eq 'Y' ? '허용' : '불가'}
                        </span>
                    </span>
                    <span>
                        전결권 보유여부(본인):
                        <span class="badge ${approverFinalYn eq 'Y' ? 'bg-success' : 'bg-secondary'}">
                            ${approverFinalYn eq 'Y' ? '보유' : '미보유'}
                        </span>
                    </span>
                </div>
            </td>
        </tr>
        <tr>
            <th>결재선</th>
            <td colspan="3">
                <div class="approval-line">
                    <c:forEach items="${document.approvalList}" var="approval" varStatus="status">
                        <span class="approval-step">
                            <span class="badge bg-secondary">${status.count}차</span>
                            <span class="mx-1">${approval.empName}</span>
                            <span class="badge ${approval.approvalStatus eq '1' ? 'bg-secondary' :
                                approval.approvalStatus eq '2' ? 'bg-primary' :
                                approval.approvalStatus eq '3' ? 'bg-success' :
                                approval.approvalStatus eq '4' ? 'bg-danger' :
                                'bg-info'}">
                                    ${approval.approvalStatusName}
                            </span>
                        </span>
                        <c:if test="${!status.last}">
                            <i class="fas fa-arrow-right mx-2"></i>
                        </c:if>
                    </c:forEach>
                </div>
            </td>
        </tr>
        <tr>
            <th>참조자</th>
            <td colspan="3">
                <div class="reference-line">
                    <c:forEach items="${document.referenceList}" var="reference" varStatus="status">
                <span class="reference-user">
                    ${reference.empName}
                    <span class="badge ${reference.readStatusYn eq 'Y' ? 'bg-success' : 'bg-secondary'}">
                            ${reference.statusName}
                    </span>
                    <c:if test="${reference.readStatusYn eq 'Y'}">
                        <small class="text-muted">
                            (${reference.readDate})
                        </small>
                    </c:if>
                </span>
                        <c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <!-- 첨부파일 영역 -->
    <div class="attachments-container mt-4">
        <h5>첨부파일</h5>
        <div class="list-group">
            <c:forEach items="${document.atchFile.fileDetails}" var="fd">
                <c:url value="/${companyId}/approval/${document.docId}/atch/${fd.atchFileId}/${fd.fileSn}" var="downUrl" />
                <a href="${downUrl}" class="list-group-item list-group-item-action">
                        ${fd.orignlFileNm} (${fd.fileFancysize})
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- 결재 버튼 영역 -->
    <div class="approval-actions mt-4 text-center">
        <c:if test="${isCurrentApprover}">
            <button type="button" class="btn btn-success" id="approveBtn">결재승인</button>
            <button type="button" class="btn btn-danger" id="rejectBtn">결재반려</button>
            <c:if test="${approverFinalYn eq 'Y' and document.allowDelegation eq 'Y'}">
                <button type="button" class="btn btn-warning" id="finalApproveBtn">전결처리</button>
            </c:if>
            <button type="button" class="btn btn-primary" id="commentBtn">의견작성</button>
        </c:if>
    </div>

    <!-- 문서 내용 -->
    <div class="document-content mt-4" >
        <h5>문서 내용</h5>
        <div class="border p-4 rounded" id="pdfContent">
            ${document.docContent}
        </div>
    </div>

    <!-- 결재 이력 -->
    <div class="approval-history mt-4">
        <h5>결재 이력</h5>
        <div class="border p-3 rounded">
            <c:forEach items="${document.approvalList}" var="approval">
                <c:if test="${not empty approval.approvalComment}">
                    <div class="approval-comment mb-3">
                        <div class="d-flex justify-content-between">
                            <strong>${approval.empName}</strong>
                            <span class="text-muted">${approval.approvalDate}</span>
                        </div>
                        <div class="mt-1">
                            <span class="badge ${approval.approvalStatus eq '3' ? 'bg-success' :
                                    approval.approvalStatus eq '4' ? 'bg-danger' :
                                    approval.approvalStatus eq '6' ? 'bg-warning' : 'bg-secondary'}">
                                    ${approval.approvalStatusName}
                            </span>
                        </div>
                        <div class="mt-2">${approval.approvalComment}</div>
                        <hr/>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <!-- 하단 버튼 영역 -->
    <div class="bottom-actions mt-4 d-flex justify-content-end">
        <button type="button" class="btn btn-primary me-2" id="save-pdf-btn">문서저장</button>

        <c:if test="${canDelete}">
            <button type="button" class="btn btn-danger me-2" id="deleteBtn">삭제</button>
        </c:if>
        <button type="button" class="btn btn-secondary" id="listBtn">목록</button>
    </div>
    <br/>
    <br/>
</div>

<!-- 결재 의견 모달 -->
<div class="modal fade" id="commentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">결재 의견 작성</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <textarea class="form-control"
                          id="approvalComment"
                          rows="3"
                          maxlength="2000"
                          placeholder="의견을 입력해주세요 (최대 2000자)"></textarea>
                <div class="text-end mt-2">
                    <small class="text-muted"><span id="commentLength">0</span>/2000</small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="saveCommentBtn">저장</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
            </div>
        </div>
    </div>
</div>



<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}" />
<input type="hidden" id="docId" value="${document.docId}" />
<input type="hidden" id="docStatus" value="${document.docStatus}" />

<script src="${pageContext.request.contextPath}/resources/js/app/common/jspdf.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/common/pdfDownload.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/common/html2canvas.min.js"></script>

<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalDocument/approvalDetail.js"></script>
