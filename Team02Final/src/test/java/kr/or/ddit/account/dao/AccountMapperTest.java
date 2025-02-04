package kr.or.ddit.account.dao;


import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RootContextWebConfig
class AccountMapperTest {
	@Inject
	private AccountMapper mapper;
	
	@Test
	void testSelectAccountList() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectAccount() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectAccountForAuth() {
		AccountVO a = new AccountVO();
		a.setAccountMail("kimny950111@gmail.com");
		a.setAccountPass("test");
//		log.info("{}",mapper.selectAccountForAuth(a));
//		a.setAccountMail("kyungjurhdqn@gmail.com");
//		a.setAccountPass("123");
		EmployeeVO m = (EmployeeVO)mapper.selectAccountForAuth(a);
		
		log.info("{}",m.getBase64EmpImg().length());
	}

	@Test
	void testInsertAccount() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateAccount() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateStatusAccount() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteAccount() {
		fail("Not yet implemented");
	}

}
