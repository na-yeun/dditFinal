<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<script>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal"/>
		<c:set value="${principal.account.empName }" var="empName"></c:set>                        
		<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
		<c:set value="${principal.account.empId}"  var="empId"></c:set>
		var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
		//	var companyId = '${companyId}'
	</security:authorize>
	var initialValue = `${targetQuestion.quContent}`;
</script>

<h4 id="page-title">문의 게시글 수정</h4>
<hr/>
	<div class="table-responsive">
	<form:form method="post" action="${pageContext.request.contextPath}/${companyId}/question/${targetQuestion.quNo}/edit" enctype="multipart/form-data" modelAttribute="targetQuestion" id="quForm">
		<table class="table table-borderless">
			<tr>
				<td colspan="2">
					<div class="mb-4">
					  <label for="exampleFormControlInput1" class="form-label">제목</label>
					  <form:input type="text" path="quTitle" id="quTitle" cssClass="form-control" placeholder="제목을 입력하세요" />
					  <form:errors path="quTitle" cssClass="text-danger"/>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:if test="${not empty categoryList }">
						<div class="mb-4">
						  <label for="exampleFormControlSelect1" class="form-label">문의 카테고리 선택</label>
						  <form:select path="goryId" class="form-select" id="exampleFormControlSelect1" aria-label="Default select example">
							<option value="" label="문의하실 내용의 주제를 선택해 주세요.">
							<c:forEach items="${categoryList}" var="category">
							    <option value="${category.goryId }"
							        <c:if test="${category.goryId == targetQuestion.goryId}">
							            selected="selected"
							        </c:if>
							    >  
							        ${category.goryNm}
							    </option>
							</c:forEach>
						  </form:select>
						</div>
					</c:if>
				</td>
			</tr>
			<c:if test="${not empty targetQuestion.atchFile }">
				<tr>
					<td>
						<div class="mb-3">
						  <label for="formFileMultiple" class="form-label">기존파일</label>
							<c:forEach items="${targetQuestion.atchFile.fileDetails }" var="fd" varStatus="vs">
								<span>
									${fd.orignlFileNm }[${fd.fileFancysize }]
									<a data-atch-file-id="${fd.atchFileId }" data-file-sn="${fd.fileSn }" class="btn btn-danger" href="javascript:;">
										삭제						
									</a>
									${not vs.last ? '|' : ''}
								</span>
							</c:forEach>
						</div>
					</td>
				</tr>
			</c:if>
			
	        <tr>
	            <td>
	            	  <label for="formFileMultiple" class="form-label">새 파일 업로드</label>
	                <input type="file" name="uploadFiles" multiple class="form-control" />
	            </td>
	        </tr>
			
			<tr>
				<td colspan="2">
					<div class="form-check form-switch">
					  <input id="questionSecretynCheckbox" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault">
					  <label class="form-check-label" for="questionSecretynCheckbox">문의 게시글 비공개 여부</label>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
	                <!-- 숨겨둔 textarea (서버로 전송할 값) -->
	                <form:textarea path="quContent" cssClass="form-control"
	                               id="quContent" style="display:none;"/>
					<form:errors path="quContent" element="span" cssClass="text-danger"/>
	
	                <!-- Toast UI Editor가 표시될 영역 -->
	                <div id="editor"></div>
				</td>
			</tr>
			<tr>
	            <td colspan="2">
                <form:input type="hidden" path="quSecretyn" id="questionSecretynHidden" value="${targetQuestion.quSecretyn}" />
	            </td>
	        </tr>
			<tr>
			<td colspan="2">
				<div style="text-align: right;">
					<input type="submit" value="수정" class="btn btn-primary" />
					<button type="button" class="btn btn-secondary" onclick="gotoquDetail('${targetQuestion.quNo}');">취소</button> 
				</div>
			</td>
		</tr>
		</table>
	</form:form>
	</div>

<script>
const quNo = "${targetQuestion.quNo}";

var uploadImageUrl = '${pageContext.request.contextPath}/${companyId}/question/atch/uploadImage';

const checkbox = document.getElementById("questionSecretynCheckbox");
const hiddenInput = document.getElementById("questionSecretynHidden");

checkbox.addEventListener("change", () => {
    hiddenInput.value = checkbox.checked ? "Y" : "N";
});    
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/question/questionEdit.js"></script>