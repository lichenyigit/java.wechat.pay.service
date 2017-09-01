package wechat.pay.service.exception;

public class JsonConvertFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public JsonConvertFailedException() {
    }

    public JsonConvertFailedException(Throwable t) {
        super(t);
    }

    public JsonConvertFailedException(String msg, Throwable t) {
        super(msg, t);
    }

    public JsonConvertFailedException(String msg) {
        super(msg);
    }
}

