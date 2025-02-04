package kr.or.ddit.gmail.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.regex.qual.PartialRegex;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailAttachmentVO;

@Mapper
public interface MailAttachmentMapper {
	public List<MailAttachmentVO> selectAttachmentList(@Param("mailMessageId") String mailMessageId);
	public int selectAttachmentExist(MailAttachmentVO mailAttachment);
	public MailAttachmentVO selectAttachmentDetail(MailAttachmentVO mailAttachment);
	public int insertMailAttachment(MailAttachmentVO mailAttachment);
	public int deleteMailAttachment(@Param("mailmessageId")String mailmessageId);

}
