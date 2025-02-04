package kr.or.ddit.roomReservation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.roomReservation.service.RoomReservationService;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
import kr.or.ddit.roomTime.dao.RoomTimeMapper;
import kr.or.ddit.roomTime.service.RoomTimeService;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/roomTime")
public class RoomReservationController {

	@Inject
	private RoomReservationService service;
	
	@Inject
	private RoomTimeService rservice;
	
	public static final String MODELNAME = "roomReser";

	@ModelAttribute(MODELNAME)
	public RoomReservationVO room() {
		return new RoomReservationVO();
	}

	// 시설 하나의 예약 현황
	@GetMapping("{roomId}/rList")
	@ResponseBody // JSON 데이터 반환
	public List<RoomReservationVO> roomReserList(
			Authentication authentication,
			@PathVariable("companyId") String companyId,
			@PathVariable("roomId") String roomId,
			Model model) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		
		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		
		model.addAttribute("myEmp", myEmp);
		return service.readRoomEmpList(roomId);
	}
	
	
	

	// 시설 예약 등록(모달/ ajax로)
	@PostMapping("{roomId}/reser")
	@ResponseBody
	public Map<String, Object> insertRoomReser(Authentication authentication,
			@PathVariable("companyId") String companyId, 
			@PathVariable("roomId") String roomId,
			@RequestBody RoomReservationVO roomReser, 
			BindingResult errors,
			RedirectAttributes redirectAttributes, Model model) {

		// 로그인 한사람만 예약할 수 있음
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		Map<String, Object> response = new HashMap<>();
		
		if (myEmp == null) {
			  response.put("status", "error");
		        response.put("message", "로그인이 필요합니다.");
		        return response;
		}
		
		roomReser.setEmpId(myEmp.getEmpId());
		
		
		// 모달에서 받아온 타임코드와 범위
		// 타임코드와 범위 유효성 검사
	    List<String> timeCodes = roomReser.getTimeCodes();
	    List<String> timeRanges = roomReser.getTimeRanges();
	    if (timeCodes == null || timeRanges == null || timeCodes.size() != timeRanges.size()) {
	        response.put("status", "error");
	        response.put("message", "시간 코드 또는 범위가 유효하지 않습니다.");
	        return response;
	    }

		// 예약 생성
	    ServiceResult createResult = service.createRoomReser(roomReser);
	    if (createResult != ServiceResult.OK) {
	        response.put("status", "error");
	        response.put("message", "예약 생성 실패");
	        return response;
	    }

	    // 타임코드 상태 업데이트
	    for (String timeCode : timeCodes) {
	        log.info("Processing timeCode: {}", timeCode); // 처리 중인 타임코드 확인
	        ServiceResult modifyResult = rservice.modifyRoomReser(roomId, timeCode);
	        log.info("Modify result for timeCode {}: {}", timeCode, modifyResult); // 결과 확인
	        
	        if (modifyResult != ServiceResult.OK) {
	            response.put("status", "error");
	            response.put("message", "타임코드 " + timeCode + " 업데이트 실패");
	            return response;
	        }
	    }

	    response.put("status", "success");
	    response.put("message", "모든 예약이 성공적으로 처리되었습니다.");
	    return response;
	}

	// 예약한 것 삭제 --> 이것도 아작스로~비동기~json~
	@PostMapping("{reserId}/delete")
	@ResponseBody
	public String deleteReservation(
			@PathVariable("reserId") String reserId,
			Authentication authentication,
			HttpSession session, Model model) {
		// myEmp 확인
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		
		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		ServiceResult result = service.removeRoomReser(reserId);
		if (result != ServiceResult.OK) {

			return "room/roomList"; // 다시 예약 삭제 도전,,,
		}

		model.addAttribute("myEmp", myEmp);
		return "redirect:room/roomList"; // 다시 시설 리스트 페이지로 이동하기

	}

}
