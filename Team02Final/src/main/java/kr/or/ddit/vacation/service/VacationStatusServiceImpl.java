package kr.or.ddit.vacation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import kr.or.ddit.vacation.vo.VacationTypeVO;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.survey.vo.SurveyResultVO;
import kr.or.ddit.vacation.dao.VacationStatusMapper;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VacationStatusServiceImpl implements VacationStatusService {
	@Inject
	public VacationStatusMapper vacationStatusmapper;
	
	
	@Transactional
	@Override
	public ServiceResult createVacationStatus(VacationDTO vacation, Set<String> failEmps) {
		try {
			List<String> empList = vacation.getVacationTo();
			for(String emp : empList) {
				ServiceResult result = readVacationExist(emp);
				if(result==ServiceResult.PKDUPLICATED) {
					// 이미 올해의 휴가가 부여되어있기 때문에 insert 작업 하지 않음
					failEmps.add(emp);
				} else if(result==ServiceResult.OK) {
					// 올해의 휴가가 부여되어있지 않기 때문에 insert 작업이 가능함
					VacationStatusVO vs = new VacationStatusVO();
					vs.setEmpId(emp);
					vs.setVstaAllcount(vacation.getVacationCnt());
					
					vacationStatusmapper.insertVacationStatus(vs);
				}
			}
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
		return ServiceResult.OK;
	}
	
	/**
     * 해당 조건으로 데이터가 있는지 없는지 확인
     * @param vacation
     * @return 이미 데이터가 있으면 PK / 데이터 없으면 OK
     */
	public ServiceResult readVacationExist(String empId) {
		int vacationCnt = vacationStatusmapper.selectVacationExist(empId);
		if(vacationCnt>0) {
			// 이미 올해의 데이터가 있음
			return ServiceResult.PKDUPLICATED;
		} else {
			// 올해의 데이터가 없음
			return ServiceResult.OK;
		}
	}
	
	
	@Override
	public List<VacationStatusVO> readVacationStatusList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VacationStatusVO> readVacationStatusThisYearList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VacationStatusVO> readVacationStatus(String empId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VacationStatusVO readVacationStatusThisYear(String empId) {
		return vacationStatusmapper.selectVacationStatusThisYear(empId);
	}

	@Override
	public ServiceResult modifyVacationStatus(VacationDTO vacation, Set<String> failEmps) {
		try {
			List<String> empList = vacation.getVacationTo();
			for(String emp : empList) {
				ServiceResult result = readVacationExist(emp);
				if(result==ServiceResult.PKDUPLICATED) {
					// 이미 올해의 휴가가 부여되어있기 때문에 update 작업이 가능함
					VacationStatusVO vs = new VacationStatusVO();
					vs.setEmpId(emp);
					vs.setVstaAppend(vacation.getVacationCnt());
					
					vacationStatusmapper.updateVacationStatus(vs);
				} else if(result==ServiceResult.OK) {
					// 올해의 휴가가 부여되어있지 않기 때문에 update 작업이 불가능
					failEmps.add(emp);
				}
			}
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
		return ServiceResult.OK;
	}

	@Override
	public ServiceResult deleteVacationStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> readVacationTypeList() {
		return vacationStatusmapper.selectVacationTypeList();
	}

	
	

	

}
