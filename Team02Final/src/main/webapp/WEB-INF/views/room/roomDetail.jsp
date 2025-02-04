<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<script
	src="${pageContext.request.contextPath }/resources/js/app/room/roomTime.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/resources/css/room/room.css">
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.companyId }" var="companyId"></c:set>   
</security:authorize>
<c:if test="${not empty errorMessage}">
    <script>
        Swal.fire({
            title: '알림',
            text: '${message}',
            icon: 'warning',
            confirmButtonText: '확인'
        });
    </script>
</c:if>
	 <!-- 특정 roomId에 해당하는 시설만 출력 -->
	<c:if test="${room.roomId == roomId}">
		<h3 id="page-title">${room.roomName}</h3>

		<%-- <input type="text" id="empName" value="${empName}"> --%>

		<div class="main-layout">
			<!-- 시설 정보 섹션 -->
			<div class="facility-section">
				<!-- 이미지 -->
				<div class="facility-image-wrapper">
					<img src="data:image/*;base64,${room.base64Img}" alt="시설 이미지" />
				</div>

				<!-- 시설 정보와 선택 정보 -->
				<div class="info-wrapper">
					<!-- 시설 정보 테이블 -->
					<div class="facility-info">
						<h5 class="facility-title">
							<span class="icon">✔</span> 시설정보
						</h5>
						<table class="styled-table">
							<tr>
								<th>호수</th>
								<td id="roomHosu">${room.roomHosu}호</td>
								<th>시설명</th>
								<td id="roomName">${room.roomName}</td>
								<th>수용인원</th>
								<td id="roomNum">${room.roomNum}명</td>
							</tr>
							<tr>
								<th>시설상세정보</th>
								<td id="roomDetail" colspan="3">${room.roomDetail}</td>
								<th>사용 가능<br>여부
								</th>
								<td><c:choose>
										<c:when test="${room.roomYn == '1'}">예약 가능</c:when>
										<c:otherwise>사용 불가능</c:otherwise>
									</c:choose></td>
							</tr>
						</table>
					</div>

					<!-- 선택 정보 -->
					<div class="room-reservation">
						<h5 class="selection-title">
							<span class="icon">✔</span> 선택정보
						</h5>
						<table class="styled-table">
							<tr>
								<th>예약날짜</th>
								<td id="selected-date"></td>
							</tr>
							<tr>
								<th>예약시간</th>
								<td><select id="time-select" class="form-select" multiple>
										<option value="" disabled selected>시간을 선택하세요</option>
										<c:forEach var="roomTime" items="${roomTime}">
											<c:if
												test="${roomTime.roomtimeYn == 'Y' && roomTime.room.roomYn == '1'}">
												<!-- 예약 가능 -->
												<option value="${roomTime.timeReser.timeCode}"
													data-yn="${roomTime.roomtimeYn}">
													${roomTime.timeReser.timeRange} (예약 가능)</option>
											</c:if>
											<c:if
												test="${roomTime.roomtimeYn == 'N' || roomTime.room.roomYn != '1'}">
												<!-- 예약 불가능 -->
												<option data-yn="${roomTime.roomtimeYn}"
													value="${roomTime.timeReser.timeCode}" disabled>
													${roomTime.timeReser.timeRange} (예약 불가능)</option>
											</c:if>

										</c:forEach>
								</select></td>

							</tr>
							<tr>
								<th>선택 회차</th>
								<td id="selected-times"></td>
							</tr>
						</table>
						<button id="modal-button" class="btn btn-primary mt-3 right-align"
							data-bs-toggle="modal" data-bs-target="#basicModal">
							예약하기</button>
					</div>
				</div>
			</div>
		</div>

	</c:if>

<div id="reservationTable">
    <security:authorize access="isAuthenticated()">
        <security:authentication property="principal" var="principal" />
        <c:set value="${principal.account.empId}" var="empId"></c:set>
    </security:authorize>
    <h5 class="selection-title">
        <span class="icon">✔</span> 예약현황
    </h5>
    <table id="reTab" class="table">
        <thead>
            <tr>
                <th>예약시간</th>
                <th>예약자 부서</th>
                <th>예약자명</th>
                <th>예약취소</th>
            </tr>
        </thead>
        <tbody>
            <!-- 예약 리스트가 비어있는 경우 -->
            <c:if test="${empty roomReserList}">
                <tr>
                    <td colspan="4" style="text-align: center;">
                        <div class="alert alert-info" role="alert">예약현황이 없습니다.</div>
                    </td>
                </tr>
            </c:if>
            <!-- 예약 리스트가 비어있지 않은 경우 -->
            <c:if test="${not empty roomReserList}">
                <c:forEach var="roomReser" items="${roomReserList}">
                    <tr>
                        <td>${roomReser.timeReser.timeRange}</td>
                        <td>${roomReser.department.departName}</td>
                        <td>${roomReser.employee.empName}</td>
                        <td>
                            <!-- 삭제 버튼 -->
                            <c:if test="${roomReser.employee.empId eq empId || posiId == 7}">
                                <button class="btn btn-danger delBtn"
    data-url="${pageContext.request.contextPath}/${companyId}/roomTime/${roomReser.reserId}/delete">예약취소</button>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
    </table>
</div>
<div style="text-align:right;" >
<button style="width:8%; margin-bottom:20px;" 
data-url="${pageContext.request.contextPath}/${companyId}/room"
class="btn btn-secondary" id="backLocationBtn">목록</button>
</div>


<!-- 예약을 확인하고 최종으로 예약하는 모달 -->
<div class="modal fade" id="basicModal" tabindex="-1" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<input type="hidden" id="hiddenCompanyId" value="${companyId}">
				<input type="hidden" id="hiddenRoodId" value="${roomId}">
				<c:forEach var="roomTime" items="${roomTime}">
					<input type="hidden" id="hiddenTimecode"
						value="${roomTime.timeCode}">
					<security:authorize access="isAuthenticated()">
						<security:authentication property="principal" var="principal" />
						<c:set value="${principal.account.empName}" var="empName"></c:set>
					</security:authorize>
					<input type="hidden" id="empName" value="${empName}" >
				</c:forEach>
				<!-- <h5 class="modal-title">예약 확인</h5> -->
				<h3 id="page-title">예약 확인</h3>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">

				<table class="table table-bordered">
					<tbody>
						<tr>
							<td>시설</td>
							<td><img id="modal-facility-image" src="" alt="시설 이미지"
								style="width: 100%; height: 100%; object-fit: cover;" /></td>
						</tr>
						<tr>
							<td>호수</td>
							<td id="modal-room-hosu"></td>
						</tr>
						<tr>
							<td>이름</td>
							<td id="modal-room-name"></td>
						</tr>
						<tr>
							<td>번호</td>
							<td id="modal-room-num"></td>
						</tr>
						<tr>
							<td>상세정보</td>
							<td id="modal-room-detail"></td>
						</tr>
						<tr>
							<td>사용용도</td>
							<td><textarea id="modal-roomReser-detail"
									class="form-control" required="required" maxlength="60"></textarea></td>
						</tr>
					</tbody>
				</table>
				<p>
					<strong>선택 날짜:</strong> <span id="modal-selected-date"></span>
				</p>
				<p>
					<strong>선택 회차:</strong> <span id="modal-selected-times"></span>
				</p>
			</div>
			<div class="modal-footer">
				
				<button type="button" id="confirm-reservation"
					class="btn btn-primary"
					data-url="${pageContext.request.contextPath}/${companyId }/roomTime/${roomId}/reser">
					예약하기</button>
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">취소</button>	
			</div>
		</div>
	</div>
</div>
