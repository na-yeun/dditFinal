<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/provider/provContCompanyPayHistoryPage.css">
<h4 id="page-title">계약업체 결제정보</h4>


<div class ="table-responsive">
	<table class="table">
		
			<thead>
				<tr>

					<th data-column="rnum">
                        <div class ="th-content">
                            번호
                        </div>
                    </th>
					<th data-column="contractCompany">
                        <div class ="th-content">
                            업체명
                        </div>                       
                    </th>
					<th data-column="contractName">
                        <div class ="th-content">
                            업체 대표명
                        </div>                  
                    </th>
					<th data-column="payAmount">
                        <div class ="th-content">
                            결제금액
                        </div>                  
                    </th>
					<th data-column="payMethod">
                        <div class ="th-content">
                            결제수단
                        </div>                  
                    </th>
					<th data-column="payDate">
                        <div class ="th-content">
                            결제일
                        </div>                  
                    </th>
					
				</tr>
			</thead>
			
			<tbody id="innerTbody"></tbody>
				
	</table>
</div>

<script>
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/provider/provContCompanyPayHistoryPage.js"></script>



