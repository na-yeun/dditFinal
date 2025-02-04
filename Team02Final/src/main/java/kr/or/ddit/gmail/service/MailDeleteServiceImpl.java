package kr.or.ddit.gmail.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.dao.MailDeleteMapper;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;

@Service
public class MailDeleteServiceImpl implements MailDeleteService {
	@Inject
	private MailDeleteMapper deleteMapper;
	
	@Inject
	private MailSentService sentService;
	
	@Override
	public String readLastApiCallTime(String empMail) {
		return deleteMapper.selectLastApiCallTime(empMail);
	}

	@Override
	public List<MailDeleteVO> readDeleteMailList(PaginationInfo paging) {
		int totalRecord = deleteMapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
	
		// 데이터베이스에서 리스트 조회해오기
		List<MailDeleteVO> list = deleteMapper.selectDeleteMailList(paging);
		
		for(MailDeleteVO delete : list) {
			List<String> fromDelList = new ArrayList<>();
			List<String> toDelList = new ArrayList<>();
			// 아래 부분 to 리스트로 한 번 더 와야함
			
			// 조회해 온 메일 리스트의 메일 한 개에 들어있는 from
			String delMailFrom = delete.getDelmailFrom();
			// , 를 기준으로 자르기
			String[] delMailFromArray = delMailFrom.split(",");
	        for (String email : delMailFromArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
	        	if(mailEmp!=null) {
	        		// 회사 소속 직원일 경우
	        		String searchEmpName = mailEmp.getEmpName();
	        		//String searchEmpMail = mailEmp.getEmpMail();
	        		String searchEmpDepartmentName = mailEmp.getPosiName();
	        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
	        		fromDelList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		fromDelList.add(trimEmail);
	        	}
	        	delete.setDelmailFromList(fromDelList);
	        }
	        
	        
	        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
	        String delMailTo = delete.getDelmailTo();
	        // , 를 기준으로 자르기
	        String[] delmailToArray = delMailTo.split(",");
	        for (String email : delmailToArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
	        	if(mailEmp!=null) {
	        		// 회사 소속 직원일 경우
	        		String searchEmpName = mailEmp.getEmpName();
	        		//String searchEmpMail = mailEmp.getEmpMail();
	        		String searchEmpDepartmentName = mailEmp.getPosiName();
	        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
	        		toDelList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		toDelList.add(trimEmail);
	        	}
	        	delete.setDelmailToList(toDelList);
	        }
		}
		return list;
		
	}

	@Override
	public MailDeleteVO readDeleteMailDetail(String messageId) {
		MailDeleteVO delete = deleteMapper.selectDeleteMailDetail(messageId);
		
		List<String> fromDelList = new ArrayList<>();
		List<String> toDelList = new ArrayList<>();
		// 아래 부분 to 리스트로 한 번 더 와야함
		
		// 조회해 온 메일 리스트의 메일 한 개에 들어있는 from
		String delMailFrom = delete.getDelmailFrom();
		// , 를 기준으로 자르기
		String[] delMailFromArray = delMailFrom.split(",");
        for (String email : delMailFromArray) {
        	// , 기준으로 자른 메일 배열 한 개씩 조회
        	// 공백 있을 수 있으니 공백 날리기
        	String trimEmail = email.trim();
        	// 해당 메일 주소로 employee에 정보 있는지 조회
        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
        	if(mailEmp!=null) {
        		// 회사 소속 직원일 경우
        		String searchEmpName = mailEmp.getEmpName();
        		//String searchEmpMail = mailEmp.getEmpMail();
        		String searchEmpDepartmentName = mailEmp.getPosiName();
        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
        		fromDelList.add(searchEmp);
        	} else {
        		// 회사 소속 직원이 아닐 경우
        		fromDelList.add(trimEmail);
        	}
        	delete.setDelmailFromList(fromDelList);
        }
        
        
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String delMailTo = delete.getDelmailTo();
        // , 를 기준으로 자르기
        String[] delmailToArray = delMailTo.split(",");
        for (String email : delmailToArray) {
        	// , 기준으로 자른 메일 배열 한 개씩 조회
        	// 공백 있을 수 있으니 공백 날리기
        	String trimEmail = email.trim();
        	// 해당 메일 주소로 employee에 정보 있는지 조회
        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
        	if(mailEmp!=null) {
        		// 회사 소속 직원일 경우
        		String searchEmpName = mailEmp.getEmpName();
        		//String searchEmpMail = mailEmp.getEmpMail();
        		String searchEmpDepartmentName = mailEmp.getPosiName();
        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
        		toDelList.add(searchEmp);
        	} else {
        		// 회사 소속 직원이 아닐 경우
        		toDelList.add(trimEmail);
        	}
        	delete.setDelmailToList(toDelList);
        }
		
        List<String> delCcList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String delmailCc = delete.getDelmailCc();
        if(StringUtils.isNotBlank(delmailCc)){
	        String[] mailCcArray = delmailCc.split(",");
	        for (String email : mailCcArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
	        	if(mailEmp!=null) {
	        		// 회사 소속 직원일 경우
	        		String searchEmpName = mailEmp.getEmpName();
	        		//String searchEmpMail = mailEmp.getEmpMail();
	        		String searchEmpDepartmentName = mailEmp.getPosiName();
	        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
	        		delCcList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		delCcList.add(trimEmail);
	        	}
	        	delete.setDelmailCcList(delCcList);
	        }
        }
        
        List<String> delbccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String delmailBcc = delete.getDelmailBcc();
        if(StringUtils.isNotBlank(delmailBcc)){
	        String[] mailBCcArray = delmailBcc.split(",");
	        for (String email : mailBCcArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = sentService.readSearchMail(trimEmail);
	        	if(mailEmp!=null) {
	        		// 회사 소속 직원일 경우
	        		String searchEmpName = mailEmp.getEmpName();
	        		//String searchEmpMail = mailEmp.getEmpMail();
	        		String searchEmpDepartmentName = mailEmp.getPosiName();
	        		String searchEmp = String.format("%s %s", searchEmpDepartmentName, searchEmpName);
	        		delbccList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		delbccList.add(trimEmail);
	        	}
	        	delete.setDelmailBccList(delbccList);
	        }
        }
		
		return delete;
	}

	@Override
	public int readDeleteMailExist(MailDeleteVO deleteMail) {
		return deleteMapper.selectDeleteMailExist(deleteMail);
	}

	@Override
	public int createDeleteMail(MailDeleteVO deleteMail) {
		return deleteMapper.insertDeleteMail(deleteMail);
	}

	@Override
	public int deleteDeleteMail(String mailMessageid) {
		return deleteMapper.deleteDeleteMail(mailMessageid);
	}

}
