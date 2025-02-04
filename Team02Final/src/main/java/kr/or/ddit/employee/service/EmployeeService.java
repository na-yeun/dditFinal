package kr.or.ddit.employee.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.organitree.vo.PositionVO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface EmployeeService {
	/**
	 * 직원 한 명의 정보 조회
	 * @param accountMail
	 * @return 없을 경우 PKNotFoundException
	 */
	public EmployeeVO readEmployee(AccountVO account);
	
	
	public EmployeeVO readOneEmployee(String empId);
	
	/**
	 * 회원가입 할 때 oauth 정보없이 employee 조회
	 * @param empMail
	 * @return 없을 경우 PKNotFoundException
	 */
	public EmployeeVO readEmployeeForJoin(String empMail);
	
	/**
	 * 전체 직원의 정보 조회
	 * @return
	 */
	public List<EmployeeVO> readEmployeeList(PaginationInfo<EmployeeVO> paging);
	
	/**
	 * 문자 인증시 사용할 직원 한 명의 정보 조회
	 * @param emp : 이름, mail, 핸드폰 번호가 있음
	 * @return 없을 경우 PKNotFoundException
	 */
	public EmployeeVO readEmployeeForAuth(EmployeeVO emp);
	
	/**
	 * 같은 부서 내 휴가 중인 인원 명단
	 * @param departCode
	 * @return
	 */
	public List<EmployeeVO> readVacationEmployeeList(String departCode);
	
	/**
	 * 직원인증시 직원 한 명의 정보 업데이트(오어스 인증도 함께 진행)
	 * @param emp
	 * @return 
	 */
	public ServiceResult modifyEmployeeAtFirst(EmployeeVO emp);
	
	/**
	 * 비밀번호 재발급 시 비밀번호 업데이트
	 * @param 
	 * @return 
	 */
	public ServiceResult modifyEmployeePassword(EmployeeVO emp);
	
	/**
	 * 나의 정보 업데이트(개인정보 수정시 호출)
	 * @param emp
	 * @return
	 */
	public ServiceResult modifyMyEmployee(EmployeeVO emp);
	
	/**
	 * 관리자의 employee 업데이트
	 * @param emp
	 * @return
	 */
	public ServiceResult modifyEmployee(EmployeeVO emp);
	
	
	/**
	 * 로그인 3회 이상 실패시 직원의 status 변경(N 혹은 Y)
	 * @param accountMail
	 * @return
	 */
	public ServiceResult modifyStatusEmployee(String empMail, String empStatus);
	
	/**
	 * 프로필 사진 삭제요청
	 * @param empId
	 * @return
	 */
	public ServiceResult deleteEmpImg(String empId);
	
	/**
	 * 도장 이미지 삭제요청
	 * @param empId
	 * @return
	 */
	public ServiceResult deleteEmpSignImg(String empId);
	
	/**
	 * 문자 전송 메소드
	 * @param phoneNumber
	 * @param smsText
	 * @return
	 */
	public SingleMessageSentResponse sendOauthSMS(String phoneNumber, String smsText);
	
	/**
	 * 랜덤 인증번호 만드는 메소드
	 * 
	 * @return
	 */
	public String generateVerificationCode();
	

	
	/**
	 * 새로운 직원 추가(account 먼저 추가 후에 진행해야함)
	 * @param empData
	 * @return
	 */
	public ServiceResult createEmployee(List<Map<String, Object>> empData);
	
	/**
	 * 사원 한명 퇴사 처리 상태코드(Q)
	 * @param empId
	 * @return
	 */
	public ServiceResult removeOneEmployee(String empId);
	


	
	
	
	
	/**
	 * 12.13 민경주 - 전자결재 결재선 선택할 때 조직도와 사원을 갖고오게하기 위한 메소드
	 *
	 * @return
	 */
    public List<Map<String, Object>> readEmployeeListForDept();

    
    /**
     * 인사관리 부서 옵션용 목록 
     * @return
     */
    public List<DepartmentVO> readAllDeptList();
    
    /**
     * 인사관리 직급 옵션용 목록
     * @return
     */
    public List<CommonCodeVO> readAllPosi();

	/**
	 * 전자결재 부서명과 직급명도 갖고오기
	 */
	public Map<String, Object> readEmployeePosiNameAndDeptNameByEmpId(String empId);
	
	
	/**
	 * 인사관리 재직상태 옵션용 목록 조회  
	 * @return
	 */
	public List<CommonCodeVO> readStatusType();
	
	
	/**
	 * 인사관리에서 사원 개별 등록 
	 * @param emp
	 * @return
	 */
	public ServiceResult createOneEmployee(EmployeeVO emp);
	
	/**
	 * 인사관리에서 한 사원의 휴가 현황 조회 
	 * @param empId
	 * @param vstaCode
	 * @return
	 */
	public VacationStatusVO readOneVacationStatusDetail(String empId, String vstaCode);
	
	/**
	 * 한 사원의 휴가현황 모달에서 기준년도 옵션으로 사용할 연도 리스트 (민재)
	 * @return
	 */
	public List<VacationStatusVO> readVstaCodeList();
	
	
	/**
	 * 휴가 추가부여 처리 
	 * @return
	 */
	public ServiceResult modifyVacationStatus(VacationStatusVO vs);
	
	
	/** 나의 부서 조회 
	 * @param empId
	 * @return
	 */
	public DepartmentVO readMyDepartName(String empId);
}
