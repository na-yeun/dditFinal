package kr.or.ddit.todolist.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.ToDoListGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(of = "todoNo")
@NoArgsConstructor
public class ToDoListVO {
	@NotBlank(groups = {UpdateGroup.class, ToDoListGroup.class})
	private String todoNo;
	private String todoDate;
	
	private Long todoIndex;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 30, message = "30자 이내로 적어주세요.", groups = {InsertGroup.class, UpdateGroup.class})
	private String todoContent;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	private String todoPriority;
	private String todoStatus;
	private String empId;

}
