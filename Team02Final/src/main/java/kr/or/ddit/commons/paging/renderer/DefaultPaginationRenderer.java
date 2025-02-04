package kr.or.ddit.commons.paging.renderer;

import kr.or.ddit.commons.paging.PaginationInfo;

public class DefaultPaginationRenderer implements PaginationRenderer {

	// 키워드 검색 필요시 카톡 부탁 드립니다.
	@Override
	public String renderPagination(PaginationInfo paging, String fnName) {
		int startPage = paging.getStartPage();
		int endPage = paging.getEndPage();
		int totalPage = paging.getTotalPage();
		int blockSize = paging.getBlockSize();
		int currentPage = paging.getCurrentPage();

		endPage = endPage > totalPage ? totalPage : endPage;

		StringBuffer html = new StringBuffer();
		html.append("<ul class='pagination'>");

		String pattern = "<li class='page-item'><a class='page-link' href='javascript:void(0);' onclick='paging(%d);'>%s</a></li>";
		
		String prevPattern = "<li class='page-item prev'><a class='page-link' href='javascript:void(0);' onclick='paging(%d);'>"
				+ "<i class=\"icon-base bx bx-chevron-left icon-sm\"></i></a></li>";
		
		String nextPattern = "<li class='page-item next'><a class='page-link' href='javascript:void(0);' onclick='paging(%d);'>"
				+ "<i class=\"icon-base bx bx-chevron-right icon-sm\"></i></a></li>";
		
		
		if (startPage > blockSize) {
			html.append(String.format(prevPattern, startPage - blockSize));
		}

		for (int page = startPage; page <= endPage; page++) {
			if (page == currentPage) {
				html.append(String.format(
						"<li class='page-item active'><a class='page-link' href='javascript:void(0);'> %d </a></li>",
						page));
			} else {
				html.append(String.format(pattern, page, page));
			}

		}

		if (endPage < totalPage) {
			// 남은 페이지가 있음, 다음으로 갈 곳이 있음
			html.append(String.format(nextPattern, endPage + 1));
		}

		html.append("</ul>");
		
		

		return html.toString();

	}
}