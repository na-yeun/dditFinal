<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>신청서 양식</title>
<!-- <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/> -->
<link href="${pageContext.request.contextPath }/resources/lib/sweetalert2.min.css" rel="stylesheet"> 
<link href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/core.css" rel="stylesheet"/>
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/theme-default.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/fonts/boxicons.css" />

<link href="${pageContext.request.contextPath }/resources/css/contract/requestForm.css" rel="stylesheet"/>
</head>
<body>

<div class="container mt-5">
		<div style="text-align: right;">
    	   <h2 class="mb-4 text-center">신청서 양식</h2>
		   <button type="button" id="autoMappingBtn" class="btn btn-outline-dark btn-sm" onclick="automap()">데이터삽입</button>
		</div>
    <form id="requestForm" method="post" action="${pageContext.request.contextPath }/contract/all/previewReceipt">
        <!-- 계약업체명 -->
        <div class="mb-4">
            <label for="contractCompany" class="form-label">계약업체명</label>
			<div class="input-group w-75">
            <input type="text" name="contractCompany" id="contractCompany" class="form-control" value="${contract.contractCompany}" 
            required  maxlength="30"/>
            <span class="text-danger">${errors.contractCompany}</span>
            </div>
        </div>

        <!-- 계약업체 대표 이름 -->
        <div class="mb-4">
            <label for="contractName" class="form-label">계약업체 대표 이름</label>
            <input type="text" name="contractName" id="contractName" class="form-control" value="${contract.contractName}" 
             required maxlength="30"/>
            <span class="text-danger">${errors.contractName}</span>
        </div>
        
           
        
<!--------------------------------------------------------------------------------------------------------------------------->
        <div class="sms-body">
            <div class="mb-4">
                <label for="contractTel" class="form-label">계약업체 대표 전화번호</label>
                <div class="input-group w-75">
                    <input type="text" name="contractTel" id="contractTel" class="form-control me-2" 
                           placeholder="예) 01012345678" required 
                           pattern="^\d{10,11}$" title="전화번호는 10~11자리 숫자여야 합니다." />
                    <button type="button" id="send-sms-btn" class="btn btn-outline-primary">문자전송</button>
                </div>
                    <p id="guide-phone-auth"></p>
            </div>
        
	        <div class="mb-4">
	        	<div class="input-group w-75">
		            <input type="text" name="authCode" id="auth-input" 
		                   placeholder="인증번호 작성해주세요." class="form-control me-2" disabled="disabled" />
		            <button type="button" id="check-auth-btn" class="btn btn-outline-primary">인증번호 확인</button>
	            </div>
		            <p id="phone-auth-result"></p>
	        </div>
        
        </div>
        
<!--------------------------------------------------------------------------------------------------------------------------->
    
        
        <!-- 계약업체 이메일 -->
        <div class="mb-4">
            <label for="contractEmail" class="form-label">계약업체 대표 이메일</label>
            <input type="email" name="contractEmail" id="contractEmail" class="form-control" value="${contract.contractEmail}" 
             placeholder="예)  test123@gmail.com" required pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"/>
            <span class="text-danger">${errors.contractEmail}</span>
        </div>
		
		<div class="mb-4">
			<label for="contractAddr1" class="form-label">주소</label>
			<div class="input-group" style="display: flex; align-items: center; gap: 10px;">
			<input type="text" name="contractAddr1" id="roadAddress" class="form-control me-2" value="${contract.contractAddr1 }"
				onclick="getAddrForm()" readonly="readonly" title="클릭하면 주소 검색창이 열립니다." placeholder="클릭하면 주소 검색창이 열립니다." required
			/>
			<a onclick="getAddrForm()" class="btn btn-outline-primary" id="search-btn">검색</a>
			</div>
			<span id="guide" style="color: #999; display: none"></span>
			<span class="text-danger">${errors.contractAddr1}</span>
		</div>
		<div class="mb-4">
			<label for="contractAddr2" class="form-label">상세주소</label>
			<input type ="text" name="contractAddr2" id ="extraAddress" class="form-control" maxlength="30"/>
			<span class="text-danger">${errors.contractAddr2}</span>			
		</div>
		
        <!-- 계약시작일 -->
<!--         <div class="mb-4"> -->
<!--             <label for="contractStart" class="form-label">계약기간</label> -->
<!-- 		    <div class="d-flex align-items-center"> -->
<!-- 		        <input type="date" name="contractStart" id="contractStart"  -->
<%-- 		               class="form-control me-2" value="${contract.contractStart}" required /> --%>
<!-- 		        ~ -->
<!-- 		        <input type="date" name="contractEnd" id="contractEnd"  -->
<%-- 		               class="form-control ms-2" value="${contract.contractEnd}" required readonly /> --%>
<!-- 		    </div> -->
<%-- 			    <span class="text-danger">${errors.contractStart}</span> --%>
<%-- 			    <span class="text-danger">${errors.contractEnd}</span> --%>
<!--         </div> -->
        
		<div class="mb-4">
            <label for="contractStart" class="form-label">계약기간</label>
		    <div class="d-flex align-items-center">
		        <input type="hidden" name="contractStart" id="contractStart" 
		               class="form-control me-2" value="${contract.contractStart}" />
		        <input type="hidden" name="contractEnd" id="contractEnd">
		    <label class="me-2">
	        	<input type="radio" name="contractEnd" value="3개월" class="form-check-input" required checked> 3개월
		    </label>
		    
		    <label class="me-2">
		        <input type="radio" name="contractEnd" value="6개월" class="form-check-input" required> 6개월
		    </label>
		    
		    <label class="me-2">
		        <input type="radio" name="contractEnd" value="1년" class="form-check-input" required> 1년
		    </label>
		    
		    <label class="me-2">
		        <input type="radio" name="contractEnd" value="3년" class="form-check-input" required> 3년
		    </label>
		    
		    <label class="me-2">
		        <input type="radio" name="contractEnd" value="5년" class="form-check-input" required> 5년
		    </label>
			    </div>
				    <span class="text-danger">${errors.contractStart}</span>
				    <span class="text-danger">${errors.contractEnd}</span>
	        </div>
<!--         계약종료일 -->
<!--         <div class="mb-4"> -->
<!--             <label for="contractEnd" class="form-label">계약종료일</label> -->
            
<!--         </div> -->

        <!-- 업종명 -->
        <div class="mb-4">
            <label for="contractType" class="form-label">업종명</label>
			<select name ="contractType" id="contractType" class="form-select" required>
				<option value="" disabled selected>업종 선택</option>
				<option value="IT">IT</option>
				<option value="서비스업">서비스업</option>
				<option value="유통업">유통업</option>
				<option value="농/축산업">농/축산업</option>
				<option value="제조업">제조업</option>
				<option value="기타">기타</option>
			</select>            
        </div>
        <!-- 사용인원 -->
        <div class="mb-4">
            <label for="empCountId" class="form-label">사용인원</label>
            <select name="empCountId" id="empCountId" class="form-select" required>
            		<option value="" disabled selected>사용인원을 선택하세요</option>
                <c:forEach items="${empCountList}" var="emp">
                    <option value="${emp.empCountId},${emp.empCountPrice},${emp.empCount}">${emp.empCount}, +${emp.empCountPrice}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 스토리지 용량 -->
        <div class="mb-4">
            <label for="storageId" class="form-label">스토리지 용량</label>
            <select name="storageId" id="storageId" class="form-select" required>
            		<option value="" disabled selected>스토리지 용량을 선택하세요</option>
                <c:forEach items="${storageList}" var="strg">
                    <option value="${strg.storageId},${strg.storagePrice},${strg.storageSize}">${strg.storageSize}, +${strg.storagePrice}</option>
                </c:forEach>
            </select>
        </div>
        <!-- 업체규모 -->
        <div class="mb-4">
            <label for="scaleId" class="form-label">업체규모</label>
            <select name="scaleId" id="scaleId" class="form-select" required>
            		<option value="" disabled selected>업체규모를 선택하세요</option>
                <c:forEach items="${scaleList}" var="scale">
                    <option value="${scale.scaleId},${scale.scalePrice},${scale.scaleSize}">${scale.scaleSize}, +${scale.scalePrice}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 버튼 그룹 -->
        <div class="text-end">
            <button type="submit" id="submitBtn" class="btn btn-primary">신청하기</button>
            <button type="button" class="btn btn-danger">취소</button>
        </div>
    </form>
</div>
<script>
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/lib/sweetalert2.min.js"></script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="${pageContext.request.contextPath }/resources/js/app/contract/requestForm.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
