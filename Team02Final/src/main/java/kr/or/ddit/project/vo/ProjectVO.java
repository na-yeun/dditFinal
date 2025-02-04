package kr.or.ddit.project.vo;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;

@Data
public class ProjectVO {
	private String projId;
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 100)
	private String projTitle;
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 200)
	private String projContent;
	
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate projSdate;
	
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate projEdate;
	
	@NotBlank
	private String projStatus;

	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate projRegidate;
	
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate projEditdate;
	
	@NotBlank
	private String projLastup;
	
	private ProjectDTO projectDto;
	
	private int calculateProgress;
	
	private int taskCalculateProgress;
}
