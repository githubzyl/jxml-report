package top.zylsite.jxml.report.exception;

/**
 * 参数为空异常
 *
 * @author zhaoyl
 * @date 2021/09/26 13:10
 **/
public class NullParameterValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NullParameterValueException() {
        super();
    }

    public NullParameterValueException(String message) {
        super(message);
    }

}
