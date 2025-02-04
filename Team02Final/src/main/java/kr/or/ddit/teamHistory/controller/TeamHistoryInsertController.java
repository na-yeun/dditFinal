package kr.or.ddit.teamHistory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.teamHistory.service.TeamHistoryService;
import kr.or.ddit.teamHistory.vo.TeamHistoryVO;

@Controller
@RequestMapping("/{companyId}/teamHistory")
public class TeamHistoryInsertController {
	
	@Autowired
	private TeamHistoryService service;
	
	@PostMapping("/addMembers")
	public ResponseEntity<?> insertTeamMember(
	        @RequestBody Map<String, List<TeamHistoryVO>> requestBody) {
	    
	    // Map에서 members 리스트 추출
	    List<TeamHistoryVO> list = requestBody.get("members");
	    
	    for (TeamHistoryVO member : list) {
	    	System.out.println("팀ID : "+ member.getTeamId());
	        System.out.println("사번: " + member.getEmpId());
	    }
	    
	    service.insertTeamHistory(list);
	    
	    return ResponseEntity.ok().build();
	}

	
}
