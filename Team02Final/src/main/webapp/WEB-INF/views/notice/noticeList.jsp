<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<style>

    .search-bar-container {
        display: flex;
        justify-content: flex-end; /* 오른쪽 정렬 유지 */
        align-items: center;
        gap: 10px;
        flex-wrap: nowrap;
        width: 100%; /* 컨테이너 전체 너비 사용 */
    }

    /* td에 text-align: right 추가 */
    .right-text {
        text-align: right;
    }
    
    .no-notice{
    	text-align: center;
    }

    .search-area {
        display: flex;  /* 요소들을 가로로 배치 */
        align-items: center;  /* 요소들을 세로 중앙 정렬 */
        gap: 10px;  /* 요소들 사이의 간격 */
    }

    .search-area .input-group {
        width: auto;  /* 입력 그룹의 너비를 자동으로 설정 */
        display: flex;
    }

    /* 필요한 경우 기존 스타일 유지하면서 추가 */
    .search-area .form-select {
        width: 100px;
        height: 35px;
        font-size: 14px;
        padding: 5px;
    }

    .search-area .form-control {
        width: 200px;
        height: 35px;
        font-size: 14px;
    }

    .search-area .btn-primary {
        height: 35px;
        font-size: 14px;
        padding: 0 15px;
        white-space: nowrap;
    }

    .btn-primary {
        white-space: nowrap;
    }

    .table-responsive {
        margin-top: 20px;
    }
    
    .notice-list{
    	cursor: pointer;
    }
    
    .notice-list:hover{
    	background-color:rgba(24,28,33, 0.03);
    }
    
</style>

<h3 id="page-title">공지 사항</h3>
<hr/>
<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal" />
</security:authorize>
<div class="table-responsive">
    <table class="table">
        <thead>
        <tr>
            <td class="right-text" colspan="4">
                <div class="search-bar-container">
                    <div class="search-area">
                        <select name="searchType" class="form-select">
                            <option value="title">제목</option>
                        </select>
                        <div class="input-group input-group-merge">
                            <span class="input-group-text"><i class="bx bx-search"></i></span>
                            <input type="text" name="searchWord" class="form-control"
                                   placeholder="검색어를 입력해주세요." value="${simpleCondition.searchWord}"/>
                        </div>
                        <button type="button" id="searchBtn" class="btn btn-primary">검색</button>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${not empty noticeList}">
                <c:forEach items="${noticeList}" var="notice">
                    <tr class="notice-list" data-url="${pageContext.request.contextPath}/${principal.account.companyId}/notice/${notice.noticeNo}">
                        <td>${notice.rnum}</td>
                        <td>
                        	
                        	<security:authentication property="principal" var="principal" />
                        	
                            <c:url value="/${principal.account.companyId}/notice/${notice.noticeNo}" var="detailUrl" />
                            <span class="notice-link">
                                <c:choose>
                                    <c:when test="${notice.noticeImportant eq 'Y'}">
                                        <strong>🚨 ${notice.noticeName}</strong>
                                    </c:when>
                                    <c:otherwise>
                                        ${notice.noticeName}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>${notice.employee.empName}</td>
                        <td>${notice.noticeDate}</td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td class="no-notice" colspan="4">등록된 공지사항이 없습니다.</td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<div style="text-align: right; margin-top: 10px;">
	<c:if test="${principal.account.posiId == '7'}">
	    <c:url value="/${principal.account.companyId}/notice/newForm" var="writeUrl" />
	    <button type="button" class="btn btn-primary" onclick="location.href='${writeUrl}'" style="width: auto; padding: 10px 20px; font-size: 14px;">
	        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
	            <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
	            <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
	        </svg> 글쓰기
	    </button>
	</c:if>
</div>


<br/>

<!-- 페이지네이션 -->
<div class="paging-area">
    ${pagingHTML}
</div>


<div style="display: none;">
    <form id="searchForm">
        <input type="hidden" name="searchType" />
        <input type="hidden" name="searchWord" />
        <input type="hidden" name="page" />
    </form>
</div>

<script>
	document.addEventListener("DOMContentLoaded", () => {
        let noticeList = document.querySelectorAll('.notice-list');
        noticeList.forEach(element => {
            element.addEventListener("click", e => {
                let thisTr = e.target.closest('tr');
                let thisurl = thisTr.dataset.url;
                location.href = thisurl;
            })
        });

        // 검색 버튼 이벤트
        const searchBtn = document.querySelector('#searchBtn');
        searchBtn.addEventListener("click", () => {
            const searchForm = document.querySelector('#searchForm');
            const searchType = document.querySelector('select[name="searchType"]').value;
            const searchWord = document.querySelector('input[name="searchWord"]').value;

            // hidden 폼에 검색 조건 설정
            searchForm.querySelector('input[name="searchType"]').value = searchType;
            searchForm.querySelector('input[name="searchWord"]').value = searchWord;
            searchForm.querySelector('input[name="page"]').value = "1";

            // 폼 제출
            searchForm.submit();
        });
    });
</script>