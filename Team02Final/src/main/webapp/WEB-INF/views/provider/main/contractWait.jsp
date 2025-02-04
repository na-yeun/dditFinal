<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<link href="${pageContext.request.contextPath }/resources/css/contract/contractCompanyList.css" rel="stylesheet"/>
<link href="${pageContext.request.contextPath }/resources/css/contract/contractWait.css" rel="stylesheet"/>


<h4 id="page-title">승인 대기업체 목록</h4>

<div class="filter-container" style="display : flex;">  

   <!-- 검색창 -->
   <div class="search-container">
       <input type="text" id="searchInput" class="search-input form-control" placeholder="업체명을 입력하세요">
   </div>
</div>

<hr/>
<div class="table-responsive">
    <table class="table">
        <thead>
            <tr>

                <th data-column="rnum">
                    <div class ="th-content">
                        번호
                        <span class="arrow">▼</span>
                    </div>
                </th>
                <th data-column="contractCompany">
                    <div class ="th-content">
                        업체명
                        <span class="arrow">▼</span>
                    </div>                       
                </th>
                <th data-column="contractName">
                    <div class ="th-content">
                        업체 대표명
                        <span class="arrow">▼</span>
                    </div>                  
                </th>
                <th data-column="contractType">
                    <div class ="th-content">
                        업종명
                        <span class="arrow">▼</span>
                    </div>
                </th>
                <th data-column="contractTel">
                    <div class ="th-content">
                        업체 전화번호
                        <span class="arrow">▼</span>
                    </div>
                </th>
                <th data-column="contractEmail">
                    <div class ="th-content">
                        업체 이메일
                        <span class="arrow">▼</span>
                    </div>
                </th>
                <th data-column="payDate">
                    <div class ="th-content">
                        신청 날짜
                        <span class="arrow">▼</span>
                    </div>
                </th>
            </tr>
        </thead>
        <tbody id="waitTbody"></tbody>
    </table>
</div>
<div style="display: flex; justify-content: space-between; align-items: center;">
    <div id="pagination" class="pagination-container"></div>
</div>


<div id="waitComModal" class="modal fade bd-example-modal-lg"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4>신청 상세 정보</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="waitComModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end"> 
				    <button type="button" id="okBtn" class="btn btn-primary">
				        승인
				    </button>
				    <button type="button" id="rejectBtn" class="btn btn-danger">
				    	반려
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-secondary" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>

<div id="rejectModal" class="modal fade bd-example-modal-sm"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<h4>반려 사유</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="rejectModalBody" class="modal-body table-responsive">
				<textarea rows="11" cols="30" placeholder="반려사유는 150자 이내로 입력하세요." maxlength="150"></textarea>
			</div>

				<div class="modal-footer d-flex justify-content-end"> 
				    <button type="button" id="submitBtn" class="btn btn-primary">
				        제출
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-secondary" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>

<script>
    const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/app/contract/contractWait.js"></script>