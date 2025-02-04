package kr.or.ddit.event.loginGmailEvent;

import java.io.IOException;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.google.api.client.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.service.GmailListCommonService;
import kr.or.ddit.gmail.service.MailDeleteService;
import kr.or.ddit.gmail.service.MailDraftService;
import kr.or.ddit.gmail.service.MailImportantService;
import kr.or.ddit.gmail.service.MailReceivedService;
import kr.or.ddit.gmail.service.MailSentService;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.gmail.vo.MailDraftVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSentVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Component
public class GmailListEventListener {
	@Inject
	private GoogleOAuthService googleOAuthServie;
	
	@Inject
	private MailReceivedService rMailService;
	@Inject
	private MailSentService sMailService;
	@Inject
	private MailDraftService dMailService;
	@Inject
	private MailDeleteService delMailService;
	@Inject
	private MailImportantService iMailService;
	@Inject
	private GmailListCommonService common;

	
	/**
	 * 받은 메일 호출
	 * 
	 * @param event
	 */
	@EventListener
	@Async
	public void getRecievedMailList(LoginSuccessEvent event) {
		
		// 1. api 호출을 위한 url설정과 oauth 정보 확인

		// 사용 가능한 access token을 발급받은 oauth 객체임!
		OAuthVO myOAuth = googleOAuthServie.getUsableAccessToken(event.getMyOAuth(), event.getMyOAuth().getOauthEmpmail());
		// 받은 메일함을 불러오는 api의 쿼리파라미터(쿼리 파라미터에 따라 조건이 달라짐)
		StringBuffer inboxQuery = new StringBuffer();
		// 받은 메일함 : messages?q=label:inbox
		inboxQuery.append("label:inbox");

		String empMail = myOAuth.getOauthEmpmail(); // 메일 주소
		String empId = myOAuth.getEmpId(); // 사번


		inboxQuery.append(" newer_than:10d");
		try {
			// google api 호출(받은 메일 리스트를 담은 response 리턴)
			HttpResponse mailInboxListResponse = common.getMailList("messages", inboxQuery, myOAuth);
			// return 받은 메일의 리스트에서 "message"라는 이름의 Json 배열 가져오기
			// (message가 메일임, 메일 정보를 담은 json 배열을 가져온다는 말임)

			String listResponseToString = mailInboxListResponse.parseAsString();
			JsonObject jsonResponse = JsonParser.parseString(listResponseToString).getAsJsonObject();
			JsonArray messages = jsonResponse.getAsJsonArray("messages");

			if (messages == null || messages.size() == 0) {
				// 아무것도 없으면 추가적인 작업을 하지 않아도 됨!
			} else {
				// 메일이 있을 경우
				// 메일 여러 개가 담긴 JsonArray를 순차적으로 돌면서 메일 한 개씩 접근
				for (JsonElement message : messages) {
					// 이메일 한 개의 고유 id를 가져옴, json 객체의 id라는 이름으로 메일의 id가 담겨있음
					// 그 메일 id로 식별하기 때문에 받아놔야함
					// 이 id로 메일 한 개의 디테일한 정보를 가지고 올 수 있음
					String mailMessageId = message.getAsJsonObject().get("id").getAsString();
					
					// data base에 있는지 없는지 확인하고, 없으면 저장하는 코드

					MailReceivedVO receivedCheck = new MailReceivedVO();
					receivedCheck.setEmpId(empId);
					receivedCheck.setEmpMail(empMail);
					receivedCheck.setMailMessageId(mailMessageId);

					MailImportantVO importantCheck = new MailImportantVO();
					importantCheck.setEmpId(empId);
					importantCheck.setEmpMail(empMail);
					importantCheck.setMailMessageId(mailMessageId);

					int rMailresult = rMailService.readReceivedMailExist(receivedCheck);
					int iMailresult = iMailService.readImportantMailExist(importantCheck);
					if (rMailresult == 0 || iMailresult == 0) {
						// google api 호출(메일 한 개의 상세정보 리턴)
						HttpResponse detailResponse = common.getMailDetail("messages", mailMessageId, myOAuth);
						JsonObject detailJson = JsonParser.parseString(detailResponse.parseAsString())
														  .getAsJsonObject();

						JsonArray labelIds = detailJson.getAsJsonArray("labelIds");
						if (labelIds != null) {
							// 라벨 아이디가 있을 때,
							boolean isStarred = false;
							
							for (JsonElement labelId : labelIds) {
								String label = labelId.getAsString();
								
								if (label.equalsIgnoreCase("STARRED")) {
									common.setImportantMail(detailJson, mailMessageId, myOAuth);
						            isStarred = true; // STARRED가 발견됨
						            break; // 더 이상 반복할 필요 없음
						        }
							}
							if(!isStarred) {
								common.setReceivedMail(detailJson, mailMessageId, myOAuth);
							}
						} else {
							// 라벨 아이디가 없으면 received mail에 그냥 저장
							common.setReceivedMail(detailJson, mailMessageId, myOAuth);
						}
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 보낸 메일 호출
	 * 
	 * @param event
	 */
	@EventListener
	@Async
	public void getSentMailList(LoginSuccessEvent event) {
		// oauth 객체
		OAuthVO myOAuth = googleOAuthServie.getUsableAccessToken(event.getMyOAuth(), event.getMyOAuth().getOauthEmpmail());
		// 받은 메일함을 불러오는 api의 쿼리파라미터(쿼리 파라미터에 따라 조건이 달라짐)
		StringBuffer sentQuery = new StringBuffer();
		// 받은 메일함 : messages?q=label:inbox
		sentQuery.append("label:sent");

		String empMail = myOAuth.getOauthEmpmail(); // 메일 주소
		String empId = myOAuth.getEmpId(); // 사번
		
		/* google에서 after를 지원하지 않음
		String apiCallTime = sMailService.readLastApiCallTime(empMail);

		if (apiCallTime != null) {
		// api 마지막 호출 시간을 유닉스 타임스탬프로 변환(쿼리파라미터가 유닉스 타임스탬프로 값을 받음..)
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
			LocalDateTime dateTime = LocalDateTime.parse(apiCallTime, formatter);
			long unixTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
			sentQuery.append(" newer_than:80d");
			sentQuery.append(String.format(" after:%s %s", unixTimestamp, "newer_than:1000d"));
//		}
		 */
		
		sentQuery.append(" newer_than:10d");
		try {
			// google api 호출(받은 메일 리스트를 담은 response 리턴)
			HttpResponse mailInboxListResponse = common.getMailList("messages", sentQuery, myOAuth);

			// return 받은 메일의 리스트에서 "message"라는 이름의 Json 배열 가져오기
			// (message가 메일임, 메일 정보를 담은 json 배열을 가져온다는 말임)

			String listResponseToString = mailInboxListResponse.parseAsString();
			JsonObject jsonResponse = JsonParser.parseString(listResponseToString).getAsJsonObject();
			JsonArray messages = jsonResponse.getAsJsonArray("messages");

			if (messages == null || messages.size() == 0) {
				// 아무것도 없으면 추가적인 작업을 하지 않아도 됨!
			} else {
				// 메일이 있을 경우
				// 메일 여러 개가 담긴 JsonArray를 순차적으로 돌면서 메일 한 개씩 접근
				for (JsonElement message : messages) {
					// 이메일 한 개의 고유 id를 가져옴, json 객체의 id라는 이름으로 메일의 id가 담겨있음
					// 그 메일 id로 식별하기 때문에 받아놔야함
					// 이 id로 메일 한 개의 디테일한 정보를 가지고 올 수 있음
					String mailMessageId = message.getAsJsonObject().get("id").getAsString();

					MailSentVO sent = new MailSentVO();
					sent.setEmpId(empId);
					sent.setEmpMail(empMail);
					sent.setMailMessageId(mailMessageId);
					int sresult = sMailService.readSentMailExist(sent);

					MailImportantVO important = new MailImportantVO();
					important.setEmpId(empId);
					important.setEmpMail(empMail);
					important.setMailMessageId(mailMessageId);
					int iresult = iMailService.readImportantMailExist(important);
					// 3. 메일 id를 통해 메일 한 개의 정보 가지고 오기

					if (sresult == 0 || iresult == 0) {
						// google api 호출(메일 한 개의 상세정보 리턴)
						HttpResponse detailResponse = common.getMailDetail("messages", mailMessageId, myOAuth);
						JsonObject detailJson = JsonParser.parseString(detailResponse.parseAsString())
								.getAsJsonObject();

						JsonArray labelIds = detailJson.getAsJsonArray("labelIds");
						if (labelIds != null) {
							// 라벨 아이디가 있을 때,
							boolean isStarred = false;
							
							for (JsonElement labelId : labelIds) {
								String label = labelId.getAsString();
								
								if (label.equalsIgnoreCase("STARRED")) {
									common.setImportantMail(detailJson, mailMessageId, myOAuth);
						            isStarred = true; // STARRED가 발견됨
						            break; // 더 이상 반복할 필요 없음
						        }
							}
							if(!isStarred) {
								common.setSentMail(detailJson, mailMessageId, myOAuth);
							}
						} else {
							// 라벨 아이디가 없으면 received mail에 그냥 저장
							common.setSentMail(detailJson, mailMessageId, myOAuth);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 임시 메일 호출
	 * 
	 * @param event
	 */
	@EventListener
	@Async
	public void getDraftMailList(LoginSuccessEvent event) {
		
		// oauth 객체
		OAuthVO myOAuth = googleOAuthServie.getUsableAccessToken(event.getMyOAuth(), event.getMyOAuth().getOauthEmpmail());
		String empMail = myOAuth.getOauthEmpmail(); // 메일 주소
		String empId = myOAuth.getEmpId(); // 사번

		StringBuffer draftQuery = new StringBuffer();

		// 모든 리스트를 매번 다 읽어올 필요는 없기 때문에 마지막 api 호출시간을 불러옴
//		String apiCallTime = dMailService.readLastApiCallTime(empMail);

//		if (apiCallTime != null) {
		// api 마지막 호출 시간을 유닉스 타임스탬프로 변환(쿼리파라미터가 유닉스 타임스탬프로 값을 받음..)
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//			LocalDateTime dateTime = LocalDateTime.parse(apiCallTime, formatter);
//			long unixTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
//			draftQuery.append(" newer_than:80d");
//		}
		draftQuery.append(" newer_than:10d");

		try {
			// google api 호출(받은 메일 리스트를 담은 response 리턴)
			HttpResponse mailInboxListResponse = common.getMailList("drafts", draftQuery, myOAuth);

			String listResponseToString = mailInboxListResponse.parseAsString();
			JsonObject jsonResponse = JsonParser.parseString(listResponseToString).getAsJsonObject();
			JsonArray drafts = jsonResponse.getAsJsonArray("drafts");

			if (drafts == null || drafts.size() == 0) {
				// 아무것도 없으면 추가적인 작업을 하지 않아도 됨!
			} else {
				// 메일이 있을 경우
				// 임시메일 여러 개가 담긴 JsonArray를 순차적으로 돌면서 메일 한 개씩 접근
				// drafts 안에 배열이 있음..apiCallTime 그 배열에 draft 한 개씩 있음
				for (JsonElement draft : drafts) {
					// draft 한 개에 접근
					// draft의 id 가져오기
					// draft 한 개 안에는 id랑 message가 있고 id는 고유 id..
					String dmailDraftId = draft.getAsJsonObject().get("id").getAsString();
//					dmailDraftId를 통해 mailMessageId에 접근,

					JsonObject message = draft.getAsJsonObject().get("message").getAsJsonObject();
					String mailMessageId = message.get("id").getAsString();

					MailDraftVO draftMail = new MailDraftVO();
					draftMail.setEmpId(empId);
					draftMail.setEmpMail(empMail);
					draftMail.setMailMessageId(mailMessageId);
					int result = dMailService.readDraftMailExist(draftMail);
					if (result == 0) {
						// google api 호출(메일 한 개의 상세정보 리턴)
						HttpResponse detailResponse = common.getMailDetail("messages", mailMessageId, myOAuth);
						String detailResponseToString = detailResponse.parseAsString();

						JsonObject detailJson = JsonParser.parseString(detailResponseToString).getAsJsonObject();

						common.setDraftMail(detailJson, mailMessageId, dmailDraftId, myOAuth);
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 삭제 메일 호출
	 * 
	 * @param event
	 */
	
	@EventListener
	@Async
	public void getDeleteMailList(LoginSuccessEvent event) {
		
		// oauth 객체
		OAuthVO myOAuth = googleOAuthServie.getUsableAccessToken(event.getMyOAuth(), event.getMyOAuth().getOauthEmpmail());
		// 받은 메일함을 불러오는 api의 쿼리파라미터(쿼리 파라미터에 따라 조건이 달라짐)
		StringBuffer deleteQuery = new StringBuffer();
		// 받은 메일함 : messages?q=in:trash
		deleteQuery.append("in:trash");

		String empMail = myOAuth.getOauthEmpmail(); // 메일 주소
		String empId = myOAuth.getEmpId(); // 사번

		// 모든 리스트를 매번 다 읽어올 필요는 없기 때문에 마지막 api 호출시간을 불러옴
//		String apiCallTime = sMailService.readLastApiCallTime(empMail);

//		if (apiCallTime != null) {
		// api 마지막 호출 시간을 유닉스 타임스탬프로 변환(쿼리파라미터가 유닉스 타임스탬프로 값을 받음..)
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//			LocalDateTime dateTime = LocalDateTime.parse(apiCallTime, formatter);
//			long unixTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
//			deleteQuery.append(" newer_than:80d");
//			sentQuery.append(String.format(" after:%s %s", unixTimestamp, "newer_than:1000d"));
//		}
		deleteQuery.append(" newer_than:10d");
		try {
			// google api 호출(받은 메일 리스트를 담은 response 리턴)
			HttpResponse mailInboxListResponse = common.getMailList("messages", deleteQuery, myOAuth);
			String listResponseToString = mailInboxListResponse.parseAsString();
			JsonObject jsonResponse = JsonParser.parseString(listResponseToString).getAsJsonObject();
			JsonArray messages = jsonResponse.getAsJsonArray("messages");

			if (messages == null || messages.size() == 0) {
				// 아무것도 없으면 추가적인 작업을 하지 않아도 됨!
			} else {
				// 메일이 있을 경우
				// 메일 여러 개가 담긴 JsonArray를 순차적으로 돌면서 메일 한 개씩 접근
				for (JsonElement message : messages) {
					// 이메일 한 개의 고유 id를 가져옴, json 객체의 id라는 이름으로 메일의 id가 담겨있음
					// 그 메일 id로 식별하기 때문에 받아놔야함
					// 이 id로 메일 한 개의 디테일한 정보를 가지고 올 수 있음
					String mailMessageId = message.getAsJsonObject().get("id").getAsString();

					MailDeleteVO delete = new MailDeleteVO();
					delete.setEmpId(empId);
					delete.setEmpMail(empMail);
					delete.setMailMessageId(mailMessageId);
					int delresult = delMailService.readDeleteMailExist(delete);

					MailImportantVO important = new MailImportantVO();
					important.setEmpId(empId);
					important.setEmpMail(empMail);
					important.setMailMessageId(mailMessageId);
					int iresult = iMailService.readImportantMailExist(important);

					if (delresult == 0 || iresult == 0) {
						HttpResponse detailResponse = common.getMailDetail("messages", mailMessageId, myOAuth);
						JsonObject detailJson = JsonParser.parseString(detailResponse.parseAsString())
								.getAsJsonObject();
						common.setDeleteMail(detailJson, mailMessageId, myOAuth);
						
					}

				}

				log.info("❤️❤️❤️❤️❤️❤️❤️❤️❤️모든 메소드 실행 끝");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
