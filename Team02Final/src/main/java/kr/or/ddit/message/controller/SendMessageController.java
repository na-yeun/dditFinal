package kr.or.ddit.message.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.message.dao.MessageMappingMapper;
import kr.or.ddit.message.dao.ReceiveMessageMapper;
import kr.or.ddit.message.service.SendMessageService;
import kr.or.ddit.message.vo.MessageMappingVO;
import kr.or.ddit.message.vo.ReceiveMessageVO;
import kr.or.ddit.message.vo.SendMessageVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/message/send")
public class SendMessageController {

	@Inject
	private SendMessageService service;

	@Inject
	private MessageMappingMapper mappingMapper;

	@Inject
	private ReceiveMessageMapper receiveMapper;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public static final String MODELNAME = "sendMessage";

	@ModelAttribute(MODELNAME)
	public SendMessageVO sendMessage() {
		return new SendMessageVO();
	}

	@ModelAttribute("mappingList")
	public List<MessageMappingVO> mappingList() {
		return mappingMapper.selectMappingMessageList();
	}

	@GetMapping
	public String getSendList(Authentication authentication, @PathVariable("companyId") String companyId,
			@RequestParam(required = false, defaultValue = "1") int page,
			@ModelAttribute("condition") SimpleCondition simpleCondition, Model model) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		String empId = myEmp.getEmpId();

		PaginationInfo<SendMessageVO> paging = new PaginationInfo<>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);

		model.addAttribute("sendList", service.readSendMessageList(paging, empId));

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		model.addAttribute("pagingHTML", renderer.renderPagination(paging, "fnPaging"));

		return "message/sendList";
	}

	@GetMapping("{smesId}")
	public ResponseEntity<Map<String, Object>> getSendOne(Authentication authentication,
			@ModelAttribute(MODELNAME) SendMessageVO sendMessage, @PathVariable("companyId") String companyId,
			@PathVariable("smesId") String smesId, Model model) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}

		// 메시지 조회

		List<SendMessageVO> sendMessageList = service.readSendMessageOne(smesId);
		if (sendMessageList == null || sendMessageList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
		}
		// 메시지 조회 결과가 있다면 수신자 이름 추출
		List<String> allReceiverNames = sendMessageList.stream().map(SendMessageVO::getReceiverNames) // 각 메시지의 수신자 이름
																										// 가져오기
				.filter(Objects::nonNull) // null 값 제거
				.flatMap(names -> Arrays.stream(names.split(", "))) // 쉼표로 나누어 개별 이름으로 분리
				.distinct() // 중복 제거
				.collect(Collectors.toList());

		// 수신자 이름을 쉼표로 연결하여 하나의 문자열로 만들기
		String combinedReceiverNames = String.join(", ", allReceiverNames);

		// 최종 응답 데이터 구성
		Map<String, Object> response = new HashMap<>();
		response.put("smesId", sendMessageList.get(0).getSmesId()); // 첫 번째 메시지를 기준으로 ID
		response.put("semergencyYn", sendMessageList.get(0).getSemergencyYn()); // 첫 번째 메시지 긴급 여부
		response.put("smesTitle", sendMessageList.get(0).getSmesTitle()); // 첫 번째 메시지 제목
		response.put("receiverNames", combinedReceiverNames); // 모든 수신자 이름 연결
		response.put("smesContent", sendMessageList.get(0).getSmesContent()); // 첫 번째 메시지 내용
		response.put("smesDate", sendMessageList.get(0).getSmesDate()); // 첫 번째 메시지 날짜

		return ResponseEntity.ok(response);

	}

	@PostMapping
	@ResponseBody
	public Map<String, String> messageSend(Authentication authentication,
			@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) SendMessageVO sendMessage,
			@RequestParam("receivers") String receiversJson) {

		Map<String, String> response = new HashMap<>();

		// 인증된 사용자 정보 가져오기
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}

		// 발신자 설정
		sendMessage.setMessendId(myEmp.getEmpId());

		// 메시지 삽입 처리
		ServiceResult result = service.insertSendMessage(sendMessage);

		if (result != ServiceResult.OK) {
			response.put("result", "FAIL");
			response.put("message", "메시지 전송에 실패했습니다.");
			return response;
		}

		// 수신자 처리
		String[] receiversArray = StringUtils.strip(receiversJson, "[]").split(",");
		List<String> receiverIds = Arrays.stream(receiversArray).map(String::trim).map(s -> s.replace("\"", ""))
				.collect(Collectors.toList());

		for (String receiverId : receiverIds) {
			ReceiveMessageVO receiveMessage = new ReceiveMessageVO();
			receiveMessage.setRmesreceiveId(receiverId);
			receiveMessage.setRmesTitle(sendMessage.getSmesTitle());
			receiveMessage.setRmesContent(sendMessage.getSmesContent());
			receiveMessage.setRemergencyYn(sendMessage.getSemergencyYn());
			receiveMessage.setRsendId(sendMessage.getMessendId());
			receiveMapper.insertReceiveMessage(receiveMessage);

			MessageMappingVO messageMapping = new MessageMappingVO();
			messageMapping.setReceiveId(receiverId);
			messageMapping.setSmesId(sendMessage.getSmesId()); // SMES_ID 설정

			mappingMapper.insertMappingMessage(messageMapping);

			 // WebSocket 알림 전송
	        Map<String, String> payload = new HashMap<>();
	        payload.put("senderId", myEmp.getEmpId());
	        payload.put("sender", myEmp.getEmpName());
	        payload.put("title", sendMessage.getSmesTitle());
	        payload.put("content", sendMessage.getSmesContent());
	        payload.put("receiverId", receiverId);

	        messagingTemplate.convertAndSendToUser(
	        	receiverId, // 수신자 ID
	            "/queue/DM", // WebSocket 경로
	            payload // 전송할 데이터
	        );
	        log.info("WebSocket 메시지 전송: receiverId={}, payload={}", receiverId, payload);
		}

		response.put("result", "OK");
		return response;
	}

	@PostMapping("{smesId}/del")
	@ResponseBody

	public Map<String, Object> deleteMessages(@RequestBody Map<String, Object> request) {
		// 요청에서 smesIds 추출
		List<String> smesIds = (List<String>) request.get("smesIds");
		List<Integer> mapIds = (List<Integer>) request.get("mapIds");
		Map<String, Object> response = new HashMap<>();

		// smesIds와 mapIds 유효성 검사
		if ((smesIds == null || smesIds.isEmpty()) && (mapIds == null || mapIds.isEmpty())) {
			response.put("success", false);
			response.put("message", "삭제할 항목이 없습니다.");
			return response;
		}

		try {
			// smesId와 관련된 메시지 삭제
			if (smesIds != null) {
				for (String smesId : smesIds) {
					service.deleteSendMessage(smesId);
				}
			}

			// mapId와 관련된 맵핑 데이터 삭제
			if (mapIds != null) {
				for (Integer mapId : mapIds) {
					mappingMapper.deleteMappingMessage(mapId);
				}
			}

			response.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "삭제 중 오류가 발생했습니다.");
		}

		return response;
	}

}