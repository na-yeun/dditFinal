package kr.or.ddit.scheduletype.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.scheduletype.vo.ScheduleTypeVO;

@Mapper
public interface ScheduleTypeMapper {
	public List<ScheduleTypeVO> selectScheduleTypeList();
	public ScheduleTypeVO selectScheduleTypeOne(String schetypeId);
}
