package kr.or.ddit.account.service;

import java.util.List;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;

public interface AccountService {
	
	/**
	 * 새로운 account(계정) 추가
	 * @param account
	 * @return ServiceResult.OK / ServiceResult.FAIL
	 */
	public ServiceResult createAccount(AccountVO account);
	
	/**
	 * account(계정) 전체 조회
	 * @return 없으면 빈 list 반환
	 */
	public List<AccountVO> readAccountList();
	
	/**
	 * 한 명의 account(계정) 조회
	 * @param accountMail
	 * @return 없으면 PKNotFoundException
	 */
	public AccountVO readAccount(String accountMail);
	
	/**
	 * 로그인 시 일치하는 계정 있는지 확인하는 메소드
	 * @param account
	 * @return 없으면 PKNotFoundException
	 */
	public AccountVO readAccountExist(AccountVO account);
	
	
	/**
	 * account(계정) 업데이트
	 * @param account
	 * @return ServiceResult.OK / ServiceResult.FAIL
	 */
	public ServiceResult modifyAccount(AccountVO account);
	

	
	/**
	 * account(계정) 삭제 - 쓸 일 없을 듯
	 * @param accountMail
	 * @return ServiceResult.OK / ServiceResult.FAIL
	 */
	public ServiceResult deleteAccount(String accountMail);
}
