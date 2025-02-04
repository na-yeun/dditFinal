package kr.or.ddit.teamHistory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.teamHistory.service.TeamHistoryService;
import kr.or.ddit.teamHistory.vo.TeamHistoryVO;

@Controller
@RequestMapping("/{companyId}/teamHistory")
public class TeamHistoryReadController {
	
	@Autowired
	private TeamHistoryService service;
	
	@GetMapping("/teams")
	@ResponseBody
	public List<TeamHistoryVO> getTeamHistories(){
		return service.readTeamHistoryList();
	}
	
	
	
}
