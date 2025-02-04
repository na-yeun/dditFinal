package kr.or.ddit.expense.service;

import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;

import java.util.List;
import java.util.Map;

public interface ExpenseService {


    public List<Map<String, Object>> readExpenseTypeList();

    public List<Map<String, Object>> readExpenseCategoryList();

    public int processExpenseDocument(String docId, String statusCode);

    /**
     * 지출데이터 삽입하기
     * @param documentId
     * @param dto
     * @param myEmp
     */
    public void preProcessExpenseDocument(String documentId, ApprovalDocumentDTO dto, EmployeeVO myEmp);

    // 부서/분류 코드 조회
    List<Map<String, Object>> retrieveDepartmentList();

    List<CommonCodeVO> retrieveExpenseCategories();

    // 요약 통계
    Map<String, Object> retrieveMonthlyComparison(Map<String, String> filters);
    List<Map<String, Object>> retrieveDepartmentStats(Map<String, String> filters);

    // 분석 통계
    List<Map<String, Object>> retrieveAnalysisStats(Map<String, String> params);

    // 상세 통계
    Map<String, Object> retrieveDetailStats(Map<String, String> params);
}
