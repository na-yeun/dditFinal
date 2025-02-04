package kr.or.ddit.employee.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetailsService;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import kr.or.ddit.vacation.vo.VacationStatusVO;

@Mapper
public interface EmployeeMapper{
//	public interface EmployeeMapper extends MyUserDetailsServiceImpl {
	
	
	
	public EmployeeVO selectEmployee(AccountVO account);
	public EmployeeVO selectOneEmployee(@Param("empId")String empId);
	public List<EmployeeVO> selectEmployeeList(PaginationInfo<EmployeeVO> paging);
	public EmployeeVO selectEmployeeForAuth(EmployeeVO emp);
	public EmployeeVO selectEmployeeForJoin(String empMail);
	
	public List<EmployeeVO> selectVacationEmployeeList(@Param("departCode")String departCode);
	
	// 여러명 추가
	public int insertEmployee(Map<String, Object> emp);
	// 한명 개별 추가
	public int insertOneEmployee(EmployeeVO emp);
	
	// empId를 구하기위해 yyyymm 가 몇까지 있는지 조회 
	public String selectMaxEmpIdByJoin(String yyyymm);

	public int updateEmployee(EmployeeVO emp);
	public int updateEmployeeStatus(@Param("empMail")String empMail, @Param("empStatus")String empStatus);
	public int deleteEmployee(@Param("empMail")String empMail);
	public int selectTotalRecord(PaginationInfo<EmployeeVO> paging);
	public int deleteOneEmployee(String empId);
	public List<CommonCodeVO> selectStatusType();
	
	public List<DepartmentVO> selectAllDept();
	public List<CommonCodeVO> selectAllPosi();
	// 프로필 이미지 삭제 요청시
	public int deleteEmpImg(String empId);
	// 도장 이미지 삭제 요청시
	public int deleteEmpSignImg(String empId);

	// 민경주 - 결재선 리스트 갖고오게하기
    public List<EmployeeVO> selectEmployeeListForDept();

	// 민경주 - 사원번호로 부서명 직급명만 갖고오기
	public Map<String, Object> selectEmployeePosiNameAndDeptNameByEmpId(String empId);
    
    /**
     * 사원관리에서 한 사원의 휴가현황 조회 (민재)
     * @param empId
     * @param vstaCode
     * @return
     */
    public VacationStatusVO selectOneVacationStatusDetail(@Param("vstaCode")String vstaCode ,@Param("empId")String empId);
    
    /**
     * 한 사원의 휴가현황 모달에서 기준년도 옵션으로 사용할 연도 리스트 (민재) 
     * @return
     */
    public List<VacationStatusVO> selectVstaCodeList(); 
    
    
    /**
     * 휴가 추가부여 처리 로직 (민재)
     * @param vs
     * @return
     */
    public int updateVacationStatus(VacationStatusVO vs);
    

    
    
    /**
     * 사원의 부서명 조회 
     * @param empId
     * @return
     */
    public DepartmentVO selectMyDepartName(String empId);

	/**
	 * 1월 13일 민경주 - id로 name갖고오기
	 * @param currentEmpId
	 */
    public String selectEmployeeNameById(String currentEmpId);
}
