package kr.or.ddit.commons.paging.renderer;

import kr.or.ddit.commons.paging.PaginationInfo;

public interface PaginationRenderer {
	
	/**
	 * 
	 * @param paging
	 * @param fnName
	 * @return
	 */
	public String renderPagination(PaginationInfo paging, String fnName);
	
}
