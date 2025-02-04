<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>


<style>
#lock-img{
	width: 17px;
	height: 17px;
	margin-bottom: 2px;
}
.search-bar-container {
    display: flex;
    justify-content: flex-end; /* 오른쪽 정렬 */
    align-items: center;
    margin-bottom: 20px;
    gap: 10px;
}

.pagination-bar {
    display: flex; /* flexbox로 설정 */
    justify-content: space-between; /* 양쪽 정렬 */
    align-items: center; /* 세로 축 중앙 정렬 */
    margin-top: 20px;
}

.paging-area {
    flex-grow: 1; /* 페이지네이션 영역이 버튼 영역과 겹치지 않도록 */
    text-align: center; /* 페이지네이션 버튼 중앙 정렬 */
}

#questionWrite {
    flex-shrink: 0; /* 문의 작성 버튼 크기 유지 */
    margin-left: 10px; /* 버튼과 페이지네이션 간 여백 */
}

.search-area {
    display: flex;
    align-items: center;
    gap: 10px; /* 내부 요소 간 간격 */
}

.search-area .form-select {
    width: 140px; /* 검색 필터 크기 */
}

.input-group {
    flex: 1;
}

.btn-primary {
    white-space: nowrap; /* 버튼 텍스트 줄바꿈 방지 */
}


#searchTypeTag{
	width: 140px; 
}

</style>
	
<h4 id="page-title">문의 게시판</h4>
<hr/>
<div class="search-bar-container">
    <div class="search-area" data-pg-target="#searchform" data-pg-name="fnPaging">
        <form:select path="condition.searchType" class="form-select" id="searchTypeTag" aria-label="Default select example">
            <form:option value="" label="전체"/>
            <form:option value="title" label="제목"/>
            <form:option value="writer" label="작성자"/>
        </form:select>
        <div class="input-group input-group-merge">
            <span class="input-group-text" id="basic-addon-search31"><i class="bx bx-search"></i></span>
            <form:input path="condition.searchWord" type="text" class="form-control" placeholder="검색어를 입력해주세요." aria-label="Search..." aria-describedby="basic-addon-search31" />
        </div>
        <button type="button" id="search-btn" class="btn btn-primary">검색</button>
    </div>
</div>
<div class="table-responsive">
    <table class="table">
        <thead>
            <tr>
                <th>번호</th>
                <th>답변여부</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${not empty QuestionList}">
                <c:forEach items="${QuestionList }" var="question">
                    <tr>
                        <td>${question.rnum}</td>
                        <c:choose>
                            <c:when test="${question.quYn eq 'Y' }">
                                <td><span class="badge bg-success">답변완료</span></td>
                            </c:when>
                            <c:otherwise>
                                 <td><span class="badge bg-warning">답변대기중</span></td>
                            </c:otherwise>
                        </c:choose>
                        <td>
                            <a href='<c:url value="/contract/question/${question.quNo}"/>'>${question.quTitle }</a>
                            <c:if test="${question.quSecretyn eq 'Y'}">
                                <img id="lock-img" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAXtJREFUSEvNlU1Kw0AUx/8v9eMA4qJLP0DQlVfQhR6geAB3oht1WkhiYkzaFEykKz2DguLaoldwpyAouBIX4gGU5ElCxagxk4aWOqshM/n95s28N0Poc6M+85FL4Lh+hYE1AAudBV0x8eGuWj2XLVAqsF3PA0ikgYjIMtTtvSxJpsBueCsgOokAzOzwMB9FfXqnDSLS4z6FS4Zaa/8lyRa4/jWAeWJsGbpoJSFO80Bn5jqAC1MTy0UFAQAlHMG4JcRLEtJotMoBBU8AXk1NjBUVcPSjqYnUSG3XzxyPtzDrgGQA2fhgBHHOE+pgzMhy/Ns44Y4YO4YmTpPff22R7fo3AGa7gn9NvjU1MScTxAdXtP1MiLQIBiY4Dt+U9SgyZTT0wFhNi7JwBCGVJix18zGCOs7+FJeU+94KhsKyVas9R1Cr2ZpUOHjoqQDMZ6ZerURQ2/UvASz2VpC4Mj4r+N8K+ltoneexCWC6q2LLe1V0Bc0xWfom52BkTvkAUFivGWrbWREAAAAASUVORK5CYII="/>
                            </c:if>
                        </td>
                        <td>${question.empName }</td>
                        <td>${question.quDate }</td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
		<tr>
		    <td colspan="5">
		        <div class="pagination-bar">
		            <!-- 페이지네이션 영역 -->
		            <div class="paging-area">
		                ${pagingHTML}
		            </div>
		            <!-- 문의 작성 버튼 -->
		            <button style="display: none;" type="button" class="btn btn-primary" id="questionWrite" 
		                data-lc-add="${pageContext.request.contextPath}/question/new">
		                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" 
		                    class="bi bi-pencil-square" viewBox="0 0 16 16">
		                    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
		                    <path fill-rule="evenodd" 
		                        d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
		                </svg>
		                문의 작성
		            </button>
		        </div>
		    </td>
		</tr>
    </table>
		<div style="display: none;">
			<form:form id="searchForm" method="get" modelAttribute="condition">
				<form:input path="searchType"/>
				<form:input path="searchWord"/>
				<input type="hidden" name="page"/>
			</form:form>
		</div>
	</div>

<script src="${pageContext.request.contextPath}/resources/js/app/utils/paging.js"></script>
