package kr.or.ddit.todolist.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.todolist.vo.ToDoListVO;

public interface ToDoListService {
	
	public ServiceResult createToDoList(ToDoListVO todolist);
	public ServiceResult modifyToDoList(ToDoListVO todolist);
	public ServiceResult deleteToDoList(String todoNo);
	public List<ToDoListVO> readToDoListList(String empId);

}
