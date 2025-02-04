package kr.or.ddit.commons.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 키워드 검색용
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCondition {
	// 검색 타입
	private String searchType;
	
	// 검색명
	private String searchWord;
}
