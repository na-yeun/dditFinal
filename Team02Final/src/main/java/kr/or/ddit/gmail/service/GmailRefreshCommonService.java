package kr.or.ddit.gmail.service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.OAuthVO;

public interface GmailRefreshCommonService {
	/**
	 * 전체 새로고침
	 * @param oAuth
	 */
	public ServiceResult allRefresh(OAuthVO oAuth, String empMail);
	
}
