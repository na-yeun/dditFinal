<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/provider/filterOption.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>


<div id="filterSection">
    <div class="filter-item">
        <label class="form-label">연도</label>
        <select id="selectYear" class="form-select"></select>
    </div>
    <div class="filter-item">
        <label class="form-label">월</label>
        <select id="selectMonth" class="form-select">
            <option value="">전체</option>
            <option value="01">1월</option>
            <option value="02">2월</option>
            <option value="03">3월</option>
            <option value="04">4월</option>
            <option value="05">5월</option>
            <option value="06">6월</option>
            <option value="07">7월</option>
            <option value="08">8월</option>
            <option value="09">9월</option>
            <option value="10">10월</option>
            <option value="11">11월</option>
            <option value="12">12월</option>
        </select>
    </div>
</div>





<script>
	const contextPath = "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath}/resources/js/app/provider/filterOption.js"></script>