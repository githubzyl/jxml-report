package top.zylsite.jxml.report.exception;

/**
 * 非法表达式异常
 *
 * @author zhaoyl
 * @date 2021/09/26 13:08
 **/
public class IllegalExpressionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalExpressionException() {
        super();
    }

    public IllegalExpressionException(String message) {
        super(message);
    }

}
