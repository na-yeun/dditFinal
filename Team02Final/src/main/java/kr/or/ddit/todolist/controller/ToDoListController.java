package kr.or.ddit.todolist.controller;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.ToDoListGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.todolist.service.ToDoListService;
import kr.or.ddit.todolist.vo.ToDoListVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("{companyId}/todo")
public class ToDoListController {
	@Inject
	private ToDoListService toDoListService;
	
	@PostMapping
	public ResponseEntity postToDoList(
			Authentication authentication
			, @RequestBody @Validated(InsertGroup.class) ToDoListVO todolist
			, BindingResult errors
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO)account;
		
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body("검증 실패");
		} else {
			todolist.setEmpId(myEmp.getEmpId());
			ServiceResult result = toDoListService.createToDoList(todolist);
			if(result==ServiceResult.OK) {
				return ResponseEntity.ok("성공");
			} else {
				return ResponseEntity.badRequest().body("필수 파라미터 누락");
			}
		}
	}
	
	@PutMapping("{todoNo}")
	@ResponseBody
	public ResponseEntity updateTodoList(
			@PathVariable("todoNo") String todoNo
			, @Validated(UpdateGroup.class)@RequestBody ToDoListVO todolist
			, BindingResult errors
	) {
		
		if(errors.hasErrors()) {
			// 검증 실패
			return ResponseEntity.badRequest().body("검증 실패");
		} else {
			if(todoNo.equals(todolist.getTodoNo())) {
				// 둘이 같아야 업데이트 할 수 있음
				ServiceResult result = toDoListService.modifyToDoList(todolist);
				
				if(result==ServiceResult.OK) {
					return ResponseEntity.ok("성공");
				} else {
					return ResponseEntity.internalServerError().body("서버 오류");
				}
			} else {
				return ResponseEntity.badRequest().body("검증 실패");
			}
			
		}
		
	}
	@PutMapping("{todoNo}/check")
	@ResponseBody
	public ResponseEntity checkTodoList(
			@PathVariable("todoNo") String todoNo
			, @Validated(ToDoListGroup.class)@RequestBody ToDoListVO todolist
			, BindingResult errors
			) {
		
		if(errors.hasErrors()) {
			// 검증 실패
			return ResponseEntity.badRequest().body("검증 실패");
		} else {
			if(todoNo.equals(todolist.getTodoNo())) {
				// 둘이 같아야 업데이트 할 수 있음
				ServiceResult result = toDoListService.modifyToDoList(todolist);
				
				if(result==ServiceResult.OK) {
					return ResponseEntity.ok("성공");
				} else {
					return ResponseEntity.internalServerError().body("서버 오류");
				}
			} else {
				return ResponseEntity.badRequest().body("검증 실패");
			}
			
		}
	}
	
	@DeleteMapping("{todoNo}")
	@ResponseBody
	public ResponseEntity deleteToDoList(
			Authentication authentication
			, @PathVariable("todoNo") String todoNo
			) {
		
		if(todoNo==null) {
			return ResponseEntity.badRequest().body("to do list 번호 누락");
		} else {
			ServiceResult result = toDoListService.deleteToDoList(todoNo);
			if(result==ServiceResult.OK) {
				return ResponseEntity.ok(null);
			} else {
				return ResponseEntity.internalServerError().body(null);
			}
		}	
		
		
	}
}
