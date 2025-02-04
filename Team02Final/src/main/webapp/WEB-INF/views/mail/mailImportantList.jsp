<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mail/mailList.css"/>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>

<h4 id="page-title">중요 메일함</h4>
<hr/>
<div class="search-area">
	<button type="button" class="search-btn btn btn-outline-primary" id="mail-refresh">메일 새로고침</button>
	
	<select name="type" id="type-select" class="form-control">
		<option value="" label="전체"/>
		<option value="to" label="수신자"/>
		<option value="from" label="발신자"/>
		<option value="title" label="제목"/>
		<option value="content" label="내용"/>
	</select>
	<input type="text" id="value-input" name="value" class="form-control"/>
	<button type="button" class="search-btn btn btn-primary" id="search-btn">검색</button>

</div>

<div class="search-area">
	<button type="button" class="search-btn btn btn-warning" id="important-btn">일반메일복원</button>
	<button type="button" class="search-btn btn btn-danger" id="delete-btn">삭제</button>
</div>

<table id="listtable" class="table mail-list-table">
	<thead>
		<tr>
			<th class="th-checkbox"><input id="total-checkbox" type="checkbox"></th>
			<c:if test="${not empty list }">
				<th id="my-mail-area" data-mymail="${list[0].empMail}" class="from th-tofrom">수신자</th>
				<th class="to th-tofrom">발신자</th>
			</c:if>
			<c:if test="${empty list }">
				<th id="my-mail-area" data-mymail="" class="to th-tofrom">수신자</th>
				<th class="from th-tofrom">발신자</th>
			</c:if>
			<th class="th-subject">제목</th>
			<th class="th-date">날짜</th>
		</tr>
	</thead>
	<tbody>
		<c:if test="${not empty list }">
			<c:forEach items="${list}" var="mail">
				<tr id="listtr" data-messageid="${mail.mailMessageId}">
					<td>
						<input class="one-checkBox" type="checkbox">
					</td>	
							
					<td class="to">
						<c:forEach items="${mail.imailToList}" var="toOne">
							<span class="mail-span badge bg-label-info">${toOne}</span>
						</c:forEach>
					</td>	
							
					<td class="from">
						<c:forEach items="${mail.imailFromList}" var="fromOne">
							<span class="mail-span badge bg-label-info">${fromOne}</span>
						</c:forEach>
					</td>
								
					<td class="clickTitle">${mail.imailSubject}</td>			
					<td>${mail.imailDate}</td>			
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
      <form id="forwardForm" method="post">
	      <div class="modal-body">
	        <table>
		        <tr style="display:none;" id="this-message-id">
					<td class="modal-body-title">메세지id</td>
					<td class="modal-body-main" >
						<input type="text" name="messageId" id="mail-id"/>
					</td>
				</tr>
	        	<tr>
	        		<td class="modal-body-title">보낸 사람</td>
	        		<td class="modal-body-main" id="mail-from"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">받은 사람</td>
	        		<td class="modal-body-main" id="mail-to"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">참조</td>
	        		<td class="modal-body-main" id="mail-cc"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">숨은참조</td>
	        		<td class="modal-body-main" id="mail-bcc"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">보낸 날짜</td>
	        		<td class="modal-body-main" id="mail-date"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">제목</td>
	        		<td class="modal-body-main" id="mail-title"></td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">첨부파일</td>
	        		<td class="modal-body-main" id="mail-attch">
	        			<div id="attach-area">
	        				
	        			</div>
	        		</td>
	        	</tr>
	        	<tr>
	        		<td class="modal-body-title">내용</td>
	        		<td class="modal-body-main" id="mail-content"></td>
	        	</tr>
	        </table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" id="modal-re-btn" class="btn btn-primary btn-sm">답장</button>
	        <button type="button" id="modal-for-btn" class="btn btn-success btn-sm">전달</button>
	        <button type="button" id="modal-important-btn" class="btn btn-warning btn-sm">일반메일복원</button>
	        <button type="button" id="modal-del-btn" class="btn btn-danger btn-sm">삭제</button>
	        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">닫기</button>
	      </div>
      </form>
    </div>
  </div>
</div>





<form id="searchForm">
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="value" value="${value}"/>
	<input type="hidden" name="page"/>
</form>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/mail/mailImportantList.js"></script>