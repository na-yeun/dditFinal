package kr.or.ddit.account.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.account.vo.AccountVO;


@Mapper
public interface AccountMapper {
	// crud
	public List<AccountVO> selectAccountList();
	public AccountVO selectAccount(@Param("accountMail")String accountMail);
	/**
	 * 로그인할 때
	 * @param account
	 * @return
	 */
	public AccountVO selectAccountForAuth(AccountVO account);
	
	public int insertAccount(AccountVO account);
	public int updateAccount(AccountVO account);
	public int deleteAccount(String accountMail);
}
