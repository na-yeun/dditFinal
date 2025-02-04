package kr.or.ddit.gmail.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Base64;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.gmail.vo.MailDraftVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSentVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Gmail API를 통해 메일의 list를 가지고 오는 메소드들
 * 
 * @author kimny
 *
 */
@Slf4j
@Component
public class GmailListCommonService {

	private HttpRequestFactory requestFactory;
	private String apiReCalltime;

	@PostConstruct
	public void init() {
		this.requestFactory = new NetHttpTransport().createRequestFactory();
	}

	@Inject
	private MailAttachmentService mailAttachmentService;
	@Inject
	private MailDraftService dMailService;
	@Inject
	private MailReceivedService rMailService;
	@Inject
	private MailSentService sMailService;
	@Inject
	private MailDeleteService delMailService;
	@Inject
	private MailImportantService iMailService;
	@Inject
	private GmailAPICommonService crudCommonService;

	/**
	 * google api에 접근하여 종류에 따른 메일 리스트를 가지고 오는 메소드 메일의 종류
	 * (임시보관함) : me/drafts
	 * (받은 메일함) : me/messages?q=label:inbox
	 * (보낸 메일함) : me/messages?q=label:sent
	 * (메일 휴지통) : me/messages?q=label:trash
	 * (중요 메일함) : me/messages?q=label:지정한 라벨 이름(gmail의 중요메일은 STARRED를 씀)
	 * 
	 * @param q       요청하려는 list에 따른 파라미터 종류
	 * @param myOAuth
	 * @return
	 * @throws IOException
	 */
	public HttpResponse getMailList(String type, StringBuffer q, OAuthVO myOAuth) throws IOException {

		// oauth access token
		String userAccessToken = myOAuth.getOauthAccess();
		// gmail api 호출 url
		String gmailApiListUrl = "https://gmail.googleapis.com/gmail/v1/users/me/" + type;
		GenericUrl listUrl = new GenericUrl(gmailApiListUrl);
		if (q != null) {
			listUrl.put("q", q);
		}
		listUrl.put("maxResults", 1);

		// 요청 생성
		HttpRequest listRequest = requestFactory.buildGetRequest(listUrl);

		// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요)
		HttpHeaders headers = new HttpHeaders();
		headers.setAuthorization("Bearer " + userAccessToken);
		headers.setAccept("application/json");

		// 생성한 요청에 header 셋팅하기
		listRequest.setHeaders(headers);

		/*
		 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴)
		 * { "messages": [ 
		 * 		{ "id": "메일 한 개의 id",
		 * 		  "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } ,
		 * 		   ... ]
		 * }
		 */
		listRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

		// api 호출 시간 기록하기
		LocalDateTime now = LocalDateTime.now();
		apiReCalltime = now.toString();
		// request.execute() 실행, string으로 반환해서 전달
		return listRequest.execute();
	}

	/**
	 * gmail api에 접근하여 메일 id를 통해 메일 하나의 정보를 가지고 오는 메소드
	 * 
	 * @param rmailMessageid
	 * @return
	 * @throws IOException
	 */
	public HttpResponse getMailDetail(String type, String rmailMessageid, OAuthVO myOAuth) throws IOException {
		// 메일 한 개의 정보를 가져오는 api url
		// https://gmail.googleapis.com/gmail/v1/users/me/%s
		// GET https://gmail.googleapis.com/gmail/v1/users/me/messages/{id}
		// messages/{id} 이만큼을 만들어줘야함! (mailIdUrl)
		
		String gmailApiDetailUrl = "https://gmail.googleapis.com/gmail/v1/users/me/" + type + "/" + rmailMessageid;
		GenericUrl detailUrl = new GenericUrl(gmailApiDetailUrl);
		HttpRequest detailMessageRequest = requestFactory.buildGetRequest(detailUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAuthorization("Bearer " + myOAuth.getOauthAccess());
		headers.setAccept("application/json"); // JSON 형식 요청

		detailMessageRequest.setHeaders(headers);

		detailMessageRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));
		HttpResponse detailMessageResponse = detailMessageRequest.execute();
		log.info("over here : {}",detailMessageResponse.toString());
		return detailMessageResponse;
	}

	// 본문 내용 꺼내고 첨부파일은 vo를 통해 data base 저장
	// 본문 내용(첨부파일 없이) 꺼내오기
	public String getContent(JsonObject payload, String mailMessageId, OAuthVO myOAuth) {
		String returnResult = "";
		try {
			// 직접 본문 데이터가 있는 경우
			if (payload.has("body") && payload.getAsJsonObject("body").has("data")) {
				String encodedBody = payload.getAsJsonObject("body").get("data").getAsString();
				returnResult = new String(Base64.getUrlDecoder().decode(encodedBody));
			}
			
			// parts 배열에 본문이 있는 경우
			JsonArray parts = payload.getAsJsonArray("parts");
			if (parts != null) {
				
				for (JsonElement partElement : parts) {
					
					JsonObject part = partElement.getAsJsonObject();
					String mimeType = part.get("mimeType").getAsString();
					// text/plain 또는 text/html 본문 처리
					if ("text/plain".equals(mimeType) || "text/html".equals(mimeType)) {
						String encodedBody = part.getAsJsonObject("body").get("data").getAsString();
						returnResult = new String(Base64.getUrlDecoder().decode(encodedBody));
					}

					// 첨부파일 확인
					if (part.has("filename") && StringUtils.isNotBlank(part.get("filename").getAsString())) {
						// 첨부파일 데이터 추출
						String attachmentId = null;
						if (part.has("body") && part.getAsJsonObject("body").has("attachmentId")) {
							attachmentId = part.getAsJsonObject("body").get("attachmentId").getAsString();
						}

						String attachmentName = part.get("filename").getAsString();
						String attachmentMimeType = mimeType;

						byte[] fileData = crudCommonService.getAttachment(mailMessageId, attachmentId, myOAuth);

						if (fileData != null) {
							// 해시코드 생성
							String fileHash = generateFileHash(fileData);

							MailAttachmentVO mailAttachment = new MailAttachmentVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId,
									attachmentId, attachmentName, attachmentMimeType, fileHash);
							int result = mailAttachmentService.readAttachmentExist(mailAttachment);
							if (result == 0) {
								// 메일을 data base에 insert
								mailAttachmentService.createMailAttachment(mailAttachment);
							}
						}

						// 첨부파일의 추가 데이터가 body의 attachmentId로 연결되어 있는 경우
					} else if (part.has("body") && part.getAsJsonObject("body").has("attachmentId")) {
						// 첨부파일 데이터 추출
						String attachmentId = part.getAsJsonObject("body").get("attachmentId").getAsString();
						String attachmentName = part.has("filename") ? part.get("filename").getAsString() : "unknown";
						String attachmentMimeType = mimeType;

						byte[] fileData = crudCommonService.getAttachment(mailMessageId, attachmentId, myOAuth);

						if (fileData != null) {
							String fileHash = generateFileHash(fileData);

							MailAttachmentVO mailAttachment = new MailAttachmentVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId,
									attachmentId, attachmentName, attachmentMimeType, fileHash);
							int result = mailAttachmentService.readAttachmentExist(mailAttachment);
							if (result == 0) {
								// 메일을 data base에 insert
								mailAttachmentService.createMailAttachment(mailAttachment);
							}
						}
					}
				}
			}
			return returnResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "내용 없음";
	}


	
	public String convertToKST(String date) {
	    log.info("date : {}", date);

	    // 출력 포맷 정의
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH);

	    // 포맷 정의
	    DateTimeFormatter zzzFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
	    DateTimeFormatter xFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss X", Locale.ENGLISH);
	    DateTimeFormatter customFormatter = new DateTimeFormatterBuilder()
	            .parseCaseInsensitive()
	            .appendPattern("EEE, dd MMM yyyy HH:mm:ss ")
	            .optionalStart()
	            .appendZoneText(TextStyle.SHORT)
	            .optionalEnd()
	            .toFormatter(Locale.ENGLISH);

	    ZonedDateTime zonedDateTime = null;

	    try {
	        // (KST)와 같은 괄호 속 시간대 정보 제거
	        date = date.replaceAll("\\s*\\(.*?\\)", "").trim();

	        // 입력값 분석
	        if (date.matches(".*GMT.*")) {
	            log.info("Using zzzFormatter for GMT");
	            zonedDateTime = ZonedDateTime.parse(date.trim(), zzzFormatter);
	        } else if (date.matches(".*[+-]\\d{4}.*")) {
	            log.info("Using xFormatter for offset timezone");
	            zonedDateTime = ZonedDateTime.parse(date.trim(), xFormatter);
	        } else {
	            log.info("Using customFormatter for other cases");
	            zonedDateTime = ZonedDateTime.parse(date.trim(), customFormatter);
	        }

	        // 한국 시간대로 변환
	        zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
	    } catch (Exception e) {
	        log.error("Failed to parse date: {}", date, e);
	        return date; // 실패 시 원본 반환
	    }

	    return zonedDateTime.format(outputFormatter);
	}

	
	

	public String generateFileHash(byte[] fileData) {
		try {
			// SHA-256 해시 함수 생성
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(fileData);

			// 해시 바이트 배열을 Base64로 변환
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 Algorithm not found", e);
		}
	}



	// 받은 메일
	public void setReceivedMail(JsonObject detailJson, String mailMessageId, OAuthVO myOAuth) {
		JsonObject payload = detailJson.getAsJsonObject("payload");
		JsonArray headersArray = payload.getAsJsonArray("headers");

		String rmailFrom = null; // 메일 보낸 사람
		String rmailCc = null; // 메일의 참조
		String rmailReplyto = null; // 회신메일주소
		String rmailDate = null; // 메일수신날짜
		String rmailSubject = null; // 메일제목
		String rmailContentType = null; // 메일 타입
		String rmailContent = null; // 메일 내용

		for (JsonElement headerElement : headersArray) {

			JsonObject header = headerElement.getAsJsonObject();
			String name = header.get("name").getAsString();

			if ("From".equalsIgnoreCase(name)) {
				rmailFrom = header.get("value").getAsString();
			} else if ("Cc".equalsIgnoreCase(name)) {
				rmailCc = header.get("value").getAsString();
			} else if ("Reply-To".equalsIgnoreCase(name)) {
				rmailReplyto = header.get("value").getAsString();
			} else if ("Date".equalsIgnoreCase(name)) {
				rmailDate = convertToKST(header.get("value").getAsString());
			} else if ("Subject".equalsIgnoreCase(name)) {
				rmailSubject = header.get("value").getAsString();
			} else if ("Content-Type".equalsIgnoreCase(name)) {
				rmailContentType = header.get("value").getAsString();
			}

		}
		rmailContent = getContent(payload, mailMessageId, myOAuth);

		MailReceivedVO receivedMail = new MailReceivedVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId, rmailFrom, rmailCc,
				rmailReplyto, rmailDate, rmailSubject, rmailContent, rmailContentType, apiReCalltime);

		int result = rMailService.readReceivedMailExist(receivedMail);
		if (result == 0) {
			rMailService.createReceivedMail(receivedMail);
		}

	}

	// 보낸 메일
	public void setSentMail(JsonObject detailJson, String mailMessageId, OAuthVO myOAuth) {
		JsonObject payload = detailJson.getAsJsonObject("payload");
		JsonArray headersArray = payload.getAsJsonArray("headers");

		String smailBcc = null;
		String smailCc = null;
		String smailContent = null;
		String smailContentType = null;
		String smailDate = null;
		String smailReplyto = null;
		String smailSubject = null;
		String smailTo = null;

		for (JsonElement headerElement : headersArray) {

			JsonObject header = headerElement.getAsJsonObject();
			String name = header.get("name").getAsString();

			if ("Bcc".equalsIgnoreCase(name)) {
				smailBcc = header.get("value").getAsString();
			} else if ("Cc".equalsIgnoreCase(name)) {
				smailCc = header.get("value").getAsString();
			} else if ("Content-Type".equalsIgnoreCase(name)) {
				smailContentType = header.get("value").getAsString();
			} else if ("Date".equalsIgnoreCase(name)) {
				smailDate = convertToKST(header.get("value").getAsString());
			} else if ("Reply-To".equalsIgnoreCase(name)) {
				smailReplyto = header.get("value").getAsString();
			} else if ("Subject".equalsIgnoreCase(name)) {
				smailSubject = header.get("value").getAsString();
			} else if ("To".equalsIgnoreCase(name)) {
				smailTo = header.get("value").getAsString();
			}

		}

		smailContent = getContent(payload, mailMessageId, myOAuth);

		MailSentVO sentMail = new MailSentVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId, smailTo, smailSubject, smailDate,
				smailContentType, smailContent, smailCc, smailBcc, smailReplyto, apiReCalltime);

		int result = sMailService.readSentMailExist(sentMail);
		if (result == 0) {
			sMailService.createSentMail(sentMail);
		}

	}

	// 중요메일
	public void setImportantMail(JsonObject detailJson, String mailMessageId, OAuthVO myOAuth) {
		JsonObject payload = detailJson.getAsJsonObject("payload");
		JsonArray headersArray = payload.getAsJsonArray("headers");

		String imailTo = null; // 메일 받은 사람
		String imailFrom = null; // 메일 보낸 사람
		String imailCc = null; // 메일의 참조
		String imailBcc = null; // 메일의 숨은 참조
		String imailReplyto = null; // 회신메일주소
		String imailDate = null; // 메일수신날짜
		String imailSubject = null; // 메일제목
		String imailContentType = null; // 메일 타입
		String imailContent = null; // 메일 내용

		for (JsonElement headerElement : headersArray) {

			JsonObject header = headerElement.getAsJsonObject();
			String name = header.get("name").getAsString();

			if ("From".equalsIgnoreCase(name)) {
				imailFrom = header.get("value").getAsString();
			} else if ("Cc".equalsIgnoreCase(name)) {
				imailCc = header.get("value").getAsString();
			} else if ("Reply-To".equalsIgnoreCase(name)) {
				imailReplyto = header.get("value").getAsString();
			} else if ("Date".equalsIgnoreCase(name)) {
				imailDate = convertToKST(header.get("value").getAsString());
			} else if ("Subject".equalsIgnoreCase(name)) {
				imailSubject = header.get("value").getAsString();
			} else if ("Content-Type".equalsIgnoreCase(name)) {
				imailContentType = header.get("value").getAsString();
			} else if ("To".equalsIgnoreCase(name)) {
				imailTo = header.get("value").getAsString();
			} else if ("Bcc".equalsIgnoreCase(name)) {
				imailBcc = header.get("value").getAsString();
			}

		}
		imailContent = getContent(payload, mailMessageId, myOAuth);

		MailImportantVO importantMail = new MailImportantVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId, imailTo, imailFrom,
				imailSubject, imailDate, imailContentType, imailContent, imailCc, imailBcc, imailReplyto, apiReCalltime);

		int result = iMailService.readImportantMailExist(importantMail);
		if (result == 0) {
			iMailService.createImportantMail(importantMail);
		}

	}

	// 휴지통 메일
	public void setDeleteMail(JsonObject detailJson, String mailMessageId, OAuthVO myOAuth) {

		JsonObject payload = detailJson.getAsJsonObject("payload");
		JsonArray headersArray = payload.getAsJsonArray("headers");

		String delmailTo = null; // (발신 메일의 경우)메일 받는 사람
		String delmailFrom = null; // (수신 메일의 경우)메일 보낸 사람
		String delmailCc = null; // 메일의 참조
		String delmailBcc = null; // 숨은참조
		String delmailReplyto = null; // 회신메일주소
		String delmailDate = null; // 메일수신날짜
		String delmailSubject = null; // 메일제목
		String delmailContentType = null; // 메일 타입
		String delmailContent = null; // 메일 내용

		for (JsonElement headerElement : headersArray) {

			JsonObject header = headerElement.getAsJsonObject();
			String name = header.get("name").getAsString();

			if ("To".equalsIgnoreCase(name)) {
				delmailTo = header.get("value").getAsString();
			} else if ("From".equalsIgnoreCase(name)) {
				delmailFrom = header.get("value").getAsString();
			} else if ("Cc".equalsIgnoreCase(name)) {
				delmailCc = header.get("value").getAsString();
			} else if ("Bcc".equalsIgnoreCase(name)) {
				delmailBcc = header.get("value").getAsString();
			} else if ("Reply-To".equalsIgnoreCase(name)) {
				delmailReplyto = header.get("value").getAsString();
			} else if ("Date".equalsIgnoreCase(name)) {
				delmailDate = convertToKST(header.get("value").getAsString());
			} else if ("Subject".equalsIgnoreCase(name)) {
				delmailSubject = header.get("value").getAsString();
			} else if ("Content-Type".equalsIgnoreCase(name)) {
				delmailContentType = header.get("value").getAsString();
			}
		}

		delmailContent = getContent(payload, mailMessageId, myOAuth);

		MailDeleteVO deleteMail = new MailDeleteVO(myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId, delmailTo, delmailFrom,
				delmailSubject, delmailDate, delmailContentType, delmailContent, delmailCc, delmailBcc, delmailReplyto,
				apiReCalltime

		);

		int result = delMailService.readDeleteMailExist(deleteMail);
		if (result == 0) {
			delMailService.createDeleteMail(deleteMail);
		}
	}
	
	// 임시저장메일
	public void setDraftMail(JsonObject detailJson, String mailMessageId, String dmailDraftId, OAuthVO myOAuth) {
		JsonObject payload = detailJson.getAsJsonObject("payload");
		JsonArray headersArray = payload.getAsJsonArray("headers");
		
		String dmailTo = null; // 메일 받는 사람
		String dmailCc = null; // 메일의 참조
		String dmailBcc = null; // 숨은참조
		String dmailReplyto = null; // 회신메일주소
		String dmailDate = null; // 메일수신날짜
		String dmailSubject = null; // 메일제목
		String dmailContentType = null; // 메일 타입
		String dmailContent = null; // 메일 내용
		
		for (JsonElement headerElement : headersArray) {

			JsonObject header = headerElement.getAsJsonObject();
			String name = header.get("name").getAsString();

			if ("To".equalsIgnoreCase(name)) {
				dmailTo = header.get("value").getAsString();
			} else if ("Cc".equalsIgnoreCase(name)) {
				dmailCc = header.get("value").getAsString();
			} else if ("Reply-To".equalsIgnoreCase(name)) {
				dmailReplyto = header.get("value").getAsString();
			} else if ("Date".equalsIgnoreCase(name)) {
				dmailDate = convertToKST(header.get("value").getAsString());
			} else if ("Subject".equalsIgnoreCase(name)) {
				dmailSubject = header.get("value").getAsString();
			} else if ("Content-Type".equalsIgnoreCase(name)) {
				dmailContentType = header.get("value").getAsString();
			} else if ("Bcc".equalsIgnoreCase(name)) {
				dmailBcc = header.get("value").getAsString();
			}
		}
		
		dmailContent = getContent(payload, mailMessageId, myOAuth);


		MailDraftVO dMail = new MailDraftVO(
				myOAuth.getEmpId(), myOAuth.getOauthEmpmail(), mailMessageId, dmailDraftId, dmailTo,
				dmailSubject, dmailDate, dmailContentType, dmailContent,
				dmailCc, dmailBcc, dmailReplyto, apiReCalltime
		);
		
		int result = dMailService.readDraftMailExist(dMail);
		if (result == 0) {
			dMailService.createDraftMail(dMail);
		}
	}

}
