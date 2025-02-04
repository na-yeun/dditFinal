<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>영수증 미리보기</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<style>
    .receipt-container {
    	width : 600px;
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        border: 1px solid #ddd;
        border-radius: 8px;
        background-color: #f9f9f9;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }
    .receipt-title {
        text-align: center;
        margin-bottom: 20px;
    }
</style>
</head>
<body>
<div class="container mt-5">
    <div class="receipt-container">
        <h2 class="receipt-title">신청 내역 확인</h2>
        <form id="reqForm" method="POST" action="${pageContext.request.contextPath}/contract/all/addContReqForm">

        <input type ="hidden" name="contractCompany" value="${contractCompany}"/>        
		<input type ="hidden" name="contractName" value="${contractName}"/>        
		<input type ="hidden" name="contractTel" value="${contractTel}"/>
		<input type ="hidden" name="contractEmail" value="${contractEmail}"/>
		        
		<input type ="hidden" name="contractStart" value="${contractStart}"/>        
		<input type ="hidden" name="contractEnd" value="${contractEnd}"/>        
		<input type ="hidden" name="contractType" value="${contractType}"/>        
		<input type ="hidden" name="empCountId" value="${empCountId}"/>        
		<input type ="hidden" name="storageId" value="${storageId}"/>        
		<input type ="hidden" name="scaleId" value="${scaleId}"/>        
        <input type ="hidden" name="totalPrice" value="${totalPrice}" /> 
		<input type ="hidden" name="contractAddr1" value="${contractAddr1 }"/>
		<input type ="hidden" name="contractAddr2" value="${contractAddr2 }"/>
        <table class="table table-bordered">
		
            <tbody>
                <tr>
                    <th>계약업체명</th>
                    <td>${contractCompany}</td>
                </tr>
                <tr>
                    <th>대표 이름</th>
                    <td>${contractName}</td>
                </tr>
                <tr>
                    <th>대표 전화번호</th>
                    <td>${contractTel}</td>
                </tr>
                <tr>
                    <th>대표 이메일</th>
                    <td>${contractEmail}</td>
                </tr>
                <tr>
                	<th>업체 주소 </th>
                	<td>${contractAddr1 } ${contractAddr2 }</td>
                </tr>
                <tr>
                    <th>계약 기간</th>
                    <td>${contractStart} ~ ${contractEnd}</td>
                </tr>
                <tr>
                    <th>업종명</th>
                    <td>${contractType}</td>
                </tr>
                <tr>
                    <th>사용 인원</th>
                    <td>${empCount} (₩${empCountPrice})</td>
                </tr>
                <tr>
                    <th>스토리지 용량</th>
                    <td>${storageSize} (₩${storagePrice})</td>
                </tr>
                <tr>
                    <th>업체 규모</th>
                    <td>${scaleSize} (₩${scalePrice})</td>
                </tr>
                <tr>
                    <th>총 가격</th>
                    <td><strong>₩${totalPrice}</strong></td>
                </tr>
            </tbody>
        </table>
        <div class="text-center mt-4">
            <button type="submit" class="btn btn-success">최종 확인 및 제출</button>
            <button type="button" class="btn btn-secondary" onclick="history.back()">수정하기</button>
        </div>
    </form>
    </div>
</div>

	
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	

</body>
</html>
