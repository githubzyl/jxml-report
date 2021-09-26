package top.zylsite.jxml.report.exception;

/**
 * 不包含属性异常
 *
 * @author zhaoyl
 * @date 2021/09/26 13:10
 **/
public class NotContainFieldException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotContainFieldException() {
        super();
    }

    public NotContainFieldException(String message) {
        super(message);
    }

}
