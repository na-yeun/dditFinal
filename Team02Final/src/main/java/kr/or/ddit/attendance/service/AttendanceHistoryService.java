package kr.or.ddit.attendance.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.attendance.vo.AttendTbVO;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;

public interface AttendanceHistoryService {
	/**
	 * 근태 추가(출근시)
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult createAttendanceHistory(AttendanceHistoryVO attendance);
	
	/**
	 * 근태 수정(퇴근시)
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult modifyAttendanceHistory(AttendanceHistoryVO attendance);
	/**
	 * 근태 삭제
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult deleteAttendanceHistory(String atthisId);

	/**
	 * 관리자 / 직원 기능 : 직원 한 명의 근태 기록 전체 출력(나의 근태 기록 전체출력)
	 * @param paging
	 * @return 없으면 빈 list
	 */
	public List<AttendanceHistoryVO> readAttendanceHistoryList(PaginationInfo paging);
	
	/**
	 * 근태 관리 전체 조회 (유민재)
	 * @param paging
	 * @return
	 */
	public List<AttendanceHistoryVO> readAllAttendanceHistoryList(PaginationInfo<AttendanceHistoryVO> paging);
	
	
	/**
	 * 근태 관리 페이징처리 없는 엑셀 다운로드( 민재) 
	 * @param condition
	 * @return
	 */
	public List<AttendanceHistoryVO> readDownloadAttendanceExcel(Map<String, Object> condition);
	
	
	/**
	 * 근태 관리 엑셀 다운로드명에 사용할 이름 검증 (민재)
	 * @param searchWord
	 * @return
	 */
	public List<EmployeeVO> readEmpName(String searchWord);
	
	
	/**
	 * 현재 설정되어있는 출퇴근 시간 조회 (민재)
	 * @return
	 */
	public AttendTbVO readAttendTime();
	
	
	/**
	 * 출근 시간 변경을 위한 시간 조회(민재)
	 * @return
	 */
	public List<CommonCodeVO> readAllAttendTimeList();
	
	/**
	 * 퇴근 시간 변경을 위한 시간 조회(민재)
	 * @return
	 */
	public List<CommonCodeVO> readAllLeaveTimeList();
	
	/**
	 * 출퇴근 시간을 수정하는 로직 (민재)
	 * @param att
	 * @return
	 */
	public ServiceResult modifyAttendTime(AttendTbVO att);
	
	
	/**
	 * 메인페이지에 보여질 나의 출근시간 조회 (민쟤)
	 * @param empId
	 * @return
	 */
	public AttendanceHistoryVO readMyHahisTime(String empId);
	
	/**
	 * 메인페이지에 보여질 나의 퇴근시간 조회 (민재)
	 * @param empid
	 * @return
	 */
	public AttendanceHistoryVO readMyHleaveTime(String empid);
	
	/**
	 * 출근 버튼 클릭시 출근 상태 insert 로직 (민쟤)
	 * @param ah
	 * @return
	 */
	public ServiceResult addMyHahisTime(AttendanceHistoryVO ah);
	
	/**
	 * 퇴근 버튼 클릭 시 퇴근 상태 insert 로직 (민재)
	 * @param ah
	 * @return
	 */
	public ServiceResult addMyHleaveTime(AttendanceHistoryVO ah);
	
	/**
	 * 근태 목록에서 한명 근태 상세 조회
	 * @param empId
	 * @param atthisId 
	 * @return
	 */
	public AttendanceHistoryVO readOneAttendDetail(String empId, String atthisId);
	
	
	/**
	 * 근태 상세 조회에서 사용할 출근상태옵션 조회
	 * @return
	 */
	public List<CommonCodeVO> readAttendStatus();
	
	/**
	 * 근태 상세 조회에서 사용할 퇴근상태옵션 조회
	 * @return
	 */
	public List<CommonCodeVO> readLeaveStatus();
	
	
	/**
	 * 관리자는 사원의 출퇴근 상태 변경 가능. 
	 * @return
	 */
	public ServiceResult modifyAttendLeaveStatus(AttendanceHistoryVO ah);
}
