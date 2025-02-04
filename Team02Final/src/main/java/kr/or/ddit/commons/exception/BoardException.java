package kr.or.ddit.commons.exception;

/**
 * 게시판에서만 사용할 커스텀 예외 입니다.
 *
 */
public class BoardException extends RuntimeException{
	
	public BoardException() {
		super();
	}
	
	public BoardException(int boNo) {
		super(String.format("글번호 %d 에서 예외 발생", boNo));
	}
	
	public BoardException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
		super(message, cause, enableSuppression, writeableStackTrace);
	}
	
	public BoardException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BoardException(String message) {
		super(message);
	}
	
	public BoardException(Throwable cause) {
		super(cause);
	}
}
