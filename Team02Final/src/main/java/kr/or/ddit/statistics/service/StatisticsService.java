package kr.or.ddit.statistics.service;

import java.util.List;

import kr.or.ddit.statistics.vo.StatisticsVO;

public interface StatisticsService {
	/**
	 * 현재 전 직원의 수를 부서를 기준으로 조회
	 * @return
	 */
	public List<StatisticsVO> readEmpByDepartmentStatisticsResult();
	
	/**
	 * 현재 전 직원의 수를 직급을 기준으로 조회
	 * @return
	 */
	public List<StatisticsVO> readEmpByPositionStatisticsResult();
	
	/**
	 * 현재 전 직원의 수를 입사연도 기준으로 조회
	 * @return
	 */
	public List<StatisticsVO> readEmpByJoinYearStatisticsResult();

	
}
