<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<link href="${pageContext.request.contextPath }/resources/css/contract/contractCompanyList.css" rel="stylesheet"/>
<h4 id="page-title">계약업체 목록</h4>  

<div class="filter-container" style="margin-right : 0;">
		
        <!-- 업종명 종류 -->
        <label for="type" style="margin-left:550px; text-align:center"><strong>업종</strong>
            <select id="selectContType" class="form-select">
				<option value="">전체</option>
				<option value="IT">IT</option>
				<option value="서비스업">서비스업</option>
				<option value="유통업">유통업</option>
				<option value="농/축산업">농/축산업</option>
				<option value="제조업">제조업</option>
				<option value="기타">기타</option>
            </select>
        </label>
		 <!-- 업체규모  -->
        <label for="scaleSize" style="text-align:center"><strong>업체규모</strong>
            <select id="selectScale" class="form-select">

            </select>
        </label>
       

    

        <div class="button-container d-flex gap-2" style="margin-top:20px; margin-left : 20px;">
            <button  id="downloadBtn" class="btn btn-success btn-sm-custom">
                <i class="fas fa-download"></i> 계약업체 목록 다운로드
            </button>
        </div>  

        <!-- 검색창 -->
        <div class="search-container" style="margin-top: 20px; display: flex; align-items: center; gap: 10px;">
            <input type="text" id="searchInput" class="search-input form-control" placeholder="업체명을 입력하세요">
            <!--       <span class="search-icon"> -->
            <!--         <i class='bx bx-search'></i> -->
            <!--       </span> -->
        </div>
    </div>


<div class ="table-responsive">
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
					<th data-column="contractTel">
                        <div class ="th-content">
                            업체 전화번호
                            <span class="arrow">▼</span>
                        </div>
                    </th>
					<th data-column="contractType">
                        <div class ="th-content">
                            업종명
                            <span class="arrow">▼</span>
                        </div>
                    </th>
					<th data-column="scale">
                        <div class ="th-content">
                            업체규모
                            <span class="arrow">▼</span>
                        </div>              
                    </th>
					<th data-column="contractStart">
                        <div class ="th-content">
                            계약시작일
                            <span class="arrow">▼</span>
                        </div>       
                    </th>
					<th data-column="contractEnd">
                        <div class ="th-content">
                            계약 종료일
                            <span class="arrow">▼</span>
                        </div>    
                    </th>
					<th data-column="remainDays">
                        <div class ="th-content">
                            남은일수
                            <span class="arrow">▼</span>
                        </div>                 
                    </th>
				</tr>
			</thead>
			
			<tbody id="innerTbody"></tbody>
				
	</table>
</div>
 <div style="display: flex; justify-content: space-between; align-items: center;">
        <div id="pagination" class="pagination-container"></div>
</div>

<div id="comDetailModal" class="modal fade bd-example-modal-lg"
	tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
	aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4>업체 정보 수정</h4>

				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div id="comDetailModalBody" class="modal-body table-responsive">
			</div>

					<div class="modal-footer d-flex justify-content-end">
				    <button type="button" id="modCompyInfoBtn" class="btn btn-primary">
				        수정
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
<script src="${pageContext.request.contextPath}/resources/js/app/contract/contractCompanyList.js"></script>
