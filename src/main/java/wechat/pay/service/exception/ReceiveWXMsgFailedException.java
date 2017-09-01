package wechat.pay.service.exception;


public class ReceiveWXMsgFailedException extends BaseException {
	private static final long serialVersionUID = -4661918273265864955L;

	public ReceiveWXMsgFailedException() {
		super();
	}

	public ReceiveWXMsgFailedException(Throwable e) {
		super(e);
	}

	@Override
	public String getErrorCode() {
		return "-30001";
	}
}