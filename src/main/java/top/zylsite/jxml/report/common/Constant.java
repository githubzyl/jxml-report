package top.zylsite.jxml.report.common;

/**
 * 系统常量
 *
 * @author
 * @date 2021/09/26 10:25
 **/
public class Constant {

    public final static String EXCEL = "excel";
    public final static String WORKBOOK = "workbook";
    public final static String SHEET = "sheet";
    public final static String TEMPLATE = "template";
    public final static String TARGETS = "targets";
    public final static String TARGET = "target";

    /**
     * 字段名分隔符  真正的分隔符是|,java解析需要转义，所以此处多了\\s
     */
    public final static String FIELD_NAME_SEPARATOR = "\\|";

    /**
     * 常量字符串表达式包裹器
     */
    public final static String CONST_SELECTOR_START = "CS(";

    /**
     * 常量数字选择器
     */
    public final static String CONST_SELECTOR_END = ")";

    public final static String EQ = "=";

    public final static String BLANK_STR = "";

    public final static String LEFT_SQUARE_BRACKET = "[";

    public final static String RIGHT_SQUARE_BRACKET = "[";

}
