package kr.or.ddit.employee.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.employee.vo.OAuthVO;
@Mapper
public interface GoogleOAuthMapper {
	/**
	 * 회원가입시 처음으로 oauth 인증 정보 받을 때
	 * @param oAuth
	 * @return insert가 완료된 컬럼의 개수
	 */
	public int insertOAuth(OAuthVO oAuth);
	
	/**
	 * oauth 정보가 갱신되었을 때
	 * @param oAuth
	 * @return update가 완료된 컬럼의 개수
	 */
	public int updateOAuth(OAuthVO oAuth);

	/**
	 * 오어스 정보 겟
	 * @param oauthEmpmail
	 * @return
	 */
	public OAuthVO selectOAuth(@Param("oauthEmpmail")String oauthEmpmail);
	

}
