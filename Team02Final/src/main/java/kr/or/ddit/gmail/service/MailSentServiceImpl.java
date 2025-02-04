package kr.or.ddit.gmail.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.dao.MailSentMapper;
import kr.or.ddit.gmail.vo.MailSentVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailSentServiceImpl implements MailSentService {
	@Inject
	private MailSentMapper sentMapper;

	@Override
	public String readLastApiCallTime(String empMail) {
		return sentMapper.selectLastApiCallTime(empMail);
	}

	@Override
	public List<MailSentVO> readSentMailList(PaginationInfo paging) {
		// 페이지네이션
		int totalRecord = sentMapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		// 데이터베이스에서 리스트 조회해오기
		List<MailSentVO> list = sentMapper.selectSentMailList(paging);
		// 리스트 한 개씩 조회
		for(MailSentVO sent : list) {
			List<String> toList = new ArrayList<>();
			// 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
			String smailTo = sent.getSmailTo();
			// , 를 기준으로 자르기
			String[] emailArray = smailTo.split(",");
	        for (String email : emailArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = readSearchMail(trimEmail);
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
	        	sent.setSmailToList(toList);
	        }
		}
		return list;
	}
	
	@Override
	public MailSentVO readSentMailDetail(String messageId) {
		MailSentVO sm = sentMapper.selectSentMailDetail(messageId);
		
		List<String> toList = new ArrayList<>();
		// 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
		String smailTo = sm.getSmailTo();
		String[] mailToArray = smailTo.split(",");
        for (String email : mailToArray) {
        	// , 기준으로 자른 메일 배열 한 개씩 조회
        	// 공백 있을 수 있으니 공백 날리기
        	String trimEmail = email.trim();
        	// 해당 메일 주소로 employee에 정보 있는지 조회
        	EmployeeVO mailEmp = readSearchMail(trimEmail);
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
        	sm.setSmailToList(toList);
        }
        
        List<String> ccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String smailCc = sm.getSmailCc();
        if(StringUtils.isNotBlank(smailCc)){
        	String[] mailCcArray = smailCc.split(",");
            for (String email : mailCcArray) {
            	// , 기준으로 자른 메일 배열 한 개씩 조회
            	// 공백 있을 수 있으니 공백 날리기
            	String trimEmail = email.trim();
            	// 해당 메일 주소로 employee에 정보 있는지 조회
            	EmployeeVO mailEmp = readSearchMail(trimEmail);
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
            	sm.setSmailCcList(ccList);
            }
        }
        
        
        List<String> bccList = new ArrayList<>();
        // 조회해 온 메일 리스트의 메일 한 개에 들어있는 to
        String smailBcc = sm.getSmailBcc();
        if(StringUtils.isNotBlank(smailBcc)){
	        String[] mailBCcArray = smailBcc.split(",");
	        for (String email : mailBCcArray) {
	        	// , 기준으로 자른 메일 배열 한 개씩 조회
	        	// 공백 있을 수 있으니 공백 날리기
	        	String trimEmail = email.trim();
	        	// 해당 메일 주소로 employee에 정보 있는지 조회
	        	EmployeeVO mailEmp = readSearchMail(trimEmail);
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
	        	sm.setSmailBccList(bccList);
	        }
		}
		return sm;
	}

	
	@Override
	public int readSentMailExist(MailSentVO sentMail) {
		return sentMapper.selectSentMailExist(sentMail);
	}
	
	
	@Override
	public int createSentMail(MailSentVO sentMail) {
		return sentMapper.insertSentMail(sentMail);
	}

	@Override
	public int deleteSentMail(String mailMessageId) {
		return sentMapper.deleteSentMail(mailMessageId);
	}

	@Override
	public EmployeeVO readSearchMail(String empMail) {
		return sentMapper.selectSearchMail(empMail);
	}


}
