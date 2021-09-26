package top.zylsite.jxml.report.enums;

/**
 * 写入方式
 *
 * @author zhaoyl
 * @date 2021/09/26 10:51
 **/
public enum ExcelWriteWay {

    /**
     * 单个单元格写入
     */
    SINGLE_CELL("1", "单个单元格写入"),
    /**
     * 插入行或列
     */
    INSERT_ROW_OR_COL("2", "插入行或列"),
    /**
     * 追加行或列
     */
    APPEND_ROW_OR_COL("3", "追加行或列");

    String code;
    String name;

    ExcelWriteWay(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean contain(String value) {
        for (ExcelWriteWay writeway : ExcelWriteWay.values()) {
            if (value.equals(writeway.getCode())) {
                return true;
            }
        }
        return false;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getCustomValues() {
        StringBuilder sb = new StringBuilder();
        for (ExcelWriteWay v : ExcelWriteWay.values()) {
            sb.append(",").append(v.getCode());
        }
        String value = sb.toString();
        value = value.substring(1);
        value = "(" + value + ")";
        return value;
    }

}
