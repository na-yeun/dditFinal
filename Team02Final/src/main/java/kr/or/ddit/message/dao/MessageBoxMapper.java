package kr.or.ddit.message.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.MessageBoxVO;
import kr.or.ddit.message.vo.ReceiveMessageVO;
@Mapper
public interface MessageBoxMapper {
	public int selectTotalRecord(@Param("paging")PaginationInfo<MessageBoxVO> paging,  @Param("empId")String empId);
	public List<MessageBoxVO> selectBoxMessageList(@Param("paging") PaginationInfo<MessageBoxVO> paging, @Param("empId")String empId);
	//public List<MessageBoxVO> selectBoxMessageList(String empId);
	public MessageBoxVO selectBoxMessageOne(String mboxId);
	public int insertBoxMessage(MessageBoxVO boxMessage);
	public int updateBoxMessage(MessageBoxVO boxMessage);
	public int deleteBoxMessage(String mboxId);
}
