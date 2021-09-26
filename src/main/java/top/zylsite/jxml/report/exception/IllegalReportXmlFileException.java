package top.zylsite.jxml.report.exception;

/**
 * 非法xml配置文件异常
 *
 * @author zhaoyl
 * @date 2021/09/26 13:09
 **/
public class IllegalReportXmlFileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalReportXmlFileException() {
        super();
    }

    public IllegalReportXmlFileException(String message) {
        super(message);
    }

}
