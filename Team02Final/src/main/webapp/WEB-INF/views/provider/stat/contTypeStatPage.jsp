<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/provider/contTypeStatPage.css">
<h4 id="page-title">업종별 계약 분석</h4>
<%@ include file="/WEB-INF/views/provider/common/filterOption.jsp" %>



<div id="chartContainer">
    <canvas id="contractTypeChart"></canvas>
</div>

<div id="rejectionChartContainer" class="collapse">
    <!-- 차트 (왼쪽) -->
    <div id="statsChartContainer">
        <canvas id="statsChart"></canvas>
    </div>

    <!-- 테이블 (오른쪽) -->
    <div id="statsTablesContainer">
        <div class="stats-table">
            <h4>업체 규모</h4>
            <table id="scaleTable">
                <thead>
                    <tr><th>업체 규모</th><th>계약건 수</th></tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

        <div class="stats-table">
            <h4>스토리지용량</h4>
            <table id="storageTable">
                <thead>
                    <tr><th>용량</th><th>계약건 수</th></tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

        <div class="stats-table">
            <h4>사용 인원</h4>
            <table id="empCountTable">
                <thead>
                    <tr><th>사용 인원</th><th>계약건 수</th></tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
</div>








<script src="${pageContext.request.contextPath }/resources/js/app/provider/contTypeStatPage.js"></script>