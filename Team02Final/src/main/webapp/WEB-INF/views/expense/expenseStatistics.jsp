<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<security:authorize access="isAuthenticated()">
    <security:authentication property="principal" var="principal"/>
    <c:set value="${principal.account.companyId}" var="companyId"/>
</security:authorize>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="container-fluid">
    <br/>
    <h3 class="mb-4">지출 관리</h3>

    <!-- 탭 메뉴 -->
    <ul class="nav nav-tabs mb-4" role="tablist">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#summary" role="tab">요약 통계</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#analysis" role="tab">통계 분석</a>
        </li>
    </ul>

    <!-- 탭 컨텐츠 -->
    <div class="tab-content">
        <!-- 요약 통계 탭 -->
        <div class="tab-pane fade show active" id="summary">
            <!-- 공통 검색 영역 -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card">
                        <div class="card-body d-flex gap-2">
                            <select class="form-select" id="yearSelect" style="width: auto;">
                                <!-- 옵션은 JS에서 동적으로 생성 -->
                            </select>
                            <select class="form-select" id="monthSelect" style="width: auto;">
                                <!-- 옵션은 JS에서 동적으로 생성 -->
                            </select>
                            <select class="form-select" id="departSelect" style="width: auto;">
                                <option value="">전체 부서</option>
                            </select>
                            <button type="button" id="search-btn" class="btn btn-primary">검색</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <!-- 좌측: 월별 비교 차트 -->
                <div class="col-md-6 mb-4">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="card-title mb-0">월별 지출 비교</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="monthlyComparisonChart" height="300"></canvas>
                        </div>
                    </div>
                </div>

                <!-- 우측: 부서별 지출 현황 -->
                <div class="col-md-6 mb-4">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="card-title mb-0">부서별 지출 현황</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="departmentExpenseChart" height="300"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 상세 분석 영역 (초기에는 숨김) -->
            <div id="detailArea">
                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="card-title" id="detailTitle">상세 분석</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <canvas id="categoryPieChart"></canvas>
                            </div>
                            <div class="col-md-6">
                                <table class="table" id="detailTable">
                                    <thead>
                                    <tr>
                                        <th>지출 분류</th>
                                        <th>금액</th>
                                        <th>비중</th>
                                        <th>건수</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 통계 분석 탭 -->
        <div class="tab-pane fade" id="analysis">
            <div class="card">
                <div class="card-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">지출 분석</h5>
                        <div class="d-flex gap-2 align-items-center">
                            <!-- 뷰 타입 선택 라디오 버튼 -->
                            <div class="btn-group" role="group">
                                <input type="radio" class="btn-check" name="analysisViewType" id="viewTotal" value="total" checked>
                                <label class="btn btn-outline-primary" for="viewTotal">전체</label>
                                <input type="radio" class="btn-check" name="analysisViewType" id="viewDepartment" value="department">
                                <label class="btn btn-outline-primary" for="viewDepartment">부서별</label>
                            </div>

                            <!-- 분석 유형 선택 -->
                            <select class="form-select" id="analysisType" style="width: auto;">
                                <option value="year">연도별</option>
                                <option value="month">월별</option>
                            </select>

                            <!-- 연도 선택 셀렉트 박스 -->
                            <select class="form-select" id="analysisYearSelect" style="width: auto; display: none;">
                                <!-- 옵션은 JS에서 동적으로 생성 -->
                            </select>

                            <button id="backToYearlyBtn" class="btn btn-secondary" style="display: none;">뒤로가기</button>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <canvas id="analysisChart" height="300"></canvas>
                </div>
            </div>

            <!-- 상세 분석 영역 -->
            <div id="analysisDetail">
                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="card-title mb-0" id="analysisDetailTitle">상세 분석</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <canvas id="analysisPieChart" height="300"></canvas>
                            </div>
                            <div class="col-md-6">
                                <div class="table-responsive">
                                    <table class="table" id="analysisDetailTable">
                                        <thead>
                                        <tr>
                                            <th>지출 분류</th>
                                            <th>금액</th>
                                            <th>비중</th>
                                            <th>건수</th>
                                        </tr>
                                        </thead>
                                        <tbody></tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}" />
<input type="hidden" id="companyId" value="${companyId}" />

<script src="${pageContext.request.contextPath}/resources/js/app/expense/expenseStatistics.js"></script>