package kr.or.ddit.vacation.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;

import kr.or.ddit.vacation.service.VacationStatusService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MyVacationListController {
	@Inject
	private VacationStatusService statusSevice;
	
	

}
