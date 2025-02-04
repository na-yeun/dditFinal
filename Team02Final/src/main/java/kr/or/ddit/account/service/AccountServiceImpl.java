package kr.or.ddit.account.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.account.dao.AccountMapper;
import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.PKNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {
	@Inject
	private AccountMapper mapper;

	@Override
	public ServiceResult createAccount(AccountVO account) {
		int rowcnt = mapper.insertAccount(account);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}

	@Override
	public List<AccountVO> readAccountList() {
		return mapper.selectAccountList();
	}
	
	@Override
	public AccountVO readAccountExist(AccountVO account) {
		AccountVO rowAccount = mapper.selectAccountForAuth(account);
		
		if(rowAccount==null) {
			throw new PKNotFoundException();
		} else {
			return rowAccount;
		}
	}
	
	@Override
	public AccountVO readAccount(String accountMail) {
		AccountVO rowData = mapper.selectAccount(accountMail);
		if(rowData==null) {
			throw new PKNotFoundException();
		}
		return rowData;
	}
	

	@Override
	public ServiceResult modifyAccount(AccountVO account) {
		int rowcnt = mapper.updateAccount(account);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}

	@Override
	public ServiceResult deleteAccount(String accountMail) {
		int rowcnt = mapper.deleteAccount(accountMail);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}

}
