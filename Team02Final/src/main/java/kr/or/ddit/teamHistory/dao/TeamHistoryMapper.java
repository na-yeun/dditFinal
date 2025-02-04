package kr.or.ddit.teamHistory.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.teamHistory.vo.TeamHistoryVO;

@Mapper
public interface TeamHistoryMapper {
	
	public List<TeamHistoryVO> readTeamHistoryList();
	
	public int insertTeamHistory(@Param("list") List<TeamHistoryVO> list);
	
	public int updateTeamHistory(TeamHistoryVO teamHistory);
	
	public int deleteTeamHistory(String teamId);
	
}
