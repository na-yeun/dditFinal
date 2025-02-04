<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<h3 id="page-title">공지사항 게시글 작성</h3>
<hr/>
<style>
	table{
		width: 100%;
	}
	
	.btn-area{
		margin-bottom:12px;
	}
</style>
<%-- validation 메시지 --%>
<c:if test="${not empty message}">
    <div class="alert alert-danger">${message}</div>
</c:if>
<form:form id="notice-form" method="post" action="${pageContext.request.contextPath}/${companyId}/notice/new" enctype="multipart/form-data" modelAttribute="newNotice">
    <table>
        <tr>
            <th>공지 제목</th>
            <td>
                <form:input type="text" path="noticeName" cssClass="form-control" required="true"/>
                <form:errors path="noticeName" element="span" cssClass="text-danger"/>
            </td>
        </tr>
        <tr>
        <th>중요공지 여부</th>
            <td>
                <!-- Hidden input -->
                <input type="hidden" name="noticeImportant" id="noticeImportantHidden" value="N" />

                <!-- 부트스트랩 토글 스위치 -->
                <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" id="noticeImportantCheckbox">
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
            <th>첨부 파일</th>
            <td>
                <input type="file" name="uploadFiles" multiple class="form-control"/>
            </td>
        </tr>
    </table>
    <!-- 버튼 그룹을 table 밖으로 분리하고 우측 정렬 -->
    <div class="text-end mt-3 btn-area">
        <input type="submit" value="등록" class="btn btn-primary"/>
        <c:url value="/${companyId}/notice" var="listUrl" />
        <a href="${listUrl}" class="btn btn-secondary ms-2">목록으로</a>
    </div>
</form:form>

<%-- 에디터안에 이미지를 삽입시 자동적으로 이벤트가 일어나는 비동기 스크립트--%>
<script>
    // form.jsp에서
    var uploadImageUrl = '${pageContext.request.contextPath}/${companyId}/notice/atch/uploadImage';
</script>

<script>
    const checkbox = document.getElementById("noticeImportantCheckbox");
    const hiddenInput = document.getElementById("noticeImportantHidden");

    checkbox.addEventListener("change", () => {
        hiddenInput.value = checkbox.checked ? "Y" : "N";
    });
</script>

<!-- JavaScript 파일 연결 -->
<script src="${pageContext.request.contextPath}/resources/js/app/notice/noticeForm.js"></script>

