package wechat.pay.service.exception;

/**
 * @author lichenyi
 * @date 2017-8-24-0024.
 */
public class HttpRequestFailedException extends BaseException {
    private static final long serialVersionUID = 1597920568895000516L;
    protected HttpRequestFailedException(Object... args) {
        super(args);
    }
    protected HttpRequestFailedException(Throwable t, Object... args) {
        super(t, args);
    }
    public HttpRequestFailedException(Exception e) {
        super(e);
    }
    @Override
    public String getErrorCode() {
        return "HU0020";
    }

}