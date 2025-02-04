<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/provider/monthlyContStatPage.css">    
    
<h4 id="page-title">월별 계약 추세</h4>
<%@ include file="/WEB-INF/views/provider/common/filterOption.jsp" %>

<div id="chartContainer">
    <canvas id="monthlyContractChart"></canvas>
</div>

<!-- <div id="dateValue"></div> -->
<div id="rejectionChartContainer" class="collapse">

    <!-- 차트 (왼쪽) -->
    <div id="statsChartContainer">
        <canvas id="statsChart"></canvas>
    </div>

    <!-- 테이블 (오른쪽) -->
    <div id="statsTablesContainer">
        <div class="stats-table">
            <h4>업종</h4>
            <table id="contractTypeTable">
                <thead>
                    <tr><th>업종</th><th>계약건수</th></tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
        <div class="stats-table">
            <h4>업체 규모</h4>
            <table id="scaleTable">
                <thead>
                    <tr><th>업체 규모</th><th>계약건수</th></tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
</div>


<script src="${pageContext.request.contextPath }/resources/js/app/provider/monthlyContStatPage.js"></script>

