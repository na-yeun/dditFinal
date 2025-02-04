package kr.or.ddit.message.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.ReceiveMessageVO;
@Mapper
public interface ReceiveMessageMapper {
	public int selectTotalRecord(@Param("paging")PaginationInfo<ReceiveMessageVO> paging,  @Param("empId")String empId);
	public List<ReceiveMessageVO> selectReceiveMessageList(@Param("paging") PaginationInfo<ReceiveMessageVO> paging, @Param("empId")String empId);
	public ReceiveMessageVO selectReceiveMessageOne(String rmesId);
	public int insertReceiveMessage(ReceiveMessageVO resMessage);
	public int updateReceiveMessage(String rmesId);
	public int deleteReceiveMessage(String rmesId);
}
