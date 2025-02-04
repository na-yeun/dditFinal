package kr.or.ddit.todolist.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.todolist.dao.ToDoListMapper;
import kr.or.ddit.todolist.vo.ToDoListVO;
@Service
public class ToDoListServiceImpl implements ToDoListService {
	@Inject
	private ToDoListMapper toDoListMapper;

	@Override
	public ServiceResult createToDoList(ToDoListVO todolist) {
		ServiceResult result = ServiceResult.FAIL;
		int rownum = toDoListMapper.insertToDoList(todolist);
		if(rownum>0) {
			result = ServiceResult.OK;
		}
		return result;
	}

	@Override
	public ServiceResult modifyToDoList(ToDoListVO todolist) {
		ServiceResult result = ServiceResult.FAIL;
		int rownum = toDoListMapper.updateToDoList(todolist);
		if(rownum>0) {
			result = ServiceResult.OK;
		}
		return result;
	}

	@Override
	public ServiceResult deleteToDoList(String todoNo) {
		ServiceResult result = ServiceResult.FAIL;
		int rownum = toDoListMapper.deleteToDoList(todoNo);
		if(rownum>0) {
			result = ServiceResult.OK;
		}
		return result;
	}

	@Override
	public List<ToDoListVO> readToDoListList(String empId) {
		// TODO Auto-generated method stub
		return toDoListMapper.selectToDoListList(empId);
	}

}
