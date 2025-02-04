package kr.or.ddit.vacation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import kr.or.ddit.vacation.vo.VacationTypeVO;

public interface VacationStatusService {
	
	
	/**
	 * 새 직원 혹은 신년의 리스트 추가
	 * 
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult createVacationStatus(VacationDTO vacation, Set<String> failEmps); // insert

	/**
	 * @return
	 */
	public List<VacationStatusVO> readVacationStatusList(); // 전체직원, 전체연도 리스트 조회

	/**
	 * 전체 직원의 올해 휴가 현황 리스트
	 * @return 있으면 vo가 담긴 list, 없으면 null
	 */
	public List<VacationStatusVO> readVacationStatusThisYearList(); // 전체직원의 현재연도 리스트 조회

	/**
	 * 직원 한 명의 전체 연도 휴가 현황 리스트
	 * @param empId
	 * @return 있으면 vo가 담긴 list, 없으면 null
	 */
	public List<VacationStatusVO> readVacationStatus(String empId); // 직원 한 명의 전체연도 리스트 조회

	/**
	 * 직원 한 명의 올해 휴가 현황
	 * @param empId
	 * @return 있으면 vo 객체, 없으면 null
	 */
	public VacationStatusVO readVacationStatusThisYear(String empId); // 직원 한 명의 현재연도 조회

	/**
	 * 상태 업데이트
	 * @param status
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult modifyVacationStatus(VacationDTO vacation, Set<String> failEmps); // update

	/**
	 * 삭제
	 * @return 성공 OK, 실패 FAIL
	 */
	public ServiceResult deleteVacationStatus();

	/**
	 * 휴가 타입 리스트갖고오는거임
	 * @return
	 */
	public List<Map<String, Object>> readVacationTypeList();
	
	
    

}
