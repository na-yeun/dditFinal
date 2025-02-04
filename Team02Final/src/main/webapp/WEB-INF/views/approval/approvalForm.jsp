<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal" />
    <c:set value="${principal.account.empId}"  var="empId"/>
    <c:set value="${principal.account.companyId }" var="companyId"/>
</security:authorize>

<%--CSS연결--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/approval/approvalForm.css">

<h3 id="page-title">결재 문서 작성</h3>
<hr/>

<%--우리는 타일즈 양식이니까 div컨테이너로 적절히 분배하는게 나아보임 이름구분을 명확히해서 구성하기--%>
<div class="approval-container">
    <!--양식 선택 영역 -->
    <div class="form-select-area">
        <div class="d-flex align-items-end gap-3"> <!-- align-items-end로 변경 -->
            <div class="select-box flex-grow-1">
                <label for="formSelect">결재 양식 선택</label>
                <select id="formSelect" class="form-select mb-0"> <!-- margin-bottom 제거 -->
                    <option value="">-- 양식을 선택하세요 --</option>
                    <c:forEach items="${formTitles}" var="form">
                        <option value="${form.apprformId}">${form.apprformNm}</option>
                    </c:forEach>
                </select>
            </div>
            <!-- 기안 관련 버튼 그룹 -->
            <div class="button-group d-flex gap-2" style="display: none;">
                <button type="button" class="btn btn-success" id="nextBtn">기안</button>
                <button type="button" class="btn btn-warning" id="modifyBtn">수정</button>
                <button type="button" class="btn btn-secondary" id="cancelBtn">취소</button>
            </div>
        </div>
        <!-- 결재선/참조자 지정 버튼 영역 (초기에 숨김) -->
        <div class="approval-button-area" style="display: none;">
            <div class="approval-buttons">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#approvalLineModal">결재선 지정</button>
                <button type="button" class="btn btn-primary" id="referenceBtn" style="display: none;" data-bs-toggle="modal" data-bs-target="#referenceModal">참조자 지정</button>
                <!-- 첨부파일 인풋을 버튼처럼 스타일링 -->
                <label for="attachmentInput" class="btn btn-primary" style="display: none;" id="attachmentLabel">
                    첨부파일
                    <input type="file" id="attachmentInput" multiple style="display: none;" />
                </label>
            </div>
        </div>
    </div>
    <!-- 참조자 리스트 영역 추가 -->
    <div class="reference-display-area" style="display: none;">
        <h6 class="mb-3">참조자 목록</h6>
        <div id="referenceDisplayList" class="reference-display-list"></div>
    </div>

    <!-- 첨부파일 목록 영역 -->
    <div class="attachment-display-area" style="display: none;">
        <h6 class="mb-3">첨부파일 목록</h6>
        <ul id="attachmentDisplayList" class="attachment-list"></ul>
    </div>


    <!--분기시점 - 휴가 신청서를 클릭했을때 -->
    <div id="vacationFormContainer" class="vacation-form-container" style="display: none;">
        <!-- 연차 현황 테이블 이거로 내 연차 현황을 확인 하는거임-->
        <div class="vacation-status">
            <h3>연차 현황</h3>
            <table class="vacation-table">
                <tr>
                    <th>총 연차</th>
                    <th>사용 연차</th>
                    <th>잔여 연차</th>
                    <th>병가 사용</th>
                </tr>
                <tr>
                    <td>-</td>
                    <td>-</td>
                    <td>-</td>
                    <td>-</td>
                </tr>
            </table>
        </div>

        <!-- 휴가 신청 양식 폼 -->
        <div class="vacation-input-form">
            <h3>휴가 신청</h3>
            <!-- 휴가신청서 -->
            <div class="form-group mb-2">
                <button type="button" id="vacationDataBtn" class="btn btn-secondary btn-sm">데이터 삽입</button>
            </div>
            <form id="vacationForm" class="form-styled">
                <div class="form-group">
                    <label for="vacationType">휴가 종류</label>
                    <select id="vacationType" class="form-control" name="vacCode" required></select>

                    <label for="startDate">휴가 기간</label>
                    <input type="date" id="startDate" class="form-control" name="vacStartdate" required>
                    ~
                    <input type="date" id="endDate" class="form-control" name="vacEnddate" required>
                </div>

                <!-- 상세 내용 에디터 -->
                <div class="form-group editor-container">
                    <textarea id="docContent" name="docContent" style="display:none;" required></textarea>
                    <div id="editor" class="form-editor"></div>
                </div>

                <button type="button" id="previewBtn" class="btn btn-primary">작성 미리보기</button>
            </form>
        </div>
    </div>

    <!-- 지출결의서 폼 영역 -->
    <div id="expenseFormContainer" class="expense-form-container" style="display: none;">
        <!-- 지출결의서 입력 폼 -->
        <div class="expense-input-form">
            <h3>지출결의서 작성</h3>
            <!-- 지출결의서 -->
            <div class="form-group mb-2">
                <button type="button" id="expenseDataBtn" class="btn btn-secondary btn-sm">데이터 삽입</button>
            </div>
            <!-- 제목 -->
            <div class="form-group mb-3">
                <label for="expenseName">지출결의서 제목</label>
                <input type="text" id="expenseName" class="form-control" name="expenseName" required
                       placeholder="지출결의서 제목을 입력하세요"
                       maxlength="30"
                       style="height: 40px; font-size: 16px;">
            </div>
            <form id="expenseForm" class="form-styled">
                <!-- 지출 구분 & 분류 -->
                <div class="form-group d-flex align-items-start gap-3" style="flex-direction: column;">
                    <label for="expenseType">지출 구분</label>
                    <div class="d-flex gap-3">
                        <select id="expenseType" class="form-control" name="expenseType" required
                                style="width: 200px; height: 40px; font-size: 16px;">
                            <option value="">선택하세요</option>
                        </select>
                        <button type="button" id="resetAllBtn" class="btn btn-secondary">초기화</button>
                    </div>
                </div>

                <!-- 지출 내역 테이블 -->
                <div class="form-group mt-4">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <label>지출 내역</label>
                        <button type="button" class="btn btn-primary btn-sm" id="addExpenseItem">
                            항목 추가
                        </button>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-bordered expense-items-table">
                            <thead>
                            <tr>
                                <th>지출일자</th>
                                <th>지출분류</th>
                                <th>결제수단</th>
                                <th>수량</th>
                                <th>단가</th>
                                <th>금액</th>
                                <th>내용</th>
                                <th>삭제</th>
                            </tr>
                            </thead>
                            <tbody id="expenseItemList">
                            <!-- 동적으로 항목이 추가될 영역 -->
                            </tbody>
                            <tfoot>
                            <tr>
                                <td colspan="5" class="text-end"><strong>총 계</strong></td>
                                <td id="totalExpenseAmount" class="text-end">0원</td>
                                <td colspan="2"></td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

                <!-- 상세 내용 에디터 -->
                <div class="form-group editor-container mt-4">
                    <label for="expenseDocContent">상세 내용</label>
                    <textarea id="expenseDocContent" name="docContent" style="display:none;" required></textarea>
                    <div id="expenseEditor" class="form-editor"></div>
                </div>

                <button type="button" id="expensePreviewBtn" class="btn btn-primary mt-3">작성 미리보기</button>
            </form>
        </div>
    </div>

    <!-- 자유양식서 폼 영역 -->
    <div id="freeFormContainer" class="free-form-container" style="display: none;">
        <div class="free-form-input">
            <h3>자유양식서 작성</h3>
            <!-- 자유양식서 -->
            <div class="form-group mb-2">
                <button type="button" id="freeFormDataBtn" class="btn btn-secondary btn-sm">데이터 삽입</button>
            </div>
            <form id="freeForm" class="form-styled">
                <div class="d-flex gap-3 mb-3">
                    <!-- 문서 제목 입력 -->
                    <div class="form-group flex-grow-1">
                        <label for="docTitle">문서 제목</label>
                        <input type="text" id="docTitle" class="form-control" name="docTitle" required
                               maxlength="30" placeholder="문서 제목을 입력하세요"
                               style="height: 40px; font-size: 16px;">
                    </div>
                    <!-- 목적/용도 입력 -->
                    <div class="form-group flex-grow-1">
                        <label for="purpose">목적/용도</label>
                        <input type="text" id="purpose" class="form-control" name="purpose" required
                               maxlength="100" placeholder="문서의 목적이나 용도를 입력하세요"
                               style="height: 40px; font-size: 16px;">
                    </div>
                </div>

                <!-- 날짜 선택 영역 -->
                <div class="mb-3">
                    <label class="mb-2">기간 설정</label>
                    <div class="date-selection-container">
                        <!-- 기간 타입 선택 -->
                        <div class="form-check form-check-inline mb-2">
                            <input class="form-check-input" type="radio" name="dateType" id="periodType" value="period" checked>
                            <label class="form-check-label" for="periodType">기간 지정</label>
                        </div>
                        <div class="form-check form-check-inline mb-2">
                            <input class="form-check-input" type="radio" name="dateType" id="singleType" value="single">
                            <label class="form-check-label" for="singleType">특정일 지정</label>
                        </div>

                        <!-- 기간 선택 (시작일-종료일) -->
                        <div id="periodSelection" class="d-flex gap-2 align-items-center">
                            <input type="date" id="freeStartDate" class="form-control" style="height: 40px;">
                            <span>~</span>
                            <input type="date" id="freeEndDate" class="form-control" style="height: 40px;">
                        </div>

                        <!-- 특정일 선택 -->
                        <div id="singleDateSelection" class="d-none">
                            <input type="date" id="singleDate" class="form-control" style="height: 40px;">
                        </div>
                    </div>
                </div>

                <!-- 상세 내용 에디터 -->
                <div class="form-group editor-container">
                    <label for="freeFormContent">상세 내용</label>
                    <textarea id="freeFormContent" name="docContent" style="display:none;" required></textarea>
                    <div id="freeFormEditor" class="form-editor"></div>
                </div>

                <button type="button" id="freeFormPreviewBtn" class="btn btn-primary mt-3">작성 미리보기</button>
            </form>
        </div>
    </div>

    <!-- 미리보기 영역 -->
    <div id="previewContainer" class="preview-container" style="display: none;">
        <!-- A4 문서가 표시될 영역 -->
        <div class="document-area">
            <!-- 여기에 문서 내용이 렌더링됨 -->
        </div>
    </div>
</div>

<!-- 모달 크기 확대 및 2안 레이아웃으로 수정 -->
<div class="modal fade" id="approvalLineModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <!-- 모달 헤더 -->
            <div class="modal-header">
                <h5 class="modal-title">결재라인 지정</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>


            <!-- 모달 바디 -->
            <div class="modal-body">
                <div class="row g-0">
                    <!-- 왼쪽: 조직도 트리 -->
                    <div class="col-6 pe-3">
                        <div class="org-tree">
                            <!-- 검색 영역 -->
                            <div class="search-area" style="padding-bottom: 10px;">
                                <div class="input-group">
                                    <input type="text"
                                           id="employeeSearch"
                                           class="form-control form-control-sm"
                                           placeholder="사원 이름 검색">
                                    <button class="btn btn-outline-primary btn-sm"
                                            type="button"
                                            id="searchBtn">
                                        검색
                                    </button>
                                    <button class="btn btn-outline-secondary btn-sm"
                                            type="button"
                                            id="clearSearch">
                                        초기화
                                    </button>
                                </div>
                                <!-- 검색 결과 표시 영역 -->
                                <div id="searchResult" class="search-result"
                                     style="display: none; padding-top: 5px; font-size: 0.875rem;">
                                    <small class="text-muted"></small>
                                </div>
                            </div>

                            <ul id="orgTree">
                                <!-- 기존 조직도 내용 -->
                            </ul>
                        </div>
                    </div>

                    <!-- 오른쪽: 결재선 관리 -->
                    <div class="col-6 ps-3">
                        <!-- 상단: 선택된 결재자 목록 -->
                        <div class="approval-manager mb-4">
                            <h6 class="mb-3">선택된 결재자</h6>
                            <!-- 즐겨찾기 제목 입력 영역 추가 -->
                            <div class="approval-line-title mb-3">
                                <input type="text"
                                       id="approvalLineTitle"
                                       class="form-control"
                                       maxlength="30"
                                       placeholder="결재선 제목을 입력하세요 (30자 이내)"
                                       required>
                            </div>
                            <div class="selected-approvers">
                                <table class="table table-sm">
                                    <thead>
                                    <tr>
                                        <th>순서</th>
                                        <th>부서</th>
                                        <th>직책/이름</th>
                                        <th>전결권</th>
                                        <th>기능</th>
                                    </tr>
                                    </thead>
                                    <tbody id="approverList">
                                    </tbody>
                                </table>
                            </div>
                            <div class="text-end mt-3">
                                <button type="button" class="btn btn-primary" id="saveApprovalLineBtn">결재선 저장</button>
                            </div>
                        </div>

                        <!-- 하단: 저장된 결재선 목록 -->
                        <div class="saved-lines">
                            <h6 class="mb-3">저장된 결재선</h6>
                            <div class="saved-lines-container">
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 모달 푸터 -->
            <div class="modal-footer">
                <div class="d-flex justify-content-end w-100 align-items-center gap-3">
                    <!-- 버튼 그룹 -->
                    <!-- 전결 허용 여부 -->
                    <div class="form-check form-switch">
                        <input type="hidden" name="allowFinalApproval" id="allowFinalApprovalHidden" value="N" />
                        <input class="form-check-input" type="checkbox" id="allowFinalApprovalCheckbox">
                        <label class="form-check-label" for="allowFinalApprovalCheckbox">전결 허용</label>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="button" class="btn btn-primary" id="confirmLineBtn" disabled>확인</button>
                        <button type="button" class="btn btn-secondary" id="cancelLineBtn" data-bs-dismiss="modal">취소</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 참조자 설정 모달임-->
<div class="modal fade" id="referenceModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5>참조자 지정</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!-- 왼쪽: 조직도 -->
                    <div class="col-6">
                        <div class="org-tree">
                            <ul id="refOrgTree"></ul>
                        </div>
                    </div>
                    <!-- 오른쪽: 선택된 참조자 -->
                    <div class="col-6">
                        <h6>선택된 참조자</h6>
                        <div id="referenceList" class="reference-list"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="confirmRefBtn">확인</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
            </div>
        </div>
    </div>
</div>

<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}" />
<input type="hidden" id="companyId" value="${companyId}" />

<script>
    // 전역 변수로 설정하여 모든 JS 파일에서 접근 가능하도록
    window.companyId = '${principal.account.companyId}';
    window.myEmpId = '${principal.account.empId}';
</script>

<!-- 다른 모듈들 먼저 로드 -->
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalFormCommon.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalEditor.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalAttachment.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalForm/approvalFormVacation.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalForm/approvalFormExpense.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalForm/approvalFormFree.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalPreview.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalLine/orgTree.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalLine/approvalLineView.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalLine/approvalLineManager.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalLine/referenceOrgTree.js"></script>

<!-- 마지막에 메인 스크립트 로드 -->
<script src="${pageContext.request.contextPath}/resources/js/app/approval/approvalForm.js"></script>

<script>


    // 휴가 현황 갖고오기
    var vacationStatusUrl = '${pageContext.request.contextPath}/${companyId}/approval/vacation/status';
    // 휴가 종류 갖고오기
    var vacationTypeUrl = '${pageContext.request.contextPath}/${companyId}/approval/vacation/types';
    // 지출 구분 갖고오기
    var expenseTypeUrl = '${pageContext.request.contextPath}/${companyId}/approval/expense/types'
    // 지출 분류 갖고오기
    var expenseCategoriesUrl = '${pageContext.request.contextPath}/${companyId}/approval/expense/categories';
    // 문서 미리보기 렌더링 해갖고 오기
    var preViewTypeUrl = '${pageContext.request.contextPath}/${companyId}/approval/{formId}/preview';
    // 결재선 미리보기 문서에 추가해갖고 오기
    var appendApproversUrl = '${pageContext.request.contextPath}/${companyId}/approval/{lineId}/appendApprovers';
    // 결재선 즐겨찾기 추가 해갖고 오기
    var saveApprovalLineUrl = '${pageContext.request.contextPath}/${companyId}/approval/line/save';
    // 저장된 결재선 조회해 갖고오기
    var getSavedApprlineUrl = '${pageContext.request.contextPath}/${companyId}/approval/line/saved';
    // 결재선 즐겨찾기 삭제 해갖고 오기
    var deleteApprovalLineUrl = '${pageContext.request.contextPath}/${companyId}/approval/line/{lineId}';
    // 기안으로 넘어가기
    var draftUrl = '${pageContext.request.contextPath}/${companyId}/approval/draft';
    // 조직도 갖고오기
    var orgTreeUrl = '${pageContext.request.contextPath}/${companyId}/approval/orgTree';
    // 사원 갖고오기
    var empUrl = '${pageContext.request.contextPath}/${companyId}/approval/employees';

    var detailUrl = '${pageContext.request.contextPath}/${companyId}/approval/detail';

    var documentListUrl = '${pageContext.request.contextPath}/${companyId}/approval/list';
</script>
