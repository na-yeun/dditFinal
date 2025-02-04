package kr.or.ddit.schedule.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.schedule.vo.ScheduleVO;
@Mapper
public interface ScheduleMapper {
	public List<ScheduleVO> selectScheduleList( 
			@Param("schetypeId") String schetypeId,
	        @Param("departCode") String departCode,
	        @Param("empId") String empId);
	public ScheduleVO selectScheduleOne(@Param("scheId") String scheId);
	public int insertSchedule(ScheduleVO schedule);
	public int updateSchedule(ScheduleVO schedule);
	public int deleteSchedule(String scheId);
	
}
