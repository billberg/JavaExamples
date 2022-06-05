package data.robot;

public class HttpAntiSpiderException extends RuntimeException {

	private int code = 0;
	public HttpAntiSpiderException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public HttpAntiSpiderException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

	public int getCode() {
		return code;
	}
	
}
