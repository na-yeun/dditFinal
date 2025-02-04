<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<script
	src="${pageContext.request.contextPath }/resources/js/app/room/roomEdit.js"></script>
<style>
.th {
	font-weight: bold;
}
.swal2-container {
    z-index: 1070 !important; 
   } 
</style>

<form:form method="post" enctype="multipart/form-data" id="roomEditForm"
	modelAttribute="room">
	<h3 id="page-title">시설 관리</h3>
	<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>                      
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
	<input type="hidden" id="hidden-company-id" value="${companyId}">
	<table class="table table-bordered">

		<tr>
			<th>시설분류</th>
			<td ><form:select path="roomGory" class="form-control">
					<form:option value="" label="전체" />
					<form:option value="1" label="회의실" />
					<form:option value="2" label="미팅룸" />
					<form:option value="3" label="휴게실" />
					<form:option value="4" label="기타" />
				</form:select> <form:errors path="roomGory" cssClass="text-danger" /></td>
			<th>시설호수</th>
			<td colspan="3"><form:input path="roomHosu" class="form-control"
					  maxlength="6"/> 
					  <form:errors cssClass="text-danger"
					path="roomHosu" /></td>

		</tr>

		<tr>
			<th>시설명</th>
			<td><form:input path="roomName" class="form-control"  maxlength="33" /> 
					<form:errors cssClass="text-danger" path="roomName" /></td>
			<th>수용인원</th>
			<td ><form:input path="roomNum" type="number" class="form-control"/> 
					<form:errors cssClass="text-danger" path="roomNum" />
			</td>
			
		</tr>

		<tr>
			<th>시설 이미지</th>
			<td><c:if test="${not empty room.base64Img}">
					<div id="roomImageWrapper">
						<img src="data:image/*;base64,${room.base64Img}"
							class="room-image" alt="시설 이미지"
							style="max-width: 200px; height: auto; text-align: center;" /> <input
							type="hidden" name="existingRoomImage" value="${room.base64Img}" required="required"/>
						
					</div>
				</c:if></td>
			<th>시설 세부사항</th>
			<td><form:textarea path="roomDetail"
					cssClass="form-control"  maxlength="160"/> <form:errors cssClass="text-danger"
					path="roomDetail" /></td>
		</tr>
		<tr>
			<th>시설 이미지 변경</th>
			<td><input type="file" name="roomImage" class="form-control"
				accept="image/*" id="roomImg" /> <form:errors path="roomImage"
					cssClass="text-danger" /></td>
			
			<th>시설 사용가능 여부</th>
			<td><form:select path="roomYn" class="form-control">
					<form:option value="1" label="예약 가능" />
					<form:option value="2" label="점검 및 유지보수중" />
					<form:option value="3" label="설비 고장" />
					<form:option value="4" label="전력 및 통신 문제" />
					<form:option value="5" label="청소 및 준비 시간" />
					<form:option value="6" label="회사 행사" />
					<form:option value="7" label="시설 임대" />
				</form:select> <form:errors cssClass="text-danger" path="roomYn" /></td>		
					
		</tr>


	</table>
	<div id="btnClass"
		style="text-align: right; margin-top: 20px; margin-bottom: 20px;">
		<button type="submit" class="btn  me-2 btn-primary">전송</button>
		<button type="button" id="btn-delete"
			class="btn  me-2 btn-danger"
			data-room-id="${room.roomId }"
			data-url="${pageContext.request.contextPath}/${companyId}/room/${room.roomId}/delete">삭제</button>
		<button type="reset" class="btn  me-2 btn-secondary"
			onclick="window.history.back()">취소</button>
	</div>
</form:form>

