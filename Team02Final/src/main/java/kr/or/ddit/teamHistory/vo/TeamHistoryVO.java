package kr.or.ddit.teamHistory.vo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TeamHistoryVO {
	private String teamId;
	private String empId;
	private LocalDate teamAssignmentdate;
	
	private TeamHistoryDTO teamHistoryDTO;
}
