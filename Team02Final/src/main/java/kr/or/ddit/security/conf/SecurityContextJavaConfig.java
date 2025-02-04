package kr.or.ddit.security.conf;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import kr.or.ddit.resource.dao.SecuredResourceMapper;
import kr.or.ddit.security.authorize.ReloadableAuthorizationManager;
import kr.or.ddit.security.handler.CustomAuthenticationFailureHandler;
import kr.or.ddit.security.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityContextJavaConfig {
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@Autowired

	@PostConstruct
	public void init() {
		authenticationManagerBuilder.eraseCredentials(false);
	}

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new PasswordEncoder() {

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				// TODO Auto-generated method stub
				return encodedPassword.equals(rawPassword);
			}

			@Override
			public String encode(CharSequence rawPassword) {
				// TODO Auto-generated method stub
				return rawPassword.toString();
			}
		};
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/resources/**");
	}

	@Inject
	private SecuredResourceMapper resMapper;

	@Bean
	public AuthorizationManager<HttpServletRequest> customAuthrozationManager() {
		return new ReloadableAuthorizationManager(resMapper);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable() // CSRF 보호 비활성화
				.anonymous().authorities("ROLE_ANONYMOUS") // 익명 사용자에게 ROLE_ANONYMOUS 권한 부여
				
				.and()
				.formLogin()
				.loginPage("/a001/login")// 사용자 정의 로그인 페이지
				.usernameParameter("accountMail") // 이메일 파라미터
				.passwordParameter("accountPass") // 비밀번호 파라미터
				.successHandler(customAuthenticationSuccessHandler).failureHandler(customAuthenticationFailureHandler)
				

				.and()
				.logout()
				.logoutUrl("/a001/logout") // 로그아웃 처리 경로
				.addLogoutHandler((request, response, authentication)->{
					SecurityContextHolder.clearContext();
				})
				// 성공하면 루트 페이지로 이동
				.logoutSuccessUrl("/a001/login")
				// 로그아웃 시 생성된 사용자 세션 삭제
				.invalidateHttpSession(true)
				
				.and()
				.authorizeRequests()
	            .antMatchers("/a001/login", "/a001/findPW", "/a001/empauth"
	            		, "/a001/join/mailCheck", "/a001/join", "/google-oauth", "/google-oauthcheck.do"
	            		, "/resources/**", "/static/**"
	            		, "/setupPage/**", "/contract/all/**" ).permitAll() // 로그인 페이지는 인증 필요 없음
	            .anyRequest().authenticated();

		return http.build();
	}

//	
	@Bean
	public HandlerMappingIntrospector handlerIntrospector() {
		return new HandlerMappingIntrospector();
	}
}
