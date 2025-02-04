package kr.or.ddit.gmail.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.dao.MailImportantMapper;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class MailImportantServiceImpl implements MailImportantService {
	@Inject
	private MailImportantMapper importantMapper;
	
	@Inject
	private MailSentService sentService;

	@Override
	public String readLastApiCallTime(String empMail) {
		// TODO Auto-generated method stub
		return importantMapper.selectLastApiCallTime(empMail);
	}

	@Override
	public List<MailImportantVO> readImportantMailList(PaginationInfo paging) {
		int totalRecord = importantMapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
				
		// 데이터베이스에서 리스트 조회해오기
		List<MailImportantVO> list = importantMapper.selectImportantMailList(paging);
		// important메일은 보낸 사람, 받은 사람 전부 리스트로 가지고 와야함
		for(MailImportantVO important : list) {
			List<String> fromList = new ArrayList<>();
			List<String> toList = new ArrayList<>();
			// 아래 부분 to 리스트로 한 번 더 와야함
			
			// 조회해 온 메일 리스트의 메일 한 개에 들어있는 from
			String imailFrom = important.getImailFrom();
//			// , 를 기준으로 자르기
			String[] imailFromArray = imailFrom.split(",");
	        for (String email : imailFromArray) {
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
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		fromList.add(trimEmail);
	        	}
	        	important.setImailFromList(fromList);
	        }
	        
	        
	        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
	        String imailTo = important.getImailTo();
	        // , 를 기준으로 자르기
	        String[] iemailToArray = imailTo.split(",");
	        for (String email : iemailToArray) {
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
	        		toList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		toList.add(trimEmail);
	        	}
	        	important.setImailToList(toList);
	        }
		}
		return list;
		
	}

	@Override
	public MailImportantVO readImportantMailDetail(String messageId) {
		// TODO Auto-generated method stub
		
		MailImportantVO important = importantMapper.selectImportantMailDetail(messageId);
		
		List<String> fromList = new ArrayList<>();
		List<String> toList = new ArrayList<>();
		// 아래 부분 to 리스트로 한 번 더 와야함
		
		// 조회해 온 메일 리스트의 메일 한 개에 들어있는 from
		String imailFrom = important.getImailFrom();
		// , 를 기준으로 자르기
		String[] imailFromArray = imailFrom.split(",");
        for (String email : imailFromArray) {
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
        		important.setImailFromList(fromList);
        	} else {
        		// 회사 소속 직원이 아닐 경우
        		fromList.add(trimEmail);
        		important.setImailFromList(fromList);
        	}
        }
        
        
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String imailTo = important.getImailFrom();
        // , 를 기준으로 자르기
        String[] iemailToArray = imailTo.split(",");
        for (String email : iemailToArray) {
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
        		toList.add(searchEmp);
        		important.setImailToList(toList);
        	} else {
        		// 회사 소속 직원이 아닐 경우
        		toList.add(trimEmail);
        		important.setImailToList(toList);
        	}
        }
        
        List<String> ccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String imailCc = important.getImailCc();
        if(StringUtils.isNotBlank(imailCc)){
	        String[] mailCcArray = imailCc.split(",");
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
	        	important.setImailCcList(ccList);
	        }
        }
        
        List<String> bccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String smailBcc = important.getImailBcc();
        if(StringUtils.isNotBlank(smailBcc)) {
	        String[] mailBCcArray = smailBcc.split(",");
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
	        		bccList.add(searchEmp);
	        	} else {
	        		// 회사 소속 직원이 아닐 경우
	        		bccList.add(trimEmail);
	        	}
	        	important.setImailBccList(bccList);
	        }
        }
		return important;
	}

	@Override
	public int readImportantMailExist(MailImportantVO importantMail) {
		// TODO Auto-generated method stub
		return importantMapper.selectImportantMailExist(importantMail);
	}

	@Override
	public int createImportantMail(MailImportantVO importantMail) {
		// TODO Auto-generated method stub
		return importantMapper.insertImportantMail(importantMail);
	}

	@Override
	public int deleteImportantMail(String mailMessageid) {
		return importantMapper.deleteImportantMail(mailMessageid);
	}

}
