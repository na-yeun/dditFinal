<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<script type="text/javascript">
 const companyId = 'a001';
</script>
<style>
  #contentSection th{
  	width : 250px;
      vertical-align: middle; /* 내용 정렬 */
  }

  #contentSection td {
      word-break: break-word; /* 내용이 길 경우 줄바꿈 */
  }
</style>

<h4 id="page-title">문의 게시글</h4>
<hr/>
	<div class="table-responsive">
		<table class="table">
			<tr>
				<th>문의 제목</th>
				<td>${question.quTitle}</td>
			</tr>
			<tr>
				<th>작성날짜</th>
				<td>${question.quDate}</td>
			</tr>
			<tr>
			    <th>문의 카테고리 명</th>
			    <td>
			        <c:forEach var="category" items="${categoryList}">
			            <c:if test="${category.goryId eq question.goryId}">
			                ${category.goryNm}
			            </c:if>
			        </c:forEach>
			    </td>
			</tr>
			<tr>
				<th>작성자</th>
				<td>${question.empName}</td>
			</tr>
			<tr>
				<th>첨부파일</th>
				<td>
					<c:choose>
						<c:when test="${not empty question.atchFile }">
							<c:forEach items="${question.atchFile.fileDetails }" var="fd" varStatus="vs">
								<c:url value='/${companyId}/question/${question.quNo}/atch/${fd.atchFileId }/${fd.fileSn }' var="downUrl"/>
								<a href="${downUrl }">${fd.orignlFileNm }(${fd.fileFancysize })</a>
								${not vs.last ? '|' : ''}
							</c:forEach>
						</c:when>
						<c:otherwise>
							파일이 등록되지 않았습니다.
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>문의게시판 내용</th>
				<td>${question.quContent}</td>
			</tr>
		</table>
		<div style="text-align: right;">
			<c:choose>
				<c:when test="${question.quYn eq 'Y'}">
					<button type="button" id="showCmtForm" style="display: none;" class="btn btn-primary">답변 작성</button>
					<button type="button" id="listBtn" class="btn btn-primary">목록</button>				
				</c:when>
				<c:otherwise>
					<button type="button" id="showCmtForm" class="btn btn-primary">답변 작성</button>
					<button type="button" id="listBtn" class="btn btn-primary">목록</button>				
				</c:otherwise>
			</c:choose>
		</div>
	</div>

<br><br>

<div class="card container" id="commentArea">
	<div class="table-responsive">
		<table class="table" id="contentSection">
			<c:choose>
				<c:when test="${question.quYn eq 'Y' }">
					<tr>
						<th>운영자(답변작성자)</th>
						<td>운영자</td>
					</tr>
					<tr>
						<th>답변날짜</th>
						<td>${question.answDate}</td>
					</tr>
					<tr>
						<th>답변 내용</th>
						<td>${question.answContent}</td>
					</tr>
					<tr>
						<td colspan="2">
							<div style="text-align: right;">
								<button type="button" id="editAnsBtn" class="btn btn-primary">답변 수정</button>
							</div>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
						<tr>
							<th>운영자 답변</th>
							<td>아직 답변이 등록되지 않았습니다.</td>
						</tr>
					<tr style="display: none;">
						<td>
							<button type="button" id="editAnsBtn" class="btn btn-primary">답변 수정</button>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</table>
		<br>
	</div>
</div>
<br><br>
<div class="card container" id="commentForm" style="display: none;">
	<div id="table-responsive">
		<form id="answForm" method="post" enctype="application/x-www-form-urlencoded" >
			<table class="table table-borderless">
				<tr>
					<td colspan="2">
						<div>
						  <label class="form-label">답변 내용</label>
						  <textarea id="ansTextarea" name="answContent" class="form-control" rows="3" placeholder="답변을 입력하세요." required></textarea>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div style="text-align: right;">
						    <button type="button" class="btn btn-outline-dark btn-sm" id="dataInputBtn">데이터삽입</button>
							<button type="submit" class="btn btn-primary">답변 등록</button>
						</div>
					</td>
				</tr>
			</table>
		</form>	
	</div>
</div>
<div class="card container" id="commentEditForm" style="display: none;">
	<div id="table-responsive">
		<form id="editForm" method="post" enctype="application/x-www-form-urlencoded" >
			<table class="table table-borderless">
				<tr>
					<td colspan="2">
						<div>
						  <label class="form-label">답변 내용</label>
						  <textarea id="editTextarea" name="answContent" class="form-control" rows="3" placeholder="답변을 입력하세요." required></textarea>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div style="text-align: right;">
							<button type="submit" class="btn btn-primary">수정</button>
						</div>
					</td>
				</tr>
			</table>
		</form>	
	</div>
</div>
<br/>
<br/>

<script>
    const quNo = "${question.quNo}";
    const contextPath = "${pageContext.request.contextPath}";
    
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/provider/provQuestionDetail.js"></script>