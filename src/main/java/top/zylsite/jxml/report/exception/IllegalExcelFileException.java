package top.zylsite.jxml.report.exception;

/**
 * 非法EXCEL文件异常
 *
 * @author zhaoyl
 * @date 2021/09/26 13:08
 **/
public class IllegalExcelFileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalExcelFileException() {
        super();
    }

    public IllegalExcelFileException(String message) {
        super(message);
    }

}
