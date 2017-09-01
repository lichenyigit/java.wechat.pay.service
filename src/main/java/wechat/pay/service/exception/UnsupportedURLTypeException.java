package wechat.pay.service.exception;

/**
 * Created by dell on 2015/11/29.
 */
public class UnsupportedURLTypeException extends RuntimeException {
    public UnsupportedURLTypeException() {
    }

    public UnsupportedURLTypeException(Throwable t) {
        super(t);
    }

    public UnsupportedURLTypeException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnsupportedURLTypeException(String msg) {
        super(msg);
    }
}

