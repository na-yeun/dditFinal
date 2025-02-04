package kr.or.ddit.room.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.room.service.RoomService;
import kr.or.ddit.room.vo.RoomVO;
import kr.or.ddit.roomReservation.dao.RoomReservationMapper;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/room")
public class RoomController {
	
	@Inject
	private RoomService service;
	
	@Inject
	private RoomReservationMapper roomReserMapper;
	
//	
//	@ModelAttribute("roomTimeList")
//	public  List<RoomTimeVO> roomTimeList() {
//		return roomtimeMapper.selectRoomTimeList();
//	}
	
	
	public static final String MODELNAME = "room";
	
	@ModelAttribute(MODELNAME)
	   public RoomVO room() {
	      return new RoomVO();
	   }
	
//	@GetMapping
//	public String getRoomList(
//	        @RequestParam(value = "roomGory", required = false) String roomGory,
//	        @RequestParam(value = "keyword", required = false) String keyword,
//	        Model model) {
//
//	    // 방 목록 가져오기
//	    List<RoomVO> roomList = service.readRoomList(pagiong);
//
//	    // 모델에 데이터 추가
//	    model.addAttribute("roomList", roomList);
//
//	    // 검색 결과를 반환
//	    return "room/roomList";
//	}
//	
	
	//시설 리스트
	@GetMapping
	public String getRoomList(
			Authentication authentication,
			@PathVariable("companyId") String companyId,
			@RequestParam(required = false, defaultValue = "1") int page,
			@ModelAttribute("condition") SimpleCondition simpleCondition,
            HttpSession session,
            Model model) {
			
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		
		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		PaginationInfo<RoomVO> paging = new PaginationInfo<RoomVO>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);
		model.addAttribute("roomList", service.readRoomList(paging));
		
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		model.addAttribute("pagingHTML", renderer.renderPagination(paging, "fnPaging"));
	    return "room/roomList";
	}
	

	//시설을 등록하는 폼
	@GetMapping("/newForm")
	public String newRoomForm(Authentication authentication
			,@PathVariable("companyId") String companyId
			,@ModelAttribute(MODELNAME) RoomVO room) {
		// 로그인 한사람만 예약할 수 있음
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		return "room/roomForm";
	}
	
	//시설 등록
	@PostMapping("/new")
	public String insertRoom( Authentication authentication,
			@PathVariable("companyId") String companyId,
			@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) RoomVO room, 
			BindingResult errors,
			RedirectAttributes redirectAttributes,
			Model model) {
		//여기는 관리자만 가능하다...
		// 로그인 한사람만 예약할 수 있음
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		model.addAttribute("myEmp", myEmp);
        String lvn = null;
        redirectAttributes.addFlashAttribute(MODELNAME, room);
        log.info("room >>>>>>>>>>> {}",room);
        if (!errors.hasErrors()){
        	//로직 실행~~
            ServiceResult result = service.createRoom(room);
            switch (result){
            //성공
                case OK:
                	System.out.println("Room data: " + room);
                    lvn = "redirect:/"+companyId+"/room";
                    break;
                    //실패
                default:
                	lvn ="redirect:/"+companyId+"/room/new";
                    redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다.");
                    break;
            }
        } else {
            // 유효성 검사 실패인 경우 기존 데이터를 가지고 등록 폼으로 리다이렉트
            String errAttrName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
            redirectAttributes.addFlashAttribute(errAttrName, errors);
            redirectAttributes.addFlashAttribute("message", "필수데이터 누락했습니다.");
            lvn = String.format("redirect:/%s/room/newForm", companyId);
        }
        return lvn;
	}
	
	
	//시설을 수정하는 폼
		@GetMapping("{roomId}/edit")
		public String uodateRoomForm(
				Authentication authentication,
				@PathVariable("companyId") String companyId,
				@PathVariable("roomId") String roomId,
				 Model model) {
			// 로그인 한사람만 예약할 수 있음
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			EmployeeVO myEmp = (EmployeeVO) account;
			
			if (myEmp == null) {
				throw new IllegalStateException("로그인이 필요합니다.");
			}
			model.addAttribute("myEmp", myEmp);
			RoomVO room = service.readRoom(roomId);
			model.addAttribute(MODELNAME, room); // 모델에 데이터 추가
			return "room/roomEdit";
		}
		
	
		//시설 수정
		@PostMapping("{roomId}/edit")
		public String updateRoom( Authentication authentication,
				@PathVariable("companyId") String companyId,
				@Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) RoomVO room, 
				@PathVariable("roomId") String roomId,
				@RequestParam(value = "roomImgDelete",required = false) Boolean roomImgDelete,
				BindingResult errors,Model model,
				RedirectAttributes redirectAttributes) {
			
			//여기는 관리자만 가능하다...
			// 로그인 한사람만 예약할 수 있음
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			EmployeeVO myEmp = (EmployeeVO) account;
			
			if (myEmp == null) {
				throw new IllegalStateException("로그인이 필요합니다.");
			}
			model.addAttribute("myEmp", myEmp);
			// 이미지 삭제 요청 했을 경우 준비
			 if (Boolean.TRUE.equals(roomImgDelete)) {
			        // 이미지 삭제 요청 시
			        service.deleteRoomImage(room.getRoomId());
			        room.setRoomImg(null); // 기존 이미지 데이터 삭제
			    } else if (room.getRoomImage() == null || room.getRoomImage().isEmpty()) {
			        // 새 이미지를 업로드하지 않은 경우 기존 이미지 유지
			        RoomVO existingRoom = service.readRoom(roomId);
			        if (existingRoom != null && existingRoom.getRoomImg() != null) {
			            room.setRoomImg(existingRoom.getRoomImg());
			        }
			    }
			 
			 
			 
			redirectAttributes.addFlashAttribute(MODELNAME, room);
	        String lvn = null;

	        if (!errors.hasErrors()){
	        	//로직 실행~~
	            ServiceResult result = service.modifyRoom(room);
	            switch (result){
	            //성공
	                case OK:
	                    lvn = "redirect:/"+companyId+"/room";
	                    break;
	             //실패
	                
	                default:
	                	lvn =String.format("redirect:/%s/room/%s/edit", companyId, roomId);
	                    redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다.");
	                    break;
	            }
	        } else {
	            // 유효성 검사 실패인 경우 기존 데이터를 가지고 등록 폼으로 리다이렉트
	            String errAttrName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
	            redirectAttributes.addFlashAttribute(errAttrName, errors);
	            lvn =String.format("redirect:/%s/room/%s/edit", companyId, roomId);
	        }
	        return lvn;
		}
	
	
		//시설 삭제(단일)
		@PostMapping("{roomId}/delete")
		@ResponseBody
		public String deleteRooms(
		        @PathVariable String companyId,
		        @PathVariable String roomId,
		       
		        Model model,
		        HttpSession session,
		        RedirectAttributes redirectAttributes) {

			
				
			    ServiceResult result = service.removeRoom(roomId);
			    if (result == ServiceResult.OK) {
			       
			        return "success";
			    } else {
			        
			        return "error"; // 다시 예약 삭제 도전,,,
			    }

		}
	
	
	
	
	
}
