package kr.or.ddit.schedule.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.schedule.dao.ScheduleColorMapper;
import kr.or.ddit.schedule.vo.ScheduleColorVO;

@Service
public class ScheduleColorServiceImpl implements ScheduleColorService {

	@Inject
	private ScheduleColorMapper dao;
	
	@Override
	public List<ScheduleColorVO> readColorList() {
		
		return dao.selectColorList();
	}

	@Override
	public ScheduleColorVO readColorOne(String schetypeId) {
		
		return dao.selectColorOne(schetypeId);
	}

	@Override
	public ServiceResult createColor(ScheduleColorVO ScheduleColor) {
		if (dao.insertColor(ScheduleColor)> 0) {
			
			
			return ServiceResult.OK;
			
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult modifyColor(ScheduleColorVO ScheduleColor) {
		if (dao.updateColor(ScheduleColor)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeColor(String scheId) {
		if (dao.deleteColor(scheId)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

}
