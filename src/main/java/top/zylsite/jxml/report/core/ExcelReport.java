package top.zylsite.jxml.report.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.common.JavaScriptEngine;
import top.zylsite.jxml.report.enums.ExcelWriteDirection;
import top.zylsite.jxml.report.enums.ExcelWriteWay;
import top.zylsite.jxml.report.exception.IllegalExpressionException;
import top.zylsite.jxml.report.exception.NotContainFieldException;
import top.zylsite.jxml.report.property.SheetProperty;
import top.zylsite.jxml.report.property.TargetProperty;
import top.zylsite.jxml.report.property.TargetPropertyWithData;
import top.zylsite.jxml.report.utils.ExcelUtil;
import top.zylsite.jxml.report.utils.LoggerUtil;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * excel report
 *
 * @author zhaoyl
 * @date 2021/09/26 10:31
 **/
public class ExcelReport extends AbstractReport {

    private final Logger logger = LoggerUtil.getLogger(ExcelReport.class);

    private JSONObject excelData;

    public ExcelReport(JSONObject excelData, File xmlFile) {
        super(xmlFile);
        this.excelData = excelData;
    }

    @Override
    public File generateReport(File outExcelFile) throws IOException {
        Workbook workbook = getWorkbook(outExcelFile);
        // 渲染excel文件
        super.renderFile(workbook, outExcelFile);
        return outExcelFile;
    }

    public Workbook getWorkbook(File outExcelFile) throws IOException {
        Workbook workbook = ExcelUtil.getWorkBook(outExcelFile);
        Sheet sheet;
        List<TargetProperty> targets;
        JSONArray targetData;
        String writeWay;
        String writeDirection;
        String[] fieldNames;
        int startRow, endRow, startCol, endCol, rowNum, colNum, dataSize;
        List<TargetPropertyWithData> insertTargets = null;
        for (SheetProperty rSheet : excel.getWorkbook().getSheets()) {
            sheet = workbook.getSheet(rSheet.getName());
            // 获取所有指标集
            targets = rSheet.getTargets();

            // 给所有非插入方式的指标赋值，同时取出所有插入方式的指标集合
            for (TargetProperty target : targets) {
                // 获取指标数据集
                targetData = excelData.getJSONArray(target.getDataSetId());
                // 截取数据集
                targetData = getJsonData(Integer.parseInt(target.getStartDataIndex()), targetData);
                // 获取指标所有字段名
                fieldNames = target.getFieldNames().split(Constant.FIELD_NAME_SEPARATOR);

                // 根据单元格集合赋值
                if (StringUtils.isNotBlank(target.getCellList())) {
                    String[] cells = target.getCellList().split(",");
                    for (int i = 0; i < cells.length; i++) {
                        int[] rc = getCell(cells[i]);
                        ExcelUtil.setCellValue(workbook, sheet, rc[0], rc[1],
                                resolveFieldValue(fieldNames[i], targetData, 0));
                    }
                } else {
                    // 获取赋值区域坐标
                    startRow = Integer.parseInt(target.getStartRowIndex()) - 1;
                    endRow = Integer.parseInt(target.getEndRowIndex()) - 1;
                    startCol = ExcelUtil.excelColStrToNum(target.getStartColIndex()) - 1;
                    endCol = ExcelUtil.excelColStrToNum(target.getEndColIndex()) - 1;
                    // 获取行数和列数
                    rowNum = Math.abs(endRow - startRow) + 1;
                    colNum = Math.abs(endCol - startCol) + 1;
                    // 获取写入方式
                    writeWay = target.getWriteWay();

                    // 过滤数据
                    String filter = target.getFilter();
                    targetData = CustomFilter.filterData(targetData, filter);

                    // 单点插入
                    if (ExcelWriteWay.SINGLE_CELL.getCode().equals(writeWay)) {
                        ExcelUtil.setCellValue(workbook, sheet, startRow, startCol,
                                resolveFieldValue(fieldNames[0], targetData.getJSONObject(0), 0));
                    } else {
                        // 获取写入方向
                        writeDirection = target.getWriteDirection();
                        // 数据集大小
                        dataSize = targetData.size();
                        // 追加
                        if (ExcelWriteWay.APPEND_ROW_OR_COL.getCode().equals(writeWay)) {
                            // 纵向
                            if (ExcelWriteDirection.VERTICAL.getCode().equals(writeDirection)) {
                                rowNum = (dataSize <= rowNum) ? dataSize : (rowNum == 1 ? dataSize : rowNum);
                                for (int i = startRow; i < startRow + rowNum; i++) {
                                    for (int j = startCol; j < startCol + colNum; j++) {
                                        ExcelUtil.setCellValue(workbook, sheet, i, j,
                                                resolveFieldValue(fieldNames[j - startCol], targetData, i - startRow));
                                    }
                                }
                            }
                            // 横向
                            else {
                                colNum = (dataSize <= colNum) ? dataSize : (colNum == 1 ? dataSize : colNum);
                                for (int j = startCol; j < startCol + colNum; j++) {
                                    for (int i = startRow; i < startRow + rowNum; i++) {
                                        ExcelUtil.setCellValue(workbook, sheet, i, j,
                                                resolveFieldValue(fieldNames[i - startRow], targetData, j - startCol));
                                    }
                                }
                            }
                        }
                        // 插入
                        else {
                            if (null == insertTargets) {
                                insertTargets = new ArrayList<>();
                            }
                            TargetPropertyWithData targetExtend = this.convert(target);
                            targetExtend.setData(targetData);
                            insertTargets.add(targetExtend);
                        }
                    }
                }
            }

            // 已插入的行数
            int insertRowCount = 0;
            // 某个指标已插入的数据条数，每次有新指标插入时清零
            int count = 0;
            // 给所有插入方式的指标赋值
            if (null != insertTargets && insertTargets.size() > 0) {
                for (TargetPropertyWithData target : insertTargets) {
                    // 获取指标数据集
                    targetData = target.getData();
                    // 获取指标所有字段名
                    fieldNames = target.getFieldNames().split(Constant.FIELD_NAME_SEPARATOR);
                    // 获取赋值区域坐标
                    startRow = Integer.parseInt(target.getStartRowIndex()) - 1 + insertRowCount;
                    endRow = Integer.parseInt(target.getEndRowIndex()) - 1 + insertRowCount;
                    startCol = ExcelUtil.excelColStrToNum(target.getStartColIndex()) - 1;
                    endCol = ExcelUtil.excelColStrToNum(target.getEndColIndex()) - 1;
                    // 获取行数和列数
                    rowNum = Math.abs(endRow - startRow) + 1;
                    colNum = Math.abs(endCol - startCol) + 1;

                    // 获取写入方向
                    writeDirection = target.getWriteDirection();
                    // 数据集大小
                    dataSize = targetData.size();
                    // 已经插入数据的条数
                    count = 0;
                    // 纵向
                    if (ExcelWriteDirection.VERTICAL.getCode().equals(writeDirection)) {
                        while (count < dataSize) {
                            sheet.shiftRows(startRow, sheet.getLastRowNum(), 1, true, false);
                            for (int j = startCol; j < startCol + colNum; j++) {
                                ExcelUtil.setCellValue(workbook, sheet, startRow, j,
                                        resolveFieldValue(fieldNames[j - startCol], targetData, count));
                            }
                            count++;
                            insertRowCount++;
                        }
                    }
                    // 横向
                    else {
                        // 暂不支持横向插入列
                        for (int j = startCol; j < startCol + colNum; j++) {
                            for (int i = startRow; i < startRow + rowNum; i++) {
                                ExcelUtil.setCellValue(workbook, sheet, i, j,
                                        resolveFieldValue(fieldNames[i - startRow], targetData, j - startCol));
                            }
                        }
                    }

                }
            }
        }

        return workbook;
    }

    /**
     * 获取单元格的行号和列号
     *
     * @param cell
     * @return int[]
     * @author zhaoyl
     * @date 2021/09/26 13:36
     */
    public int[] getCell(String cell) {
        char ch;
        boolean flag;
        int index = 0;
        for (int i = 0; i < cell.length(); i++) {
            ch = cell.charAt(i);
            flag = StringUtils.isNumeric(String.valueOf(ch));
            if (flag) {
                index = i;
                break;
            }
        }
        int column = ExcelUtil.excelColStrToNum(cell.substring(0, index));
        int row = Integer.parseInt(cell.substring(index));

        return new int[]{row - 1, column - 1};
    }

    public Object resolveFieldValue(String fieldName, Object data, int dataIndex) {
        // 判断是不是为空
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        // 先去左右两边空格
        fieldName = fieldName.trim();
        String fieldValue;
        // 替换fieldName中所有字段的值
        if (fieldName.startsWith(Constant.LEFT_SQUARE_BRACKET) && fieldName.endsWith(Constant.RIGHT_SQUARE_BRACKET)) {
            // 去掉中括号
            fieldName = fieldName.substring(1, fieldName.length() - 1);
            // 判断是不是常量表达式
            // 常量表达式中的常量用“CS()包裹”
            if (isConstantExpression(fieldName)) {
                return getConstantValue(fieldName);
            }
            // 替换表达式中所有的字段名为字段值
            fieldValue = replaceFieldFromNameToValue(fieldName, data, dataIndex);
        } else {
            // 单个的字段名要去掉左右两边的引号
            fieldName = fieldName.substring(1, fieldName.length() - 1);
            fieldValue = getFieldValue(fieldName, data, dataIndex).toString();
        }
        //值为空直接返回
        if (null == fieldValue || "".equals(fieldValue)) {
            return fieldValue;
        }
        // 交给js去执行
        try {
            return JavaScriptEngine.resolve(fieldValue);
        } catch (ScriptException e) {
            logger.error("javascript解析异常:", e);
            throw new IllegalExpressionException("the expression is not illegal, please check it");
        }
    }

    /**
     * 将表达式中的字段名称替换成字段的值
     *
     * @param fieldName
     * @param data
     * @param dataIndex
     * @return java.lang.String
     * @author zhaoyl
     * @date 2021/09/26 10:37
     */
    private String replaceFieldFromNameToValue(String fieldName, Object data, int dataIndex) {
        String newField = fieldName;
        String currStr = "";
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < fieldName.length(); i++) {
            currStr = String.valueOf(fieldName.charAt(i));
            if ("\"".equals(currStr)) {
                list.add(i);
            }
        }
        for (int j = 0; j < list.size(); j += 2) {
            String s1 = fieldName.substring(list.get(j), list.get(j + 1) + 1);
            String s2 = fieldName.substring(list.get(j) + 1, list.get(j + 1));
            newField = newField.replace(s1, getFieldValue(s2, data, dataIndex).toString());
        }
        return newField;
    }

    /**
     * 获取字段的值
     *
     * @param fieldName
     * @param data
     * @param dataIndex
     * @return java.lang.Object
     * @author zhaoyl
     * @date 2021/09/26 10:37
     */
    private Object getFieldValue(String fieldName, Object data, int dataIndex) {
        JSONObject jsonObject = null;
        if (data instanceof JSONObject) {
            jsonObject = (JSONObject) data;
        } else if (data instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) data;
            jsonObject = jsonArray.getJSONObject(dataIndex);
        }
        if (jsonObject.containsKey(fieldName)) {
            Object value = jsonObject.get(fieldName);
            if (null == value) {
                return "";
            }
            if (value instanceof String) {
                return "defaultStr('" + value + "')";
            }
            return value;
        }
        throw new NotContainFieldException("the data not contain field name of [" + fieldName + "]");
    }

    /**
     * 判断是否常量表达式
     *
     * @param fieldName
     * @return boolean
     * @author zhaoyl
     * @date 2021/09/26 10:38
     */
    private boolean isConstantExpression(String fieldName) {
        int count = 0;
        for (int i = 0; i < fieldName.length(); i++) {
            if ("\"".equals(String.valueOf(fieldName.charAt(i)))) {
                count++;
            }
        }
        if (count == 2) {
            if (fieldName.indexOf(Constant.CONST_SELECTOR_START) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取常量值
     *
     * @param fieldName
     * @return java.lang.String
     * @author zhaoyl
     * @date 2021/09/26 10:37
     */
    private static String getConstantValue(String fieldName) {
        int index = fieldName.indexOf(Constant.CONST_SELECTOR_START);
        index += Constant.CONST_SELECTOR_START.length();
        for (int i = index; i < fieldName.length(); i++) {
            if (Constant.CONST_SELECTOR_END.equals(String.valueOf(fieldName.charAt(i)))) {
                return fieldName.substring(index, i);
            }
        }
        return "";
    }

    /**
     * 获取截取后的数据集
     *
     * @param startDataIndex
     * @param jsonArray
     * @return com.alibaba.fastjson.JSONArray
     * @author zhaoyl
     * @date 2021/09/26 10:37
     */
    private JSONArray getJsonData(int startDataIndex, JSONArray jsonArray) {
        // 数据开始行
        if (startDataIndex > 0) {
            JSONArray data = new JSONArray();
            for (int i = startDataIndex; i < jsonArray.size(); i++) {
                data.add(jsonArray.get(i));
            }
            return data;
        }
        return jsonArray;
    }

    private TargetPropertyWithData convert(TargetProperty property) {
        TargetPropertyWithData entity = new TargetPropertyWithData();
        entity.setWriteWay(property.getWriteWay());
        entity.setWriteDirection(property.getWriteDirection());
        entity.setStartRowIndex(property.getStartRowIndex());
        entity.setStartColIndex(property.getStartColIndex());
        entity.setStartDataIndex(property.getStartDataIndex());
        entity.setEndRowIndex(property.getEndRowIndex());
        entity.setEndColIndex(property.getEndColIndex());
        entity.setFilter(property.getFilter());
        entity.setFieldNames(property.getFieldNames());
        entity.setCellList(property.getCellList());
        entity.setDataSetId(property.getDataSetId());
        return entity;
    }

}
