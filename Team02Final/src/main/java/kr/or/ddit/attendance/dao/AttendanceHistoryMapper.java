package kr.or.ddit.attendance.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;

import kr.or.ddit.attendance.vo.AttendTbVO;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;

/**
 * @author 유민재
 *
 */
@Mapper
public interface AttendanceHistoryMapper {
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo paging);
	
	
	/**
	 * 근태 관리 페이징처리
	 * @param paging
	 * @return
	 */
	public int selectHrTotalRecord(PaginationInfo<AttendanceHistoryVO> paging);
	/**
	 * 근태 추가(출근시)
	 * @return 성공한 행 개수
	 */
	public int insertAttendanceHistory(AttendanceHistoryVO attendance);
	
	/**
	 * 근태 수정(퇴근시)
	 * @return 성공한 행 개수
	 */
	public int updateAttendanceHistory(AttendanceHistoryVO attendance);
	
	/**
	 * 근태 삭제
	 * @return 성공한 행 개수
	 */
	public int deleteAttendanceHistory(String atthisId);
	
	/**
	 * 관리자 / 직원 기능 : 직원 한 명의 근태 기록 전체 출력(나의 근태 기록 전체출력)
	 * @param paging
	 * @return 없으면 빈 list
	 */
	public List<AttendanceHistoryVO> selectAttendanceHistoryList(PaginationInfo paging);
	
	
	/**
	 * 근태 관리 전체 조회 및 페이징 처리 (유민재) 
	 * @param paging
	 * @return
	 */
	public List<AttendanceHistoryVO> selectAllAttendanceHistoryList(PaginationInfo<AttendanceHistoryVO> paging);
	
	
	/**
	 * 근태 관리 페이징 처리 없는 엑셀 다운로드 ( 민재)
	 * @param condition
	 * @return
	 */
	public List<AttendanceHistoryVO> selectDownloadAttendaceExcel(Map<String, Object> condition);
	
	
	/**
	 * 근태 기록표 다운로드에 사용할 empName 조회 (민재)
	 * @param SearchWord
	 * @return
	 */
	public List<EmployeeVO> selectEmpName(String SearchWord);
	
	
	
	/**
	 * 현재 설정되어 있는 출퇴근 시간 조회 (민재)
	 * @return
	 */
	public AttendTbVO selectAttendTime();
	
	
	/**
	 * 출근 시간변경에 사용할 시간 조회(민재)
	 * @return
	 */
	public List<CommonCodeVO> selectAllAttendTimeList();
	
	
	/**
	 * 퇴근 시간 변경에 사용할 시간 조회 (민재)
	 * @return
	 */
	public List<CommonCodeVO> selectAllLeaveTimeList();
	
	
	/**
	 * 출퇴근 시간 수정 로직 (민재) 
	 * @param att
	 * @return
	 */
	public int updateAttendTime(AttendTbVO att);
	
	/**
	 * 출근 버튼 클릭한 후 미니프로필에서 출근 시간 조회 로직 (민재)
	 * @param empId
	 * @return
	 */
	public AttendanceHistoryVO selectMyHahisTime(String empId);
	
	/**
	 * 퇴근 버튼 클릭한 후 미니프로필에서 퇴근 시간 조회 로직 (민재) 
	 * @param empId
	 * @return
	 */
	public AttendanceHistoryVO selectMyHleaveTime(String empId);
	
	
	/**
	 * 출근 버튼을 클릭했을 때 출근시간 insert (민재)
	 * @param ah
	 * @return
	 */
	public int insertMyHahisTime(AttendanceHistoryVO ah);

	
	/**
	 * 퇴근 버튼을 클릭했을 때 퇴근시간 insert (민재)
	 * @param ah
	 * @return
	 */
	public int insertMyHleaveTime(AttendanceHistoryVO ah);
	
	/**
	 * 근태리스트에서 한명 근태 상세조회 (민재)
	 * @param empId
	 * @param atthisId 
	 * @return
	 */
	public AttendanceHistoryVO selectOneAttendDetail(@Param("empId") String empId, @Param("atthisId") String atthisId);
	
	
	/**
	 * 근태 상세 조회에서 사용할 출근상태옵션 조회 (민재)
	 * @return
	 */
	public List<CommonCodeVO> selectAttendStatus();
	
	
	/**
	 * 근태 상세 조회에서 사용할 퇴근상태옵션 조회 (민재)
	 * @return
	 */
	public List<CommonCodeVO> selectLeaveStatus();
	
		
	/**
	 * 관리자가 사원의 출퇴근 상태를 변경할 수 있음 (민재)
	 * @param ah
	 * @return
	 */
	public int updateAttendLeaveStatus(AttendanceHistoryVO ah);
	
}
