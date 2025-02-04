package kr.or.ddit.expense.dao;

import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.expense.vo.ExpenseGroupVO;
import kr.or.ddit.expense.vo.ExpenseHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExpenseMapper {

    /**
     * 지출 구분 조회해서 갖고오기
     * @return
     */
    public List<Map<String, Object>> selectExpenseTypeList();

    /**
     * 지출 분류 조회해서 갖고오기
     * @return
     */
    public List<Map<String, Object>> selectExpenseCategoryList();

    /**
     * 지출결의서 그룹 저장하기
     * @param expenseGroup
     * @return
     */
    public int insertExpenseGroup(ExpenseGroupVO expenseGroup);

    /**
     * 지출결의서 ID만들기 위해 maxCount 조회하기
     * @param yyMM
     * @return
     */
    public int getNextGroupSeq(String yyMM);

    /**
     * 지출결의서 그룹안에 항목하나씩 넣기
     * @param expenseHistory
     * @return
     */
    public int insertExpenseHistory(ExpenseHistoryVO expenseHistory);

    /**
     * 최종승인으로 데이터 활성화
     * @param docId
     * @return
     */
    public int updateExpenseStatus(String docId);

    /**
     * 반려로 데이터 삭제
     * @param docId
     * @return
     */
    public int deleteExpenseStatus(String docId);

    List<Map<String, Object>> selectDepartmentList();
    List<CommonCodeVO> selectExpenseCategories();

    Map<String, Object> selectMonthlyComparison(Map<String, String> filters);
    List<Map<String, Object>> selectDepartmentStats(Map<String, String> filters);
    List<Map<String, Object>> selectAnalysisStats(Map<String, String> params);
    List<Map<String, Object>> selectDetailCategoryStats(Map<String, String> params);
}
