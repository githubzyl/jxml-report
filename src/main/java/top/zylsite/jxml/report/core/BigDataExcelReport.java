package top.zylsite.jxml.report.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.property.SheetProperty;
import top.zylsite.jxml.report.property.TargetProperty;
import top.zylsite.jxml.report.utils.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 大数据量excel report 此种报表仅支持纵向追加
 *
 * @author zhaoyl
 * @date 2021/09/26 10:28
 **/
public class BigDataExcelReport extends AbstractReport {

    private Map<String, List<Map<String, Object>>> excelData;

    public BigDataExcelReport(Map<String, List<Map<String, Object>>> excelData, File xmlFile) {
        super(xmlFile);
        this.excelData = excelData;
    }

    @Override
    public File generateReport(File outExcelFile) throws Exception {
        Workbook workbook = getWorkbook(outExcelFile);
        // 渲染excel文件
        super.renderFile(workbook, outExcelFile);
        return outExcelFile;
    }

    private Workbook getWorkbook(File outExcelFile) throws IOException {
        Workbook workbook = ExcelUtil.getWorkBook(outExcelFile);
        Sheet sheet;
        TargetProperty target;
        List<Map<String, Object>> targetData;
        String[] fieldNames;
        int startRow = 0, startCol = 0, colNum = 0, rowNum = 0;
        Map<String, Object> map;
        for (SheetProperty rSheet : excel.getWorkbook().getSheets()) {
            sheet = workbook.getSheet(rSheet.getName());
            target = rSheet.getTargets().get(0);
            targetData = excelData.get(target.getDataSetId());
            // 获取指标所有字段名
            fieldNames = target.getFieldNames().split(Constant.FIELD_NAME_SEPARATOR);
            startRow = Integer.parseInt(target.getStartRowIndex()) - 1;
            startCol = ExcelUtil.excelColStrToNum(target.getStartColIndex()) - 1;
            rowNum = targetData.size();
            colNum = fieldNames.length;
            for (int i = 0; i < rowNum; i++) {
                map = targetData.get(i);
                for (int j = 0; j < colNum; j++) {
                    ExcelUtil.setCellValue(workbook, sheet, i + startRow, j + startCol,
                            resolveFieldValue(fieldNames[j], map));
                }
            }
        }
        return workbook;
    }

    public Object resolveFieldValue(String fieldName, Map<String, Object> data) {
        // 判断是不是为空
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        // 先去左右两边空格
        fieldName = fieldName.trim();
        // 然后去掉左右两边的引号
        fieldName = fieldName.substring(1, fieldName.length() - 1);
        return data.get(fieldName);

    }

}
