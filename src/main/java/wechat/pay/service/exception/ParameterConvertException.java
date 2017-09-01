package wechat.pay.service.exception;

import wechat.pay.service.exception.BaseException;

public class ParameterConvertException extends BaseException {
	private static final long serialVersionUID = -6993502737292105768L;

	public ParameterConvertException(String paramName,String paramValue) {
		super(paramName,paramValue);
	}
	
	public ParameterConvertException(Throwable e,String paramName,String paramValue) {
		super(e,paramName,paramValue);
	}
	@Override
	public String getErrorCode() {
		return "305";
	}

}
