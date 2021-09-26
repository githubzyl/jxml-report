package top.zylsite.jxml.report.validator;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.exception.IllegalReportXmlFileException;
import top.zylsite.jxml.report.utils.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * xml配置文件校验
 *
 * @author zhaoyl
 * @date 2021/09/26 13:34
 **/
public class XmlFileValidator {

    public final static String ONE = "one";
    public final static String AT_MOST_ONE = "at most one";
    public final static String AT_LEAST_ONE = "at least one";

    public static void verify(String filePath) throws FileNotFoundException, DocumentException {
        verify(new FileInputStream(filePath));
    }

    public static void verify(File file) throws FileNotFoundException, DocumentException {
        verify(new FileInputStream(file));
    }

    @SuppressWarnings("unchecked")
    public static void verify(InputStream inputStream) throws DocumentException {
        String elementName = Constant.EXCEL;
        Element root = XmlUtil.getRootNode(inputStream);
        if (null == root) {
            throwElementException(elementName, ONE);
            throw new IllegalReportXmlFileException("the xml file must contain one excel node");
        }

        // 校验workbook
        elementName = Constant.WORKBOOK;
        List<Element> list = root.elements(elementName);
        if (null == list) {
            throwElementException(elementName, ONE);
        }
        if (list.size() > 1) {
            throwElementException(elementName, AT_MOST_ONE);
        }

        // 校验template
        elementName = Constant.TEMPLATE;
        list = root.elements(elementName);
        if (null == list) {
            throwElementException(elementName, ONE);
        }
        if (list.size() > 1) {
            throwElementException(elementName, AT_MOST_ONE);
        }

        // 校验sheet
        elementName = Constant.SHEET;
        Element workbook = root.element(Constant.WORKBOOK);
        List<Element> sheets = workbook.elements(elementName);
        if (null == sheets || sheets.size() == 0) {
            throwElementException(elementName, AT_LEAST_ONE);
        }

    }

    public static void throwElementException(String elementName, String hint) {
        throw new IllegalReportXmlFileException("the xml file must contain " + hint + " " + elementName + " node");
    }

}
