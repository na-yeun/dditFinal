package kr.or.ddit.attendance.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.attendance.dao.AttendanceHistoryMapper;
import kr.or.ddit.attendance.vo.AttendTbVO;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;

@Service
public class AttendanceHistoryServiceImpl implements AttendanceHistoryService {
	@Inject
	private AttendanceHistoryMapper mapper;

	@Override
	public ServiceResult createAttendanceHistory(AttendanceHistoryVO attendance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResult modifyAttendanceHistory(AttendanceHistoryVO attendance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResult deleteAttendanceHistory(String atthisId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttendanceHistoryVO> readAttendanceHistoryList(PaginationInfo paging) {
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		return mapper.selectAttendanceHistoryList(paging);
	}
	
	@Override
	public List<AttendanceHistoryVO> readAllAttendanceHistoryList(PaginationInfo<AttendanceHistoryVO> paging){
		if(paging !=null) {
			int totalRecord = mapper.selectHrTotalRecord(paging);
			paging.setTotalRecord(totalRecord);
		}
		return mapper.selectAllAttendanceHistoryList(paging);
	}

	@Override
	public List<AttendanceHistoryVO> readDownloadAttendanceExcel(Map<String, Object> condition) {
		
		return mapper.selectDownloadAttendaceExcel(condition);
	}

	@Override
	public List<EmployeeVO> readEmpName(String searchWord) {
		List<EmployeeVO> result = mapper.selectEmpName(searchWord);
	    return result != null ? result : new ArrayList<>();

	}

	@Override
	public AttendTbVO readAttendTime() {
		return mapper.selectAttendTime();
	}

	@Override
	public List<CommonCodeVO> readAllAttendTimeList() {
		return mapper.selectAllAttendTimeList();
	}

	@Override
	public List<CommonCodeVO> readAllLeaveTimeList() {
		
		return mapper.selectAllLeaveTimeList();
	}

	@Override
	public ServiceResult modifyAttendTime(AttendTbVO att) {
		return mapper.updateAttendTime(att) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
		
	}

	@Override
	public ServiceResult addMyHahisTime(AttendanceHistoryVO ah) {
		return mapper.insertMyHahisTime(ah) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
		
	}

	@Override
	public AttendanceHistoryVO readMyHahisTime(String empId) {	
		return mapper.selectMyHahisTime(empId);
		
	}
	@Override
	public AttendanceHistoryVO readMyHleaveTime(String empid) {
		return mapper.selectMyHleaveTime(empid);
	}

	@Override
	public ServiceResult addMyHleaveTime(AttendanceHistoryVO ah) {
		return mapper.insertMyHleaveTime(ah) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public AttendanceHistoryVO readOneAttendDetail(String empId , String atthisId) {
		return mapper.selectOneAttendDetail(empId , atthisId);
	}

	@Override
	public List<CommonCodeVO> readAttendStatus() {
		return mapper.selectAttendStatus();
	}

	@Override
	public List<CommonCodeVO> readLeaveStatus() {
		return mapper.selectLeaveStatus();
	}

	@Override
	public ServiceResult modifyAttendLeaveStatus(AttendanceHistoryVO ah) {
		return mapper.updateAttendLeaveStatus(ah) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
		
				
	
	
	
	
	
}
