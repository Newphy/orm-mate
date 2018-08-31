package cn.newphy.orm.mybatis.exception;

import cn.newphy.mate.exception.OrmateException;

/**
 * Mybatis Mate异常
 * @author Newphy
 * @createTime 2018/8/2
 */
public class MybatisMateException extends OrmateException {
    private static final long serialVersionUID = 1598869196937110727L;

    public MybatisMateException() {
    }

    public MybatisMateException(String message) {
        super(message);
    }

    public MybatisMateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisMateException(Throwable cause) {
        super(cause);
    }

    public MybatisMateException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
