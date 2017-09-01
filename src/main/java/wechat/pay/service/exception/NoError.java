package wechat.pay.service.exception;

@SuppressWarnings("serial")
public class NoError extends BaseException {

	@Override
	public String getErrorCode() {
		return "0";
	}

	public NoError()
	{
		super(new Object[0]);
	}
	  
	
}
