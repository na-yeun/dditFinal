package kr.or.ddit.account.service;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.annotation.RootContextWebConfig;

@RootContextWebConfig
class AccountServiceImplTest {
	@Inject
	private AccountServiceImpl service;
	
	@Disabled
	@Test
	void testCreateAccount() {
		AccountVO account = new AccountVO();
		account.setAccountMail("ㅋㅋㅋㅋ");
		service.createAccount(account);
	}

	@Test
	void testReadAccountList() {
		service.readAccountList();
	}

	@Test
	void testReadAccountExist() {
		AccountVO account = new AccountVO();
		account.setAccountMail("kimny950111@gmail.com");
		account.setAccountPass("test");
		service.readAccountExist(account);
	}

	@Test//
	void testReadAccount() {
		service.readAccount("kimny950111@gmail.com");
	}

	@Test
	void testModifyAccount() {
		AccountVO account = new AccountVO();
		account.setAccountMail("1234");
		account.setAccountPass("1234");
		service.modifyAccount(account);
	}

}
