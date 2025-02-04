package kr.or.ddit.vacation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.vacation.vo.VacationStatusVO;

@Mapper
public interface VacationStatusMapper {
	
	/**
	 * 나연(1.8)
	 * empId에 해당하는 올해 연차 데이터가 있는지 없는지 확인
	 * @param vacation
	 * @return
	 */
	public int selectVacationExist(@Param("empId")String empId);
	
	
	/**
	 * 새 직원 혹은 신년의 리스트 추가
	 * 
	 * @return 성공한 행의 개수
	 */
	public int insertVacationStatus(VacationStatusVO status); // insert

	/**
	 * 전체직원, 전체연도 리스트 조회
	 * @return
	 */
	public List<VacationStatusVO> selectVacationStatusList(); // 

	/**
	 * 전체 직원의 올해 휴가 현황 리스트
	 * @return 있으면 vo가 담긴 list, 없으면 null
	 */
	public List<VacationStatusVO> selectVacationStatusThisYearList(); // 전체직원의 현재연도 리스트 조회

	/**
	 * 직원 한 명의 전체 연도 휴가 현황 리스트
	 * @param empId
	 * @return 있으면 vo가 담긴 list, 없으면 null
	 */
	public List<VacationStatusVO> selectVacationStatus(String empId); // 직원 한 명의 전체연도 리스트 조회

	/**
	 * 직원 한 명의 올해 휴가 현황
	 * @param empId
	 * @return 있으면 vo 객체, 없으면 null
	 */
	public VacationStatusVO selectVacationStatusThisYear(String empId); // 직원 한 명의 현재연도 조회

	/**
	 * 상태 업데이트
	 * @param status
	 * @return 성공한 행의 개수
	 */
	public int updateVacationStatus(VacationStatusVO status); // update

	/**
	 * 삭제
	 * @return 성공한 행의 개수
	 */
	public int deleteVacationStatus();

	/**
	 * 휴가 종류 갖고오는 리스트
	 * @return
	 */
	public List<Map<String, Object>> selectVacationTypeList();

	/**
	 * 휴가 상태 업데이트해주기
	 * @param statusVO
	 * @return
	 */
	public int updateVacationStatusCounts(VacationStatusVO statusVO);
	
	
	
	
}
