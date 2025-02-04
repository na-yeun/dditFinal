package kr.or.ddit.message.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.message.vo.MessageMappingVO;
@Mapper
public interface MessageMappingMapper {
	public List<MessageMappingVO> selectMappingMessageList();
	public MessageMappingVO selectMappingMessageOne(String smesId);
	public int insertMappingMessage(MessageMappingVO mapping);
	public int updateMappingMessage(MessageMappingVO mapping);
	public int deleteMappingMessage(long mapId);
}
 