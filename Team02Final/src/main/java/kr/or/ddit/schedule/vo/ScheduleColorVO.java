package kr.or.ddit.schedule.vo;

import lombok.Data;

@Data
public class ScheduleColorVO {
	private String schecolorNo;
	private String schetypeId;
	private String empId;
	private String scheId;
	private String scheBcolor;
	private String scheFcolor;
	
	private ScheduleVO schedule;
}
