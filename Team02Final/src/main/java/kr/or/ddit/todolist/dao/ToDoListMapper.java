package kr.or.ddit.todolist.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.todolist.vo.ToDoListVO;

@Mapper
public interface ToDoListMapper {
	// crud
	public int insertToDoList(ToDoListVO todolist);
	public int updateToDoList(ToDoListVO todolist);
	public int deleteToDoList(@Param("todoNo")String todoNo);
	public List<ToDoListVO> selectToDoListList(@Param("empId")String empId);
}
