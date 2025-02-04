package kr.or.ddit.teamHistory.vo;

import java.util.List;

import lombok.Data;

@Data
public class TeamDTO {
	private String teamId;
	private List<TeamHistoryVO> teamHistoryList;
}
