<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<style>
	table{
		width: 100%;
	}
	
	.origin-file-del-btn{
		margin-bottom: 5px;
    	margin-top: 5px;
	}
</style>


<h3 id="page-title">공지사항 게시글 수정</h3>
<hr/>

<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal"/>
    <c:set value="${principal.account.companyId }" var="companyId"/>
</security:authorize>

<script>
    // 기존 공지 내용
    var initialValue = `${targetNotice.noticeContent}`;
    var uploadImageUrl = '${pageContext.request.contextPath}/${companyId}/notice/atch/uploadImage';
</script>


<form:form id="updateForm" method="post" enctype="multipart/form-data" modelAttribute="targetNotice">
    <table>
        <tr>
            <th>공지 제목</th>
            <td><form:input type="text" path="noticeName" required="required" cssClass="form-control" />
                <form:errors path="noticeName" cssClass="text-danger" /></td>
        </tr>
        <tr>
            <th>중요 여부</th>
            <td>
                <!-- Hidden input: 체크박스 상태에 따라 Y 또는 N 값 설정 -->
                <input type="hidden" name="noticeImportant" id="noticeImportantHidden" value="${targetNotice.noticeImportant}" />

                <!-- 부트스트랩 토글 스위치 -->
                <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" id="noticeImportantCheckbox"
                        ${targetNotice.noticeImportant eq 'Y' ? 'checked' : ''}>
                    <label class="form-check-label" for="noticeImportantCheckbox">중요</label>
                </div>
            </td>
        </tr>
        <tr>
            <th>공지 내용</th>
            <td>
                <!-- 숨겨둔 textarea (서버로 전송할 값) -->
                <form:textarea path="noticeContent" cssClass="form-control"
                               id="noticeContent" style="display:none;"/>
                <form:errors path="noticeContent" cssClass="text-danger" />

                <!-- Toast UI Editor가 표시될 영역 -->
                <div id="editor"></div>
            </td>
        </tr>
        <tr>
            <th>기존 파일</th>
            <td>
                <c:forEach items="${targetNotice.atchFile.fileDetails}" var="fd" varStatus="vs">
                    <span>
                        ${fd.orignlFileNm} [${fd.fileFancysize}]
                        <a data-atch-file-id="${fd.atchFileId}" data-file-sn="${fd.fileSn}" class="btn btn-danger btn-sm origin-file-del-btn" href="javascript:;">
                            삭제
                        </a>
                        ${not vs.last ? '|' : ''}
                    </span>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <th>새 파일 업로드</th>
            <td>
                <input type="file" name="uploadFiles" multiple class="form-control" />
            </td>
        </tr>

    </table>
    <!-- 버튼 그룹 우측 정렬 -->
    <div class="text-end mt-3">
        <input type="submit" value="저장" class="btn btn-primary"/>
        <c:url value="/${companyId}/notice" var="listUrl" />
        <a href="${listUrl}" class="btn btn-secondary ms-2">목록으로</a>
    </div>
</form:form>

<script>
    window.contextPath = "${pageContext.request.contextPath}";
    window.companyId = "${companyId}";
    window.noticeNo = "${targetNotice.noticeNo}";
    console.log("JSP에서의 noticeNo:", noticeNo); // noticeNo 값 확인
</script>


<!-- JavaScript 파일 연결 -->
<script src="${pageContext.request.contextPath}/resources/js/app/notice/noticeEdit.js"></script>
