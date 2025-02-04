package kr.or.ddit.gmail.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.model.Message;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.websocket.EchoHandler;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.MailSendType;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailSendDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GmailAPICommonServiceImpl implements GmailAPICommonService {

	private HttpRequestFactory requestFactory;

	@PostConstruct
	public void init() {
		this.requestFactory = new NetHttpTransport().createRequestFactory();
	}

	@Inject
	private MailAttachmentService attachService;

	@Override
	public byte[] getAttachment(String mailMessageId, String attachmentId, OAuthVO oAuth) {
		// Gmail API의 첨부파일 URL 생성
		String attachmentUrl = String.format(
				"https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/attachments/%s", mailMessageId,
				attachmentId);

		try {
			// HTTP 요청 생성
			HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
			HttpRequest attachmentRequest = requestFactory.buildGetRequest(new GenericUrl(attachmentUrl));
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + oAuth.getOauthAccess());
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			headers.setContentLength(0L);
			attachmentRequest.setHeaders(headers);

			attachmentRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			HttpResponse response = attachmentRequest.execute();
			String jsonResponse = response.parseAsString();
			JsonObject attachmentData = JsonParser.parseString(jsonResponse).getAsJsonObject();

			String base64UrlData = attachmentData.get("data").getAsString();

			byte[] result = Base64.getUrlDecoder().decode(base64UrlData);

			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ServiceResult deleteMail(String messageId, OAuthVO myNewOAuth) {
		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/messages/%s", messageId);
		// oauth access token
		String userAccessToken = myNewOAuth.getOauthAccess();

		GenericUrl trashUrl = new GenericUrl(url);

		HttpRequest trashRequest;
		try {
			trashRequest = requestFactory.buildDeleteRequest(trashUrl);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			// 생성한 요청에 header 셋팅하기
			trashRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			trashRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = trashRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("메일이 성공적으로 휴지통으로 이동되었습니다.");
				return ServiceResult.OK;
			} else {
				log.info("메일을 휴지통으로 이동하는 데 실패했습니다. 응답 코드: " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceResult.FAIL;
		}

	}

	@Override
	public ServiceResult trashDraftMail(String messageId, OAuthVO myNewOAuth) {
		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/drafts/%s", messageId);
		// oauth access token
		String userAccessToken = myNewOAuth.getOauthAccess();

		GenericUrl trashDraftUrl = new GenericUrl(url);

		HttpRequest trashRequest;
		try {
			trashRequest = requestFactory.buildDeleteRequest(trashDraftUrl);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			// 생성한 요청에 header 셋팅하기
			trashRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			trashRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = trashRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("임시저장메일이 성공적으로 휴지통으로 이동되었습니다.");
				return ServiceResult.OK;
			} else {
				log.info("임시저장메일을 휴지통으로 이동하는 데 실패했습니다. 응답 코드: " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult addMailLables(String messageId, OAuthVO myNewOAuth, String[] labels) {
		// 라벨 수정의 경우 post 방식으로, 데이터를 가지고 보내야함
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addLabelIds", labels);

		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		HttpContent content = new JsonHttpContent(jsonFactory, requestBody);

		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/modify", messageId);
		String userAccessToken = myNewOAuth.getOauthAccess();
		GenericUrl trashDraftUrl = new GenericUrl(url);
		HttpRequest importantRequest;
		try {
			importantRequest = requestFactory.buildPostRequest(trashDraftUrl, content);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			// 생성한 요청에 header 셋팅하기
			importantRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			importantRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = importantRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("중요메일 설정에 성공했습니다.");
				return ServiceResult.OK;
			} else {
				log.info("중요메일 설정에 실패했습니다. " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult disMailLables(String messageId, OAuthVO myNewOAuth, String[] labels) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("removeLabelIds", labels);

		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		HttpContent content = new JsonHttpContent(jsonFactory, requestBody);

		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/modify", messageId);
		String userAccessToken = myNewOAuth.getOauthAccess();
		GenericUrl trashDraftUrl = new GenericUrl(url);
		HttpRequest importantRequest;
		try {
			importantRequest = requestFactory.buildPostRequest(trashDraftUrl, content);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			// 생성한 요청에 header 셋팅하기
			importantRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			importantRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = importantRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("중요메일 설정에 성공했습니다.");
				return ServiceResult.OK;
			} else {
				log.info("중요메일 설정에 실패했습니다. " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceResult.FAIL;
		}
	}

	public ServiceResult addAndDisMailLables(String messageId, OAuthVO myNewOAuth, String[] addLabels,
			String[] disLabels) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("removeLabelIds", disLabels);
		requestBody.put("addLabelIds", addLabels);

		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		HttpContent content = new JsonHttpContent(jsonFactory, requestBody);

		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/modify", messageId);
		String userAccessToken = myNewOAuth.getOauthAccess();
		GenericUrl trashDraftUrl = new GenericUrl(url);
		HttpRequest importantRequest;
		try {
			importantRequest = requestFactory.buildPostRequest(trashDraftUrl, content);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			// 생성한 요청에 header 셋팅하기
			importantRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			importantRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = importantRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("중요메일 설정에 성공했습니다.");
				return ServiceResult.OK;
			} else {
				log.info("중요메일 설정에 실패했습니다. " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult trashMail(String messageId, OAuthVO myNewOAuth, String type) {
		String url = String.format("https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/%s", messageId, type);
		// oauth access token
		String userAccessToken = myNewOAuth.getOauthAccess();

		GenericUrl trashUrl = new GenericUrl(url);

		HttpRequest trashRequest;
		try {
			HttpContent content = new EmptyContent();
			trashRequest = requestFactory.buildPostRequest(trashUrl, content);
			// 요청의 헤더 설정(oauth를 통한 api 호출에는 무조건 헤더설정이 필요함!!!!!!, 헤더 객체 자체는 init에서)
			// Authorization 헤더 추가
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			// google api의 모든 응답은 json 형태로 돌아오기 때문에 accept 지정해주는 것이 좋음
			headers.setAccept("application/json"); // JSON 형식 요청
			headers.setContentLength(0L);
			// 생성한 요청에 header 셋팅하기
			trashRequest.setHeaders(headers);

			/*
			 * 요청 실행 + 응답 파싱(모든 응답은 json 형태로 넘어옴) { "messages": [ { "id": "메일 한 개의 id",
			 * "threadId": "그 메일의 스레드 id"(우리가 아는 그 스레드랑 개념이 다름 우린 안 쓸거임) } , ... ] }
			 */
			trashRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse response = trashRequest.execute();

			if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
				log.info("메일이 성공적으로 삭제(복구)되었습니다.");
				return ServiceResult.OK;
			} else {
				log.info("메일이 삭제(복구)에 실패했습니다. 응답 코드: " + response.getStatusCode());
				return ServiceResult.FAIL;
			}
		} catch (IOException e) {
			return ServiceResult.FAIL;
		}

	}

	@Override
	public Message mailMessage(MailSendDTO mail, String forwardMessageId, OAuthVO myNewOAuth)
			throws AddressException, MessagingException, IOException {
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage emailMessage = new MimeMessage(session);
		// 보내는 사람
		emailMessage.setFrom(new InternetAddress(mail.getMailFrom()));
		// 받는 사람 추가
		List<String> toList = mail.getMailTo();
		if (toList != null) {
			for (String to : toList) {
				emailMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			}
		}
		// 참조 추가
		List<String> ccList = mail.getMailCc();
		if (ccList != null) {
			for (String cc : ccList) {
				emailMessage.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(cc));
			}
		}

		// 숨은참조 추가
		List<String> bccList = mail.getMailBcc();
		if (bccList != null) {
			for (String bcc : bccList) {
				emailMessage.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(bcc));
			}
		}
		emailMessage.setSubject(mail.getMailSubject());

		// 본문 추가
		MimeMultipart multipart = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(mail.getMailContent(), "text/html; charset=utf-8");
		multipart.addBodyPart(textPart);

		// 첨부파일 추가
		List<MultipartFile> mailFiles = mail.getMailFiles();
		if (mailFiles != null && !mailFiles.isEmpty()) {
			for (MultipartFile file : mailFiles) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
				attachmentPart.setFileName(MimeUtility.encodeText(file.getOriginalFilename()));
				attachmentPart.setDataHandler(
						new DataHandler(new ByteArrayDataSource(file.getBytes(), file.getContentType())));
				attachmentPart.setHeader("Content-Disposition",
						"attachment; filename=\"" + file.getOriginalFilename() + "\"");
				multipart.addBodyPart(attachmentPart);
			}
		}

		if (forwardMessageId != null) {
			// 전달메일일 때 실행
			// 데이터베이스에서 해당 messageId로 첨부파일 리스트 가지고 오기
			List<MailAttachmentVO> attachList = attachService.readAttachmentList(forwardMessageId);
			if (attachList != null) {
				// 리스트가 null이면 첨부파일 없으니 스킵
				// 리스트가 null이 아니면 첨부파일이 있으니 리스트 forEach문 돌려서 attachment vo에 담아서 넘기기
				for (MailAttachmentVO attach : attachList) {
					byte[] attachmentData = getAttachment(attach.getMailmessageId()
														, attach.getMailattachmentId()
														, myNewOAuth);
					MimeBodyPart attachmentPart = new MimeBodyPart();
					attachmentPart.setFileName(MimeUtility.encodeText(attach.getMailattachmentName()));
					attachmentPart.setDataHandler(new DataHandler(
							new ByteArrayDataSource(attachmentData, attach.getMailattachmentMimetype())));
					attachmentPart.setHeader("Content-Disposition",
							"attachment; filename=\"" + attach.getMailattachmentName() + "\"");
					multipart.addBodyPart(attachmentPart);
				}
			}

		}

		emailMessage.setContent(multipart);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		emailMessage.writeTo(buffer);
		String encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray());

		Message message = new Message();
		message.setRaw(encodedEmail);
		return message;
	}

	@Override
	public HttpResponse sendMail(MailSendType type, OAuthVO myNewOAuth, Message message) {
		String regex = "https://gmail.googleapis.com/gmail/v1/users/me/%s";
		String url = String.format(regex, type.getValue());
		String userAccessToken = myNewOAuth.getOauthAccess();

		try {
			GenericUrl sendTextUrl = new GenericUrl(url);
			// 본문
			HttpContent content = null;

			if (type == MailSendType.SEND) {
				content = new ByteArrayContent("application/json",
						String.format("{\"raw\":\"%s\"}", message.getRaw()).getBytes(StandardCharsets.UTF_8));
			} else if (type == MailSendType.DRAFT) {
				String jsonPayload = String.format("{\"message\":{\"raw\":\"%s\"}}", message.getRaw());
				content = new ByteArrayContent("application/json", jsonPayload.getBytes(StandardCharsets.UTF_8));
			}

			// url과 본문 사용해서 post request 만들기
			HttpRequest sendRequest = requestFactory.buildPostRequest(sendTextUrl, content);
			// header 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setAuthorization("Bearer " + userAccessToken);
			headers.setAccept("application/json");
			//
			headers.setContentType("application/json; charset=UTF-8");
			// request에 headers 설정
			sendRequest.setHeaders(headers);
			sendRequest.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			// 실행
			HttpResponse sendResponse = sendRequest.execute();
			return sendResponse;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}
