package kr.or.ddit.gmail.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.gmail.vo.MailAttachmentVO;

class MailAttachmentServiceImplTest {
	@Inject
	MailAttachmentService service;

	@Test
	void testReadAttachmentList() {
		service.readAttachmentList("193e3f3f163c0e5d");
	}

	@Test
	void testReadAttachmentExist() {
		List<MailAttachmentVO> result = service.readAttachmentList("193e3f3f163c0e5d");
		for(MailAttachmentVO attach : result) {
			assertDoesNotThrow(()->{
				service.readAttachmentExist(attach);
			});		
		}
		
	}

	@Test
	void testReadAttachmentDetail() {
		List<MailAttachmentVO> result = service.readAttachmentList("193e3f3f163c0e5d");
		for(MailAttachmentVO attach : result) {
			assertDoesNotThrow(()->{
				service.readAttachmentDetail(attach);
			});		
		}
	}

	@Test
	void testCreateMailAttachment() {
		MailAttachmentVO attach = new MailAttachmentVO();
		attach.setEmpId("test");
		attach.setEmpMail("test");
		attach.setMailattachmentHashcode("hash code");
		attach.setMailattachmentId("attachment id");
		attach.setMailattachmentMimetype("mimetype");
		attach.setMailattachmentName("파일명");
		attach.setMailmessageId("test");
	}

	@Test
	void testDeleteMailAttachment() {
		service.deleteMailAttachment("193e3f3f163c0e5d");
	}

}
