package kr.or.ddit.schedule.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.schedule.vo.ScheduleColorVO;

public interface ScheduleColorService {
	public List<ScheduleColorVO> readColorList();
	public ScheduleColorVO readColorOne(String schetypeId);
	public ServiceResult createColor(ScheduleColorVO ScheduleColor);
	public ServiceResult modifyColor(ScheduleColorVO ScheduleColor);
	public ServiceResult removeColor(String scheId);
}
