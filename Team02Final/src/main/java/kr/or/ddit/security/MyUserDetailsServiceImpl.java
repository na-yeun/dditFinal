package kr.or.ddit.security;          

import java.util.Collections;

import javax.inject.Inject;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kr.or.ddit.account.dao.AccountMapper;
import kr.or.ddit.account.vo.AccountVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class MyUserDetailsServiceImpl implements UserDetailsService {
	@Inject
	private AccountMapper accountMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AccountVO inputData = new AccountVO();
		if(!username.contains("@gmail.com")) {
			inputData.setAccountMail(username+"@gmail.com");
		} else {
			inputData.setAccountMail(username);
		}

		// account vo를 파라미터로 넘길 필요가 없음..
		AccountVO vo = accountMapper.selectAccountForAuth(inputData); // username = mail
		
		// vo가 null이면 예외를 던지고 종료**
	    if (vo == null) {
	        log.error("사용자를 찾을 수 없습니다: {}", inputData.getAccountMail());
	        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + inputData.getAccountMail());
	    }
		
		AccountVOWrapper wrapper = new AccountVOWrapper(vo);
		
	
		
		return wrapper;
	}

}
