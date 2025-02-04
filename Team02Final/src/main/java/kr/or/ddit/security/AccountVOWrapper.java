package kr.or.ddit.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import kr.or.ddit.account.vo.AccountVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountVOWrapper extends User{
	
	private final AccountVO realUser;
	// employee, provider는 기본으로 받는 롤
	// employee이면서 대리
	// employee이면서 대표 ...
	// provider이면서 직급(대표이사) ...
	public AccountVOWrapper(AccountVO realUser) {
		super(realUser.getAccountMail(), realUser.getAccountPass(), createAuthList(realUser)
				);
		this.realUser = realUser;
		log.info("realUser >>> {}",realUser);
	}
	
	// 롤 하나, 권한 하나를 표현하는 객체를 리스트로
	public static List<GrantedAuthority> createAuthList(AccountVO account){
		System.out.println(account.toString());
		// 추가적인 롤은 provider만 갖는지 / employee만 갖는지
		// account한테 role..
//		List<String> roles = Optional.ofNullable(account.getRoles())
//									 .orElse(new ArrayList<>());
		
		
		List<String> roles = Optional.ofNullable(account.getRoles())
                .filter(role -> !role.isEmpty())
                .orElseGet(ArrayList::new); // 올바른 Supplier 사용


		roles.add(account.getUserType());
		

		return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	//////////////////////////////////////////
	public AccountVO getAccount() {
		return realUser;
	}
}