package kr.or.ddit.schedule.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.schedule.vo.ScheduleVO;

public interface ScheduleService {
	/**
	 * @param schetypeId
	 * @param departCode
	 * @param empId
	 * @return 
	 * 일정 리스트, schetypeId 별로 불어와야해서
	 */
	public List<ScheduleVO> readScheduleList(String schetypeId, String departCode, String empId);
	/**
	 * @param scheId
	 * @return 
	 * 일정 하나를 조회
	 */
	public ScheduleVO readScheduleOne(String scheId);
	/**
	 * @param schedule
	 * @return 성공하면 OK, 실패하면 FAIL
	 */
	public ServiceResult createSchedule(ScheduleVO schedule);
	/**
	 * @param schedule
	 * @return 성공하면 OK, 실패하면 FAIL
	 */
	public ServiceResult modifySchedule(ScheduleVO schedule);
	/**
	 * @param scheId
	 * @return 성공하면 OK, 실패하면 FAIL
	 */
	public ServiceResult removeSchedule(String scheId);
	
}
