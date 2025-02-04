package kr.or.ddit.security.authorize;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager.Builder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;

import kr.or.ddit.resource.dao.SecuredResourceMapper;
import kr.or.ddit.resource.vo.SecuredResourceVO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReloadableAuthorizationManager implements AuthorizationManager<HttpServletRequest> {
	
	private final SecuredResourceMapper securedResourceMapper; // 설정파일에 수동으로 등록 할것이다.
	
	private RequestMatcherDelegatingAuthorizationManager realAuthManager; // 위임할 수 있는 매니저를 등록.
	
	@PostConstruct
	public void loadSecuredResource() {
		List<SecuredResourceVO> resList = securedResourceMapper.selectResourceList();
		Builder builder = RequestMatcherDelegatingAuthorizationManager.builder();
		for (SecuredResourceVO res : resList) {
			AntPathRequestMatcher reqMatcher = new AntPathRequestMatcher(res.getResUrl(), res.getResMethod());

			AuthorizationManager<RequestAuthorizationContext> delegateManager = null;
			if (CollectionUtils.isEmpty(res.getAuthorities())) {
				// 누구나 접근
				delegateManager = (a, c) -> new AuthorizationDecision(true); // permit all
			} else {
				// 접근 제한
				delegateManager = AuthorityAuthorizationManager
						.hasAnyRole(res.getAuthorities().stream().toArray(String[]::new));

			}
			builder.add(reqMatcher, delegateManager);
		}
		
		this.realAuthManager = builder.build();
	}
	
	/**
	 * 보호자원이 변경되었을때 리로드 하여 보호자원을 재등록해주는 메소드.
	 */
	public void reload() {
		// 보호자원이 변경되었을때 리로드 하여 보호자원을 재등록해주는 메소드.
		loadSecuredResource();
	}
	
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, HttpServletRequest object) {
		
		return realAuthManager.check(authentication, object);
	}

}
