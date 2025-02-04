package kr.or.ddit.teamHistory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.teamHistory.dao.TeamHistoryMapper;
import kr.or.ddit.teamHistory.vo.TeamHistoryVO;

@Service
public class TeamHistoryServiceImpl implements TeamHistoryService {
	
	@Autowired
	private TeamHistoryMapper mapper;
	
	@Override
	public List<TeamHistoryVO> readTeamHistoryList() {
		return mapper.readTeamHistoryList();
	}

	@Override
	public ServiceResult insertTeamHistory(List<TeamHistoryVO> list) {
		int rowcnt = mapper.insertTeamHistory(list);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult updateTeamHistory(TeamHistoryVO teamHistory) {
		int rowcnt = mapper.updateTeamHistory(teamHistory);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult deleteTeamHistory(String teamId) {
		int rowcnt = mapper.deleteTeamHistory(teamId);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

}
