package kr.or.ddit.message.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.SendMessageVO;
@Mapper
public interface SendMessageMapper {
	public int selectTotalRecord(@Param("paging")PaginationInfo<SendMessageVO> paging,  @Param("empId")String empId);
	//public List<SendMessageVO> selectSendMessageList(String empId);
	public List<SendMessageVO> selectSendMessageList(@Param("paging") PaginationInfo<SendMessageVO> paging,  @Param("empId")String empId);
	public List<SendMessageVO> selectSendMessageOne(String smesId);
	public int insertSendMessage(SendMessageVO sendMessage);
	public int updateSendMessage(SendMessageVO sendMessage);
	public int deleteSendMessage(String smesId);
	
}
