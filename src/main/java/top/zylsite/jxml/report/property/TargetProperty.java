package top.zylsite.jxml.report.property;

import top.zylsite.jxml.report.annotation.ValidField;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 代表报表的一个指标
 *
 * @author: zhaoyl
 * @since: 2017年9月13日 下午3:20:30
 * @history:
 */
public class TargetProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据集ID
     */
    @ValidField
    private String dataSetId;
    /**
     * 数据起始行,默认从0开始
     */
    @ValidField(pattern = "[1-9]\\d*|0", patternPromptMsg = "a non-negative integer")
    private String startDataIndex;
    /**
     * 写入方式
     */
    @ValidField(enumClassName = "top.zylsite.jxml.report.enums.ExcelWriteWay")
    private String writeWay;
    /**
     * 写入方向
     */
    @ValidField(enumClassName = "top.zylsite.jxml.report.enums.ExcelWriteDirection")
    private String writeDirection;
    /**
     * 过滤器：sql语句where后的子句
     */
    @ValidField(required = false)
    private String filter;
    /**
     * 起始列坐标：excel列号，用大写字母表示
     */
    @ValidField(required = false, pattern = "[A-Z]+", patternPromptMsg = "consisting of uppercase letters")
    private String startColIndex;
    /**
     * 起始行坐标 ：excel行号，用数字表示
     */
    @ValidField(required = false, pattern = "[1-9]\\d*", patternPromptMsg = "a positive integer")
    private String startRowIndex;
    /**
     * 结束列坐标：excel列号，用大写字母表示
     */
    @ValidField(required = false, pattern = "[A-Z]+", patternPromptMsg = "consisting of uppercase letters")
    private String endColIndex;
    /**
     * 结束行坐标：excel行号，用数字表示
     */
    @ValidField(required = false, pattern = "[1-9]\\d*", patternPromptMsg = "a positive integer")
    private String endRowIndex;

    /**
     * 单元格集合
     */
    private String cellList;

    /**
     * 字段集合
     * 约定：字段名全部用双引号包裹；constStr()包裹的值代表常量字符串；constNum()包裹的值是常量数字；所有标点符号全部是英文半角输入法下的标点符号
     * 1、字段名用英文双引号包裹，多个字段名用|分隔 2、列中如果赋值是常量值，用[]包裹，字段名和常量值用|分隔，
     * 如：["常量字段名"|constStr(abc)]、["常量字段名"|constNum(12)]、[constNum(12)|"常量字段名"]
     * 3、列中如果是函数表达式，用[]包裹，如：[add("字段A",constNum(3))]、[concat("字段A",constStr(a))]
     * 4、如果要跨行或跨列展示，那么字段名为空即可，如："A"|"B"||"C"
     */
    @ValidField
    private String fieldNames;

    public static Map<String, Class<?>> getAttrMap() {
        Map<String, Class<?>> fieldMap = new HashMap<>(16);
        Class<?> clazz = TargetProperty.class;
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (2 == fields[i].getModifiers()) {
                fieldMap.put(fields[i].getName(), fields[i].getType());
            }
        }
        return fieldMap;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getStartDataIndex() {
        return startDataIndex;
    }

    public void setStartDataIndex(String startDataIndex) {
        this.startDataIndex = startDataIndex;
    }

    public String getWriteWay() {
        return writeWay;
    }

    public void setWriteWay(String writeWay) {
        this.writeWay = writeWay;
    }

    public String getWriteDirection() {
        return writeDirection;
    }

    public void setWriteDirection(String writeDirection) {
        this.writeDirection = writeDirection;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getStartColIndex() {
        return startColIndex;
    }

    public void setStartColIndex(String startColIndex) {
        this.startColIndex = startColIndex;
    }

    public String getStartRowIndex() {
        return startRowIndex;
    }

    public void setStartRowIndex(String startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    public String getEndColIndex() {
        return endColIndex;
    }

    public void setEndColIndex(String endColIndex) {
        this.endColIndex = endColIndex;
    }

    public String getEndRowIndex() {
        return endRowIndex;
    }

    public void setEndRowIndex(String endRowIndex) {
        this.endRowIndex = endRowIndex;
    }

    public String getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String getCellList() {
        return cellList;
    }

    public void setCellList(String cellList) {
        this.cellList = cellList;
    }

    @Override
    public String toString() {
        return "TargetProperty{" +
                "dataSetID='" + dataSetId + '\'' +
                ", startDataIndex='" + startDataIndex + '\'' +
                ", writeWay='" + writeWay + '\'' +
                ", writeDirection='" + writeDirection + '\'' +
                ", filter='" + filter + '\'' +
                ", startColIndex='" + startColIndex + '\'' +
                ", startRowIndex='" + startRowIndex + '\'' +
                ", endColIndex='" + endColIndex + '\'' +
                ", endRowIndex='" + endRowIndex + '\'' +
                ", cellList='" + cellList + '\'' +
                ", fieldNames='" + fieldNames + '\'' +
                '}';
    }

}
