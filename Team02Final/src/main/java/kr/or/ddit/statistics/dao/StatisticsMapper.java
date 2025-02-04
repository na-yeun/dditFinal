package kr.or.ddit.statistics.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.statistics.vo.StatisticsVO;

@Mapper
public interface StatisticsMapper {
	/**
	 * 현재 전 직원의 수를 부서를 기준으로 조회
	 * @return
	 */
	public List<StatisticsVO> selectEmpByDepartmentStatisticsResult();
	
	

}
