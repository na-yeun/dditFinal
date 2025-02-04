<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<script>
document.addEventListener("DOMContentLoaded", () => {
    const roomNumInput = document.querySelector('input[name="roomNum"]');

    // 입력 필드에서 음수 입력 차단
    roomNumInput.addEventListener("input", (event) => {
        if (roomNumInput.value < 0) {
            roomNumInput.value = 0; // 음수가 입력되면 0으로 변경
        }
    });
     const formRoom = document.querySelector("form");
    const roomGorySelect = document.getElementById("roomGory");

    formRoom.addEventListener("submit", (event) => {
        if (!roomGorySelect.value) {
            event.preventDefault(); // 폼 제출 막기
            alert("시설 종류를 선택해주세요."); // 경고창 표시
            roomGorySelect.focus(); // 선택박스로 포커스 이동
        }
    }); 
    
  
    
   const dataMppingBtn = document.getElementById("dataMppingBtn");
   dataMppingBtn.addEventListener("click", (event)=>{
	   
	        event.preventDefault(); // 기본 동작 방지 (폼 제출 방지)

	        // 각 입력 필드에 값 할당
	        document.querySelector('select[name="roomGory"]').value = "1"; // 시설 분류: 회의실
	        document.querySelector('input[name="roomHosu"]').value = "101"; // 시설 호수
	        document.querySelector('input[name="roomName"]').value = "101호 회의실"; // 시설명
	        document.querySelector('input[name="roomNum"]').value = "20"; // 수용 인원
	        document.querySelector('input[name="roomDetail"]').value = "무선 인터넷 연결 가능, TV 사용 가능,스크린(3000×2400), 빔프로젝트 사용가능, 유선마이크(1), 무선마이크(1), 화상 카메라 사용 가능"; // 시설 설명
   })
   
});

</script>
<c:if test="${not empty message}">
    <script>
        Swal.fire({
            title: '알림',
            text: '${message}',
            icon: 'warning',
            confirmButtonText: '확인'
        });
    </script>
</c:if>
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>                      
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
<h4 id="page-title" >시설 등록</h4>
<div class="container mt-5" style="max-width: 600px;"> <!-- 컨테이너로 중앙 정렬 -->
    <form:form method="post" action="${pageContext.request.contextPath}/${companyId}/room/new"
     enctype="multipart/form-data" modelAttribute="room" class="needs-validation">
        <button id="dataMppingBtn" class="btn btn-outline-secondary ms-auto" style="font-size: 12px; padding: 5px 10px; float: right;">데이터 삽입</button>
        <!-- 시설 분류 -->
        <div class="form-group mb-3">
            <label for="roomGory">시설 분류</label>
            <form:select path="roomGory" class="form-control" required="required">
                <form:option value="" label="시설 종류를 선택해주세요" />
                <form:option value="1" label="회의실" />
                <form:option value="2" label="미팅룸" />
                <form:option value="3" label="휴게실" />
                <form:option value="4" label="기타" />
            </form:select>
            <form:errors path="roomGory" cssClass="text-danger" />
        </div>

        <!-- 시설 호수 -->
        <div class="form-group mb-3">
            <label for="roomHosu">시설 호수</label>
            <form:input path="roomHosu" class="form-control" required="required" maxlength="6"/>
            <form:errors path="roomHosu" cssClass="text-danger" />
        </div>

        <!-- 시설 이름 -->
        <div class="form-group mb-3">
            <label for="roomName">시설명</label>
            <form:input path="roomName" class="form-control" required="required" maxlength="33"/>
            <form:errors path="roomName" cssClass="text-danger" />
        </div>

        <!-- 수용 인원 -->
        <div class="form-group mb-3">
            <label for="roomNum">수용 인원</label>
            <form:input type="number" path="roomNum" class="form-control" required="required" min="0" max="300"/>
            <form:errors path="roomNum" cssClass="text-danger" />
        </div>

        <!-- 시설 사진 -->
        <div class="form-group mb-3">
            <label for="roomImage">시설 사진</label>
            <input type="file" name="roomImage" class="form-control" accept="image/*" required="required"/>
            <form:errors path="roomImage" cssClass="text-danger" />
        </div>

        <!-- 시설 설명 -->
        <div class="form-group mb-4">
            <label for="roomDetail">시설 설명</label>
            <form:input path="roomDetail" class="form-control" required="required" maxlength="160"/>
            <form:errors path="roomDetail" cssClass="text-danger" />
        </div>

        <!-- 버튼 -->
         <div class="d-flex justify-content-end gap-2" style="margin-bottom:20px;">
            <button type="submit" class="btn btn-primary">전송</button>
            <button type="reset" class="btn btn-secondary">취소</button>
        </div>
    </form:form>
</div>
