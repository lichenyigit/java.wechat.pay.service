package wechat.pay.service.exception;


public class RequestToMapException extends BaseException {
	private static final long serialVersionUID = 9211911225220917889L;

	@Override
	public String getErrorCode() {
		return "REQUEST0010";
	}

}
