<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
<link rel="stylesheet" href="https://unpkg.com/dropzone@5/dist/min/dropzone.min.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mail/mailSendForm.css" type="text/css" />

<!-- send form header js -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/mail/mailSendForm/mailSendFormHeader.js"></script>

<style>
	#search-btn{
		height: calc(1.5em + 0.75rem + 2px);
 	    white-space: nowrap;
 	    line-height: normal;
	}
</style>

<h4 id="page-title" data-contextpath="${pageContext.request.contextPath}">메일 보내기</h4>
<div style="text-align: right;">
	<button type="button" class="btn btn-outline-dark btn-sm" id="insert-pre-btn">데이터삽입</button>
</div>
<hr/>
<div id="write-option-area">
	<form:form id="mail-send-form" onsubmit="return false" enctype="multipart/form-data" modelAttribute="sendMail">
		<table class="table" id="page-title" data-url="${pageContext.request.contextPath}">
			<tr style="display:none;">
				<td class="mail-send-title">전달메일id</td>
				<td class="mail-send-main" colspan="2">
					<form:input type="text" path="messageId" id="message-id" cssClass="form-control"/>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">받는 사람</td>
				<td class="mail-send-main">
					<input type="text" name="mailTo" id="to-area" class="form-control"/>
					<ul id="autocomplete-list" class="list-group position-absolute w-100" style="z-index: 1000; display: none;">
					</ul>
					<div id="to-list" class="list-area">
						<c:if test="${not empty sendMail.mailTo}">
							<c:forEach items="${sendMail.mailTo}" var="toMail" varStatus="status">
								<script>
									document.addEventListener('DOMContentLoaded', () => {
										let toCnt = document.querySelectorAll('.to-mail-cnt').length;
										let thisType = "to"
										let thisList = document.querySelector('#to-list');
										
										plusEmail(toCnt, thisType, thisList, `${toMail}`);
									})
								</script>
							</c:forEach>
						</c:if>
					</div>
					<span class="text-danger" id="mailToError">${errors}</span>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary search-btns">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">참조</td>
				<td class="mail-send-main">
					<form:input type="text" path="mailCc" id="cc-area" cssClass="form-control"/>
					<div id="cc-list" class="list-area"></div>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary search-btns">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">숨은 참조</td>
				<td class="mail-send-main">
					<form:input type="text" path="mailBcc" id="bcc-area" cssClass="form-control"/>
					<div id="bcc-list" class="list-area"></div>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary search-btns">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">제목</td>
				<td colspan="2">
					<form:input type="text" path="mailSubject" cssClass="form-control" name="mailSubject" id="mailSubject" required="required"/>
					<span class="text-danger" id="mailSubjectError"></span>
				</td>
				
			</tr>
			
			<tr>
				<td class="mail-send-title">첨부파일</td>
				<td colspan="2">
					<input type="file" class="form-control" id="file-input" multiple/>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="dropzone" id="my-dropzone">
						<div class="dz-message needsclick">
					      <span class="text">
					       파일을 업로드하려면 첨부파일을 추가하거나 이곳에 파일을 드래그 해주세요.
					       <br>
					       첨부파일 크기는 25MB로 제한됩니다.
					      </span>
					    </div>
					</div>
				</td>
			</tr>
			
				
			<c:if test="${not empty sendMail.originalMailFiles}">
				<tr>
					<td class="mail-send-title">원본 첨부파일</td>
					<td colspan="2" id="original-attach">
						<p>전달 메일의 원본 첨부파일은 수정할 수 없습니다.</p>
						<c:forEach items="${sendMail.originalMailFiles}" var="mailFile" varStatus="status">
							<p id="${mailFile.mailattachmentId}">${mailFile.mailattachmentName}</p>
						</c:forEach>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="3">
					<div id="editor">
					</div>
				</td>
			</tr>
			<tr style="display:none;">
				<td colspan="3">
					<span class="text-danger" id="mailContentError"></span>
					<form:textarea path="mailContent" id="mailContent" class="form-control" required="required"></form:textarea>
				</td>
			</tr>
			<tr>
				<td colspan="3" class="button-area">
					<button type="submit" id="submit-btn" class="btn btn-primary">전송</button>
					<button type="button" id="cancel-btn" class="btn btn-secondary">취소</button>
				</td>
			</tr>
		</table>
	</form:form>
</div>

<!-- 주소록 모달 -->
<div class="modal fade" id="search-modal" tabindex="-1" data-bs-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">메일 수신자 검색</h4>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
			<!-- 모달 바디 -->
            <div class="modal-body">
                <div class="row g-0">
					<div class="mail-search-area mb-4">
						<div class="seach-title mb-3 d-flex gap-2 align-items-center">
						    <input type="text" id="search-input-area" class="form-control" placeholder="검색할 임직원의 이름을 입력해주세요.">
						    <button type="button" class="btn btn-primary" id="search-btn">검색</button>
						</div>
					</div>
				</div>
                <div class="col-12 pe-3">
                	<div id="mail-tree-directory"></div>
                </div>
            </div>
            <!-- 모달 푸터 -->
            <div class="modal-footer">
            	<div class="d-flex justify-content-end w-100 align-items-center gap-3">
	                <div class="d-flex gap-2">
	                    <button type="button" class="btn btn-primary" id="confirm-emp-btn">추가</button>
	                    <button type="button" class="btn btn-secondary" id="cancel-emp-btn" data-bs-dismiss="modal">취소</button>
	                </div>
            	</div>
        	</div>
		</div>
	</div>
</div>
	
<!-- 드랍존 -->
<script src="https://unpkg.com/dropzone@5/dist/min/dropzone.min.js"></script>
<!-- toast ui -->
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>



