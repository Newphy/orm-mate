package cn.newphy.mate.exception;

/**
 * Ormate异常
 *
 * @author Newphy
 * @createTime 2018/8/2
 */
public class OrmateException extends RuntimeException {

    private static final long serialVersionUID = -4644447450637139749L;

    public OrmateException() {
    }

    public OrmateException(String message) {
        super(message);
    }

    public OrmateException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmateException(Throwable cause) {
        super(cause);
    }

    public OrmateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
