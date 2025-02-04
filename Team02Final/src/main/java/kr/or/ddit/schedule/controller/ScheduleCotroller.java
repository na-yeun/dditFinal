package kr.or.ddit.schedule.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.schedule.dao.ScheduleColorMapper;
import kr.or.ddit.schedule.service.ScheduleColorService;
import kr.or.ddit.schedule.service.ScheduleService;
import kr.or.ddit.schedule.vo.ScheduleColorVO;
import kr.or.ddit.schedule.vo.ScheduleVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/schedule")
public class ScheduleCotroller {

	@Inject
	private ScheduleService scheduleService;
	@Inject
	private ScheduleColorService colorService;

	@Inject
	private ScheduleColorMapper colorMapper;

	public static final String MODELNAME = "schedule";

	@ModelAttribute(MODELNAME)
	public ScheduleVO schedule() {
		return new ScheduleVO();
	}

	// 불러온 일정을 여기다가...찍어줘야할 곳이 하나 필요
	@GetMapping("/cal")
	public String getSchedule(@PathVariable("companyId") String companyId, HttpSession session, Model model) {

		return "schedule/scheduleList"; // JSON 형태로 반환

	}

	// 일정 불러오기
	@GetMapping
	@ResponseBody
	public List<Map<String, Object>> getScheduleList(Authentication authentication,
			@RequestParam(value = "schetypeId", required = false) String schetypeId, Model model, HttpSession session) {

		// 로그인한 사원의 정보 가져오기
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		model.addAttribute("myEmp", myEmp);
		model.addAttribute("schetypeId", schetypeId); // JSP로 전달
		// 로그인된 사용자 정보
		String departCode = myEmp.getDepartCode();
		String empId = myEmp.getEmpId();

		// 서비스 호출 및 결과 반환
		List<ScheduleVO> scheduleList = scheduleService.readScheduleList(schetypeId, departCode, empId);

		// js로 보내기위해서 맵선언
		List<Map<String, Object>> result = new ArrayList<>();

		for (ScheduleVO schedule : scheduleList) {
			if (schedule == null)
				continue;
			// 캘린더에서 보여지는 애들
			Map<String, Object> event = new HashMap<>();
			event.put("id", schedule.getScheId());
			event.put("title", schedule.getScheTitle());
			event.put("start", schedule.getScheSdate());
			event.put("end", schedule.getScheEdate());
			event.put("backgroundColor", schedule.getScheBcolor());
			event.put("textColor", schedule.getScheFcolor());
			// 모달에 보여지는 애들
			Map<String, Object> extendedProps = new HashMap<>();
			extendedProps.put("scheContent", schedule.getScheContent());
			extendedProps.put("schetypeId", schedule.getSchetypeId());
			extendedProps.put("empId", schedule.getEmpId());
			extendedProps.put("myEmpId", myEmp.getEmpId());
			extendedProps.put("posiId", myEmp.getPosiId());
			extendedProps.put("departCode", (schedule.getDepart() != null) ? schedule.getDepart().getDepartCode() : "");

			// FullCalendar에서 확장 속성으로 추가
			event.put("extendedProps", extendedProps);

			result.add(event);
		}

		return result;
	}

	// 일정 추가
	@ResponseBody
	@PostMapping("add")
	public ResponseEntity<String> scheuleAdd(Authentication authentication, @PathVariable("companyId") String companyId,
			@RequestBody ScheduleVO schedule, RedirectAttributes redirectAttributes, Model model) {

		// 로그인한 사원의 정보 가져오기
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		model.addAttribute("myEmp", myEmp);

		if (schedule.getScheTitle() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("제목은 필수입니다.");
		}

		ServiceResult result = scheduleService.createSchedule(schedule);

		switch (result) {
		case OK:
			return ResponseEntity.ok("등록 성공");
		case PKDUPLICATED:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PK 충돌");
		default:
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 등록 실패");
		}
	}

	// 일정 수정
	@ResponseBody
	@PostMapping("{scheId}/edit")
	public ResponseEntity<String> scheuleEdit(Authentication authentication,
			@PathVariable("companyId") String companyId, @RequestBody ScheduleVO schedule,
			@PathVariable("scheId") String scheId, Model model) {

		// 로그인한 사원의 정보 가져오기
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		model.addAttribute("myEmp", myEmp);

		try {
			if (schedule.getScheTitle() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("제목은 필수입니다.");
			}

			ServiceResult result = scheduleService.modifySchedule(schedule);
			if (result != ServiceResult.OK) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 수정 실패");
			}

			return ResponseEntity.ok("수정 성공");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
		}
	}

	// 부서랑 회사 일정 색깔 가져오기 --> 모달에 띄어서 바꿔야하니까
	@GetMapping("getColors")
	@ResponseBody
	public ResponseEntity<Map<String, String>> getColorSettings(Authentication authentication,
			@PathVariable String companyId, Model model,
			@RequestParam(value = "schetypeId", required = false) String schetypeId) {

		// 색깔 컬럼을 담기위한 맵
		Map<String, String> colorSettings = new HashMap<>();

		// 로그인한 사원의 정보 가져오기
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		model.addAttribute("myEmp", myEmp);

		// 데이터베이스에서 색상 데이터를 조회
		List<ScheduleColorVO> colors = colorService.readColorList();

		if (colors.size() != 0) {
			for (ScheduleColorVO color : colors) {

				if (color.getSchetypeId().equals("2")) {

					// schetypeId가 2인 경우 (부서 일정)
					colorSettings.put("deptBackgroundColor", color.getScheBcolor());
					colorSettings.put("deptTextColor", color.getScheFcolor());
					log.info("colorSettings!!!!!!!!{}", colorSettings);
				}

				else if (color.getSchetypeId().equals("1")) {
					// schetypeId가 1인 경우 (회사 일정)
					colorSettings.put("companyBackgroundColor", color.getScheBcolor());
					colorSettings.put("companyTextColor", color.getScheFcolor());
				}
			}
		}

		// 기본값 설정 (필요한 경우)
		colorSettings.putIfAbsent("companyBackgroundColor", "#87CEEB");
		colorSettings.putIfAbsent("companyTextColor", "#000000");
		colorSettings.putIfAbsent("deptBackgroundColor", "#FFFACD");
		colorSettings.putIfAbsent("deptTextColor", "#000000");

		log.info("colorSettings??????{}", colorSettings);

		return ResponseEntity.ok(colorSettings);
	}

	// 부서랑 회사 일정 색깔 고정 및 커스텀
	@ResponseBody
	@PostMapping("updateColors")
	public ResponseEntity<String> updateColors(Authentication authentication,
			@PathVariable("companyId") String companyId, @RequestBody Map<String, String> colorData, Model model) {

		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		model.addAttribute("myEmp", myEmp);

		if (myEmp == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		// 스케줄id(회사/부서/개인) js에서 가져와서 넣어주기
		String schetypeId = colorData.get("schetypeId");
		// 새로 저장한 배경색 및 글씨색 가져오기
		// 배경색 가져오기
		String scheBcolor = colorData.get("backgroundColor");
		// 글씨색 가져오기
		String scheFcolor = colorData.get("textColor");

		// 검증 체크
		if (schetypeId == null || scheBcolor == null || scheFcolor == null) {
			return ResponseEntity.badRequest().body("필수 데이터가 누락되었습니다.");
		}

		// ScheduleColorVO 생성해서 컬러테이블에 set해주기
		ScheduleColorVO scheduleColors = new ScheduleColorVO();
		scheduleColors.setSchetypeId(schetypeId);
		scheduleColors.setScheBcolor(scheBcolor);
		scheduleColors.setScheFcolor(scheFcolor);

		try {
			// 색상이 존재하는지 확인>> 확인하는 이유는 1,2번(회사, 부서)같은 경우 모든 회사 일정, 모든 부서 일정을
			// 업데이트해야하기 떄문에 회사, 부서 일정이 등록되어있으면 업데이트를 해야하고 없으면 인서트해야하기 때문에
			// 있는지 없는지 확인을 위해서 리스트를 가져옴
			List<ScheduleColorVO> existingColor = colorMapper.selectColorList();

			if (existingColor != null) {
				// 색상이 존재하면 업데이트
				int rowsUpdated = colorMapper.updateColor(scheduleColors);
				if (rowsUpdated > 0) {
					return ResponseEntity.ok("색상 업데이트 성공");
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("색상 업데이트 실패");
				}
			} else {
				// 색상이 없으면 기본값 삽입
				int rowsInserted = colorMapper.insertColor(scheduleColors);
				if (rowsInserted > 0) {
					return ResponseEntity.ok("색상 기본값 삽입 성공");
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("색상 삽입 실패");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
		}
	}

	// 일정 삭제
	@ResponseBody
	@DeleteMapping("/{scheId}/delete")
	public String deleteSchedule(Authentication authentication, @PathVariable("scheId") String scheId,
			HttpSession session, @RequestBody Map<String, String> requestData) {

		// 요청데이터에서 사원의 아이디 가져오기
		String empId = requestData.get("empId");
		String departCode = requestData.get("departCode");

		// 세션 정보 확인
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;

		// 권한 체크 로직 수정
		if (myEmp == null || !myEmp.getEmpId().equals(empId) || !myEmp.getDepartCode().equals(departCode)) {
			log.warn("권한이 없는 사용자: empId={}, departCode={}", empId, departCode);
			return "fail";
		}

		// 삭제 서비스 호출
		ServiceResult result = scheduleService.removeSchedule(scheId);
		if (result == ServiceResult.OK) {
			return "success";
		} else {
			return "error";
		}
	}

}
