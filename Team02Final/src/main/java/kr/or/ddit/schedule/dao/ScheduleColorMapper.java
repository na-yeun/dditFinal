package kr.or.ddit.schedule.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.schedule.vo.ScheduleColorVO;

@Mapper
public interface ScheduleColorMapper {
	public List<ScheduleColorVO> selectColorList();
	public ScheduleColorVO selectColorOne(String schetypeId);
	public int insertColor(ScheduleColorVO ScheduleColor);
	public int updateColor(ScheduleColorVO ScheduleColor);
	public int deleteColor(String scheId);
}
