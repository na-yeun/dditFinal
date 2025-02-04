package kr.or.ddit.expense.controller;

import kr.or.ddit.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/{companyId}/expense/stats")
public class ExpenseStatisticsController {

    private final ExpenseService expenseService;

    // 통계 페이지 진입점
    @GetMapping
    public String statisticsView() {
        return "expense/expenseStatistics";
    }

    // 기본 데이터 로드
    @PostMapping("/summary")
    @ResponseBody
    public ResponseEntity<?> getSummaryStats(@RequestBody Map<String, String> filters) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("monthlyComparison", expenseService.retrieveMonthlyComparison(filters));
            result.put("departmentStats", expenseService.retrieveDepartmentStats(filters));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error loading summary stats", e);
            return ResponseEntity.internalServerError().body("요약 통계 로드 중 오류가 발생했습니다.");
        }
    }

    // 상세 분석 데이터 로드
    @PostMapping("/analysis")
    @ResponseBody
    public ResponseEntity<?> getAnalysisStats(@RequestBody Map<String, String> params) {
        try {
            List<Map<String, Object>> stats = expenseService.retrieveAnalysisStats(params);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error loading analysis stats", e);
            return ResponseEntity.internalServerError().body("분석 통계 로드 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/detail")
    @ResponseBody
    public ResponseEntity<?> getDetailStats(@RequestBody Map<String, String> params) {
        try {
            // type이 있는 경우 체크 로직 변경해야함
            String type = params.get("type");
            if ("month".equals(type) && "department".equals(params.get("viewType"))) {
                String departName = params.get("departName");
                if (departName == null || departName.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("부서별 조회 시 부서명이 필요합니다.");
                }
            }

            Map<String, Object> result = expenseService.retrieveDetailStats(params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in detail stats", e);
            return ResponseEntity.internalServerError().body("상세 통계 조회 중 오류가 발생했습니다.");
        }
    }

    // 부서/분류 코드 조회
    @GetMapping("/codes/{type}")
    @ResponseBody
    public ResponseEntity<?> getCodeList(@PathVariable String type) {
        try {
            List<?> codes = type.equals("department")
                    ? expenseService.retrieveDepartmentList()
                    : expenseService.retrieveExpenseCategories();
            return ResponseEntity.ok(codes);
        } catch (Exception e) {
            log.error("Error loading code list", e);
            return ResponseEntity.internalServerError().body("코드 목록 로드 중 오류가 발생했습니다.");
        }
    }

}