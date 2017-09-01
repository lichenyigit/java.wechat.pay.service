package wechat.pay.service.exception;

import wechat.pay.service.exception.BaseException;

public class UndefinedCommonException extends BaseException {
	  private static final long serialVersionUID = 2104587479207481733L;
	  
	  public UndefinedCommonException(Throwable e)
	  {
	    super(e, new Object[] { e.getMessage() });
	  }
	  
	  public String getErrorCode()
	  {
	    return "Undefined";
	  }
	}