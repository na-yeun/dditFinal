package kr.or.ddit.project.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.ddit.commons.validate.InsertGroup;
import lombok.Data;

@Data
public class ProjectMemberVO implements Serializable{
	
	private String projectMemberid;
	
	@NotBlank(groups = {InsertGroup.class})
	@Size(max = 20)
	private String projId;
	
	@NotNull(groups = {InsertGroup.class})
	@DateTimeFormat(iso = ISO.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate joinDate;
	
	@NotNull(groups = {InsertGroup.class})
	@DateTimeFormat(iso = ISO.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate leaveDate;
	
	@NotBlank(groups = {InsertGroup.class})
	private String projectRolenm;
	private String depatId;
	private String teamId;
	private String empId;
	
	private String codeComment;
	private String empName;
	private String departName;
	
	public String getJoinDateForDb() {
	    return joinDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	public String getLeaveDateForDb() {
	    return leaveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	
}
