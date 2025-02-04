package kr.or.ddit.teamHistory.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.teamHistory.vo.TeamHistoryVO;

public interface TeamHistoryService {
	
	public List<TeamHistoryVO> readTeamHistoryList();
	
	public ServiceResult insertTeamHistory(@Param("list") List<TeamHistoryVO> list);
	
	public ServiceResult updateTeamHistory(TeamHistoryVO teamHistory);
	
	public ServiceResult deleteTeamHistory(String teamId);
	
	
}
