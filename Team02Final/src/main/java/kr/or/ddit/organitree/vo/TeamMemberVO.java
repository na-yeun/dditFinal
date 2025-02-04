package kr.or.ddit.organitree.vo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TeamMemberVO {

	private String teamMemberid;
	private String teamId;
	private String empId;
	private LocalDate teamAssignmentdate;
	
	
	
}
