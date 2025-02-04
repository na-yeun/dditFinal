<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mail/mailList.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mail/mailSendForm.css" type="text/css" />
<link rel="stylesheet" href="https://unpkg.com/dropzone@5/dist/min/dropzone.min.css" type="text/css" />
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/mail/mailDraftList.js"></script>
<!-- 드랍존 -->
<script src="https://unpkg.com/dropzone@5/dist/min/dropzone.min.js"></script>
<!-- toast ui -->
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>


<h4 id="page-title">임시저장 메일함</h4>
<hr/>
<div class="search-area">
	<button type="button" class="search-btn btn btn-outline-primary" id="mail-refresh">메일 새로고침</button>
	
	<select name="type" id="type-select" class="form-control">
		<option value="" label="전체"/>
		<option value="to" label="수신자"/>
		<option value="title" label="제목"/>
		<option value="content" label="내용"/>
	</select>
	<input type="text" id="value-input" name="value" class="form-control"/>
	<button type="button" class="search-btn btn btn-primary" id="search-btn">검색</button>

</div>

<div class="search-area">
	<button type="button" class="search-btn btn btn-danger" id="delete-btn">삭제</button>
</div>

<table id="listtable" class="table mail-list-table">
	<thead>
		<tr>
			<th class="th-checkbox"><input id="total-checkbox" type="checkbox"></th>
			
			<c:if test="${not empty list}">
				<th id="my-mail-area" data-mymail="${list[0].empMail}" class="to th-tofrom">수신자</th>
			</c:if>
			<c:if test="${empty list}">
				<th id="my-mail-area" data-mymail="" class="to th-tofrom">수신자</th>
			</c:if>
			<th class="th-subject">제목</th>
			<th class="th-date">날짜</th>
		</tr>
	</thead>
	<tbody>
		<c:if test="${not empty list }">
			<c:forEach items="${list}" var="mail">
				<tr id="listtr" data-messageid="${mail.dmailDraftId}">
					<td><input class="one-checkBox" type="checkbox"></td>			
					<td class="to">${mail.dmailTo}</td>			
					<td class="clickTitle">${mail.dmailSubject}</td>			
					<td>${mail.dmailDate}</td>			
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${empty list}">
			<tr>
				<td style="text-align: center;" colspan="5"> 메일이 존재하지 않습니다. <td>
			</tr>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">
				<div class="paging-area">
					${pagingHtml}
				</div>
				
			</td>
		</tr>
	</tfoot>
</table>

<div class="modal fade" tabindex="-1" id="mail-detail" aria-hidden="true">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">메일 상세보기</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <table>
        	<tr style="display:none"  id="this-message-id">
        		<td class="modal-body-title">메세지id</td>
        		<td class="modal-body-main">
        			<input type="text" id="mail-id"/>
        		</td>
        	</tr>
        	
        	
        	<tr>
				<td class="mail-send-title">받는 사람</td>
				<td class="mail-send-main">
					<input type="text" id="to-area" class="form-control mail-form"/>
					<div id="to-list"></div>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">참조</td>
				<td class="mail-send-main">
					<input type="text" id="cc-area" class="form-control mail-form"/>
					<div id="cc-list"></div>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">숨은 참조</td>
				<td class="mail-send-main">
					<input type="text" id="bcc-area" class="form-control mail-form"/>
					<div id="bcc-list"></div>
				</td>
				<td class="mail-send-search-btn">
					<button type="button" class="btn btn-outline-primary">검색</button>
				</td>
			</tr>
			<tr>
				<td class="mail-send-title">제목</td>
				<td colspan="2">
					<input type="text" class="form-control plus-form" name="mailSubject" id="mailSubject"/>
				</td>
				
			</tr>
			<tr>
				<td class="mail-send-title">첨부파일</td>
				<td colspan="2">
					<input type="file" class="form-control plus-form" id="file-input" multiple/>
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
			<tr>
				<td colspan="3">
					<div id="editor">
					</div>
				</td>
			</tr>
        	
        	
        	
        	
        </table>
      </div>
      <div class="modal-footer">
        <button type="button" id="modal-submit-btn" class="btn btn-primary btn-sm">전송</button>
        <button type="button" id="modal-rewrite-btn" class="btn btn-warning btn-sm">수정</button>
        <button type="button" id="modal-del-btn" class="btn btn-danger btn-sm">삭제</button>
        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>





<form id="searchForm">
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="value" value="${value}"/>
	<input type="hidden" name="page" />
</form>

