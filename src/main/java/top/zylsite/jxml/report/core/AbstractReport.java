package top.zylsite.jxml.report.core;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentException;

import top.zylsite.jxml.report.property.ExcelProperty;
import top.zylsite.jxml.report.utils.ExcelUtil;

/**
 * 报表基类
 *
 * @author zhaoyl
 * @date 2021/09/26 10:27
 **/
public abstract class AbstractReport implements Report {

    protected File xmlFile;
    protected ExcelProperty excel;

    public AbstractReport(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public ExcelProperty getExcelProperty() throws FileNotFoundException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, DocumentException {
        // 解析xml配置
        excel = XmlResolver.getInstance().getExcelReport(xmlFile);
        return excel;
    }

    public void renderFile(Workbook workbook, File outExcelFile) {
        // 公式计算
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        // 写入excel文件
        ExcelUtil.writeDataToExcel(workbook, outExcelFile);
    }

}
