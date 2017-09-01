package wechat.pay.service.util;

import wechat.pay.service.exception.BaseException;
import wechat.pay.service.exception.NoError;

public class Result<T> {

	private String errorCode;
	  private String errorDescription;
	  private T result;
	  
	  public Result()
	  {
	    this(new NoError());
	  }
	  
	  public Result(BaseException e){
	    setErrorCode(e.getErrorCode());
	    setErrorDescription(e.getMessage());
	  }

      public String getErrorCode()
	  {
	    return this.errorCode;
	  }
	  
	  public String getErrorDescription()
	  {
	    return this.errorDescription;
	  }
	  
	  public void setErrorDescription(String errorDescription)
	  {
	    this.errorDescription = errorDescription;
	  }
	  
	  public void setErrorCode(String errorCode)
	  {
	    this.errorCode = errorCode;
	  }
	  
	  public T getResult()
	  {
	    return this.result;
	  }
	  
	  public void setResult(T result)
	  {
	    this.result = result;
	  }
	
}
