package kr.or.ddit.project.vo;

import lombok.Data;

@Data
public class ProjectTaskDTO {
	private String color;
	private String open;
	private String id;
	private String text;
	private String content;
	
    private String start_date;
	private String end_date;
	private String projectMemberid;
	private String projId;
	private Long duration;
	private String parent;
	private Long progress;
	private String status;
}
