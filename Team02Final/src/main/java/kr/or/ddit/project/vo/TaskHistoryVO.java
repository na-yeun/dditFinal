package kr.or.ddit.project.vo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TaskHistoryVO {
	
	private String taskId;
	private String taskMethod;
	private String empId;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime taskDate;
	private String taskTitle;
	private String projId;
	// DTO
	private String empName;
}
