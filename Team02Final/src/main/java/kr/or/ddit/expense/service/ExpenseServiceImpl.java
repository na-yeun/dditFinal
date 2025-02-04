package kr.or.ddit.expense.service;


import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.expense.dao.ExpenseMapper;
import kr.or.ddit.expense.vo.ExpenseGroupVO;
import kr.or.ddit.expense.vo.ExpenseHistoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    ExpenseMapper expenseMapper;

    @Override
    public List<Map<String, Object>> readExpenseTypeList() {
        return expenseMapper.selectExpenseTypeList();
    }

    @Override
    public List<Map<String, Object>> readExpenseCategoryList() {
        return expenseMapper.selectExpenseCategoryList();
    }

    @Override
    public int processExpenseDocument(String docId, String statusCode) {

        int result = 0;

        try {
            //최종승인, 전결상태
            if (statusCode.equals("3")) {
                result = expenseMapper.updateExpenseStatus(docId);
            } else if (statusCode.equals("4")) {
                result = expenseMapper.deleteExpenseStatus(docId);
            } else { // 잘못된 상태 코드 처리
                throw new IllegalArgumentException("잘못된 상태 코드입니다: " + statusCode);
            }
            return result;

        } catch (IllegalArgumentException e) {
            System.err.println("잘못된 입력: " + e.getMessage());
            e.printStackTrace();
            return -1; // 에러 발생 시 -1 반환

        } catch (Exception e) {
            System.err.println("문서 후처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return -1; // 에러 발생 시 -1 반환
        }
    }

    /**
     * 지출 결의서 후처리 메소드
     * VACATION_HISTORY 테이블에 대기상태로 데이터 삽입
     */
    @Override
    public void preProcessExpenseDocument(String documentId, ApprovalDocumentDTO dto, EmployeeVO myEmp) {
        try {
            Map<String, Object> formData = dto.getFormData();
            Map<String, Object> expenseData = (Map<String, Object>) formData.get("data");
            List<Map<String, Object>> expenseItems = (List<Map<String, Object>>) expenseData.get("expenseItems");

            ExpenseGroupVO expenseGroup = new ExpenseGroupVO();

            // 그룹 ID 생성 (EG-2501-0001형식)
            String groupId = String.format("EG-%s-%04d",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM")),
                    expenseMapper.getNextGroupSeq(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"))));
            expenseGroup.setGroupId(groupId);

            expenseGroup.setDocId(documentId); // 결재 문서 ID넣기

            expenseGroup.setEmpId(myEmp.getEmpId()); // 기안자 사원번호넣기

            expenseGroup.setExpenseName((String) expenseData.get("expenseName")); // 제목 넣기

            expenseGroup.setTotalAmount(Long.parseLong(expenseData.get("totalAmount").toString().replaceAll("[^0-9]", ""))); // 지출결의서의 총 계 (다합친가격)넣기

            expenseGroup.setExpenseType((String) expenseData.get("expTypeCode")); // 지출결의서 구분(사전 승인/ 사후 정산)

            expenseGroup.setGroupStatus("N"); // 최종승인전까진 무조건 N

            expenseGroup.setDepartCode(myEmp.getDepartCode()); // 250108 지출결의데이터 저장을위해 부서 고정

            //그룹 정보 저장하기
            int groupResult = expenseMapper.insertExpenseGroup(expenseGroup);
            if (groupResult <= 0) {
                throw new RuntimeException("지출 그룹 정보 저장 실패");
            }


            // 2. 각 지출 항목 저장
            for (Map<String, Object> item : expenseItems) {
                ExpenseHistoryVO expenseHistory = new ExpenseHistoryVO();


                expenseHistory.setExpenseItemId((String) item.get("id"));// ID는 클라이언트에서 생성한 값 사용

                expenseHistory.setExpenseDate(LocalDate.parse((String) item.get("date"))); // 지출 일자 변환 및 설정

                expenseHistory.setExpenseDetail((String) item.get("detail"));// 지출 내용

                expenseHistory.setExpenseCategories((String) item.get("categoryCode")); // 지출분류 개별 저장

                expenseHistory.setPaymentMethod((String) item.get("paymentMethod")); // 결제수단 개별 저장

                expenseHistory.setQuantity(Long.parseLong((String) item.get("quantity"))); // 몇개인지

                expenseHistory.setUnitPrice(Long.parseLong((String) item.get("price"))); // 얼마인지

                expenseHistory.setExpenseAmount(Long.parseLong(item.get("amount").toString().replaceAll("[^0-9]", ""))); // 총합은?

                expenseHistory.setGroupId(groupId); // 참조키 그룹ID넣기

                // 항목 저장
                int result = expenseMapper.insertExpenseHistory(expenseHistory);
                if (result <= 0) {
                    throw new RuntimeException("지출 내역 저장 실패: " + item);
                }
            }

            log.info("지출 결의서 후처리 완료: {}", documentId);
        } catch (Exception e) {
            log.error("지출 결의서 후처리 중 오류 발생: ", e);
            throw new RuntimeException("지출 결의서 후처리 실패", e);

        }

    }

    @Override
    public List<Map<String, Object>> retrieveDepartmentList() {
        return expenseMapper.selectDepartmentList();
    }

    @Override
    public List<CommonCodeVO> retrieveExpenseCategories() {
        return expenseMapper.selectExpenseCategories();
    }

    @Override
    public Map<String, Object> retrieveMonthlyComparison(Map<String, String> filters) {
        Map<String, Object> result = expenseMapper.selectMonthlyComparison(filters);
        if (result == null) {
            result = new HashMap<>();
            result.put("CURRENT_AMOUNT", 0L);
            result.put("PREV_AMOUNT", 0L);
            result.put("GROWTH_RATE", 0.0);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> retrieveDepartmentStats(Map<String, String> filters) {
        List<Map<String, Object>> result = expenseMapper.selectDepartmentStats(filters);
        return result != null ? result : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> retrieveAnalysisStats(Map<String, String> params) {
        List<Map<String, Object>> result = expenseMapper.selectAnalysisStats(params);
        return result != null ? result : new ArrayList<>();
    }

    public Map<String, Object> retrieveDetailStats(Map<String, String> params) {
        // 연도가 미선택(전체)인 경우 현재 연도로 설정
        if (params.get("year") == null || params.get("year").trim().isEmpty()) {
            int currentYear = Year.now().getValue();
            params.put("year", String.valueOf(currentYear));
            // 로깅 추가
            log.info("연도 미선택, 현재 연도({})로 대체", currentYear);
        }

        List<Map<String, Object>> stats = expenseMapper.selectDetailCategoryStats(params);


        Map<String, Object> result = new HashMap<>();
        result.put("categoryStats", stats);

        // 추가 메타 데이터
        result.put("year", params.get("year"));
        result.put("month", params.get("month"));
        result.put("departName", params.get("departName"));
        result.put("type", params.get("type"));

        return result;
    }



}
