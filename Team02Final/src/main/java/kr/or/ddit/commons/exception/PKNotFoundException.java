package kr.or.ddit.commons.exception;

/**
 * primary key 를 검색조건으로 사용하는 경우, 해당 레코드가 존재하지 않는 상황에 대한 예외.
 *
 */
public class PKNotFoundException extends RuntimeException{

	public PKNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PKNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public PKNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PKNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PKNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
