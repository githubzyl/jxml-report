package top.zylsite.jxml.report.enums;

/**
 * 写入方向
 *
 * @author zhaoyl
 * @date 2021/09/26 10:50
 **/
public enum ExcelWriteDirection {

    /**
     * 纵向
     */
    VERTICAL("1", "纵向"),
    /**
     * 横向
     */
    HORIZONTAL("2", "横向");

    String code;
    String name;

    ExcelWriteDirection(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean contain(String value) {
        for (ExcelWriteDirection writeDirection : ExcelWriteDirection.values()) {
            if (value.equals(writeDirection.getCode())) {
                return true;
            }
        }
        return false;
    }

    public static String getCustomValues() {
        StringBuilder sb = new StringBuilder();
        for (ExcelWriteDirection v : ExcelWriteDirection.values()) {
            sb.append(",").append(v.getCode());
        }
        String value = sb.toString();
        value = value.substring(1);
        value = "(" + value + ")";
        return value;
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

}
