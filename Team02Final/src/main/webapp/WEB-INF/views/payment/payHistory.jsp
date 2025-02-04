<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
   
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/payment/payHistory.css">

<h4 id="page-title">결제 이력</h4>
<hr/>
<div class="table-responsive">

	<table class="table">
		<thead>
			<tr>
				<th class="text-center">번호</th>
				<th class="text-center">결제금액</th>
				<th class="text-center">결제수단</th>
				<th class="text-center">계약기간</th>
				<th class="text-center">결제일자</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty list }">
			<c:forEach items="${list }" var="list">
			<tr id="rowOne" data-contract-id="${list.contract.contractId }">
				<td class="text-center">${list.rnum }</td>
				<td class="text-center"><fmt:formatNumber value="${list.payAmount }" type="number" groupingUsed="true"/></td>
				<td class="text-center">${list.payMethod }</td>
				<td class="text-center">
				${list.contract.contractStart.substring(0,4) }-${list.contract.contractStart.substring(4,6) }-${list.contract.contractStart.substring(6,8) } 
				~ 
				${list.contract.contractEnd.substring(0,4) }-${list.contract.contractEnd.substring(4,6) }-${list.contract.contractEnd.substring(6,8) }
				</td>
				<td class="text-center">${list.payDate }</td>
			</tr>
			</c:forEach>
			</c:if>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<div class="paging-area">
						${pagingHtml}
					</div>
				</td>
			</tr>
		</tfoot>	
	</table>
</div>

		    		
<div id="payDetailModal" class="modal fade bd-example-modal-lg"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4>결제 상세조회</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="payDetailModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end">

				    <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">
				        닫기
				    </button>
			</div>		
		</div>
	</div>
</div>


<script>
	const companyId = "${companyId}";
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/resources/js/app/payment/payHistory.js"></script>	



