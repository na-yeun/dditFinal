<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<link href="${pageContext.request.contextPath }/resources/css/contract/contractCompanyList.css" rel="stylesheet"/>
<link href="${pageContext.request.contextPath }/resources/css/contract/firstSettingList.css" rel="stylesheet"/>
<style>





</style>
<h4 id="page-title">세부 세팅대기업체 목록</h4>

<div class="filter-container">
<!-- 검색창 -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input form-control" placeholder="업체명을 입력하세요">
            <!--       <span class="search-icon"> -->
            <!--         <i class='bx bx-search'></i> -->
            <!--       </span> -->
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
                <th>
                	<div class="statusBadge">
                		신청상태
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
                <th data-column="firstRequestDate">
                    <div class ="th-content">
                        세팅 신청 날짜
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


<div id="fsDetailModal" class="modal fade bd-example-modal-lg"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4>초기세팅 상세정보</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="fsDetailModalBody" class="modal-body table-responsive">
			</div>

				<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="okBtn" class="btn btn-primary">
				        반영
				    </button>
				    <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">
				        취소
				    </button>
			</div>		
		</div>
	</div>
</div>


<script>
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/resources/js/app/contract/firstSettingList.js"></script>

