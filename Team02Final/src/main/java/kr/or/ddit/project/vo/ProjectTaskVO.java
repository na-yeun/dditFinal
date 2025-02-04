package kr.or.ddit.project.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;

@Data
public class ProjectTaskVO {
	@Size(max = 20)
	private String taskColor;
	
	private String taskOpen;
	
	@Size(max = 20)
	private String taskId;
	
	@Size(max = 200)
	private String taskNm;
	
	@Size(max = 500)
	private String taskContent;
	
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate taskSdate;
	
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate taskEdate;
	
	@Size(max = 20)
	private String projectMemberid;

	@Size(max = 20)
	private String projId;
	
	private Long taskDuration;
	
	@Size(max = 20)
	private String taskParentid;
	
	private Long taskProgress;
	
	@Size(max = 30)
	private String taskStatus;
	
	// 수정된 setTaskDuration 메서드
    public void setTaskDuration() {
        if (taskSdate != null && taskEdate != null) {
            // 시작 날짜와 종료 날짜 간의 일수 계산
            this.taskDuration = ChronoUnit.DAYS.between(taskSdate, taskEdate)+1;
        } else {
            this.taskDuration = 0L; // 시작 또는 종료 날짜가 없는 경우 기본값 설정
        }
    }
    
    // DTO
    
    private String empName;
    
    private String modifyName;
    
    private String taskMethod;
    
    private String taskTitle;
    
    private LocalDateTime taskDate;
    
    private String logName;
    
	
}	
