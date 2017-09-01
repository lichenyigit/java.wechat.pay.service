package wechat.pay.service.exception;


public class ConvertFailedException extends BaseException {
	private static final long serialVersionUID = -4661918273265864955L;

	public ConvertFailedException() {
		super();
	}

	public ConvertFailedException(Throwable e) {
		super(e);
	}

	@Override
	public String getErrorCode() {
		return "300";
	}
}