package kr.or.ddit.schedule.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.schedule.dao.ScheduleColorMapper;
import kr.or.ddit.schedule.dao.ScheduleMapper;
import kr.or.ddit.schedule.vo.ScheduleColorVO;
import kr.or.ddit.schedule.vo.ScheduleVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Inject
	private ScheduleMapper dao;
	
	@Inject
	private ScheduleColorMapper colorDao;
	
	@Override
	public List<ScheduleVO> readScheduleList(String schetypeId, String departCode, String empId) {
		
		log.info("empId>>>>>>>>{}",empId);
		log.info("schetypeId>>>>>>>>{}",schetypeId);
		log.info("departCode>>>>>>>>{}",departCode);
		// TODO Auto-generated method stub
		return dao.selectScheduleList(schetypeId, departCode, empId);
	}

	@Override
	public ScheduleVO readScheduleOne(String scheId) {
		// TODO Auto-generated method stub
		return dao.selectScheduleOne(scheId);
	}

	@Transactional
	@Override
	public ServiceResult createSchedule(ScheduleVO schedule) {
		try {
			String type = schedule.getSchetypeId();
			
			if(type.equals("3")) {
				// 타입이 3일 때(개인 일정일 때)
				// 개인일정일 때는 일정도 insert하고 색깔도 insert 해야함
				
				dao.insertSchedule(schedule);
				String empId = schedule.getEmpId();
				String scheId = schedule.getScheId();
				
				ScheduleColorVO scheduleColors = schedule.getScheduleColor();
				scheduleColors.setSchetypeId(type);
				scheduleColors.setEmpId(empId);
				scheduleColors.setScheId(scheId);
				
				// 일정 insert
				// 일정에 대한 색 insert
				colorDao.insertColor(scheduleColors);
				
			} else {
				// 타입이 1이나 2일 때(전체 일정 혹은 부서일정일 때)
				// 일정만 insert 하고 색깔은 insert 하지 않음
				dao.insertSchedule(schedule);
			}
			return ServiceResult.OK;
		} catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    }
		
	}
	
	@Transactional
	@Override
	public ServiceResult modifySchedule(ScheduleVO schedule) {
		if (dao.updateSchedule(schedule)> 0) {
			String schetypeId = schedule.getSchetypeId();
			ScheduleColorVO scheduleColors = schedule.getScheduleColor();
			 // 색상 정보가 있는 경우
	        if (scheduleColors != null) {
	            scheduleColors.setSchetypeId(schetypeId); // 스케줄 유형 설정
	            
	            // 2. 색상 업데이트
	            if ("1".equals(schetypeId) || "2".equals(schetypeId)) {
	                // 회사 또는 부서 일정의 색상 업데이트
	                int updatedRows = colorDao.updateColor(scheduleColors);
	                if (updatedRows <= 0) {
	                    throw new RuntimeException("Failed to update company/department schedule colors");
	                }
	            } else if ("3".equals(schetypeId)) {
	                // 개인 일정의 색상 업데이트
	                scheduleColors.setScheId(schedule.getScheId()); // SCHE_ID 설정
	                scheduleColors.setEmpId(schedule.getEmpId());   // EMP_ID 설정
	                
	                int updatedRows = colorDao.updateColor(scheduleColors);
	                if (updatedRows <= 0) {
	                    throw new RuntimeException("Failed to update personal schedule color");
	                }
	            }
	        }
			//colorDao.updateColor(String schetypeId, String scheBcolor, String scheFcolor);
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}
	@Transactional
	@Override
	public ServiceResult removeSchedule(String scheId) {
		try {
			ScheduleVO sche = dao.selectScheduleOne(scheId);
			String scheTypeId = sche.getSchetypeId();
			if (scheTypeId.equals("3")) {
				// 1. SCHEDULE_COLOR 테이블에서 관련 색상 삭제
				colorDao.deleteColor(scheId);
			}

			// 2. SCHEDULE 테이블에서 일정 삭제
			dao.deleteSchedule(scheId);
			return ServiceResult.OK;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
