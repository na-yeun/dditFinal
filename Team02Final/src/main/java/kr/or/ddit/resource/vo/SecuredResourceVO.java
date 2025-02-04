package kr.or.ddit.resource.vo;

import java.util.List;

import lombok.Data;

@Data
public class SecuredResourceVO {
	// resources 테이블
	private String resId;
	private String resUrl;
	private String resMethod;
	private int resSort;
	private String resParent;
	
	
	private List<String> authorities;	// director, admin 만 삭제 가능
}
