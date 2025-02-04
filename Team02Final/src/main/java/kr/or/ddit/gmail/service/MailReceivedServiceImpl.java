package kr.or.ddit.gmail.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.dao.MailReceivedMapper;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSentVO;
@Service
public class MailReceivedServiceImpl implements MailReceivedService {
	@Inject
	private MailReceivedMapper mapper;
	
	@Inject
	private MailSentService sentService;
	
	@Override
	public String readLastApiCallTime(String empMail) {
		return mapper.selectLastApiCallTime(empMail);
	}

	@Override
	public List<MailReceivedVO> readReceivedMailList(PaginationInfo paging) {
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		// 데이터베이스에서 리스트 조회해오기
		List<MailReceivedVO> list = mapper.selectReceivedMailList(paging);
		
		for(MailReceivedVO received : list) {
			List<String> fromList = new ArrayList<>();
			// 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
			String rmailFrom = received.getRmailFrom();
			// , 를 기준으로 자르기
			String[] emailArray = rmailFrom.split(",");
	        for (String email : emailArray) {
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
	        		fromList.add(searchEmp);
	        		received.setRmailFromList(fromList);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		fromList.add(trimEmail);
	        		received.setRmailFromList(fromList);
	        	}
	        }
		}
		return list;
	}
	
	@Override
	public MailReceivedVO readReceivedMailDetail(String messageId) {
		MailReceivedVO mr = mapper.selectReceivedMailDetail(messageId);
		
		List<String> fromList = new ArrayList<>();
		// 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
		String rmailFrom = mr.getRmailFrom();
		// , 를 기준으로 자르기
		String[] emailArray = rmailFrom.split(",");
        for (String email : emailArray) {
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
        		fromList.add(searchEmp);
        		mr.setRmailFromList(fromList);
        	} else {
        		// 회사 소속 직원이 아닐 경우
        		fromList.add(trimEmail);
        		mr.setRmailFromList(fromList);
        	}
        }
        
        List<String> ccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String rmailCc = mr.getRmailCc();
        if(StringUtils.isNotBlank(rmailCc)){
	        // , 를 기준으로 자르기
	        String[] mailCcArray = rmailCc.split(",");
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
	        		ccList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		ccList.add(trimEmail);
	        	}
	        	mr.setRmailCcList(ccList);
	        }
        }
		
		return mr;
	}
	
	@Override
	public int readReceivedMailExist(MailReceivedVO receivedMail) {
		return mapper.selectReceivedMailExist(receivedMail);
		
	}
	
	@Override
	public int createReceivedMail(MailReceivedVO receivedMail) {
		return mapper.insertReceivedMail(receivedMail);
	}

	@Override
	public int deleteReceivedMail(String mailMessageid) {
		return mapper.deleteReceivedMail(mailMessageid);
	}

}
