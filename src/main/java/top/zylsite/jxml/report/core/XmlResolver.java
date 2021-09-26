package top.zylsite.jxml.report.core;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.exception.IllegalReportXmlFileException;
import top.zylsite.jxml.report.property.*;
import top.zylsite.jxml.report.utils.XmlUtil;
import top.zylsite.jxml.report.validator.TargetValidator;
import top.zylsite.jxml.report.validator.TemplateValidator;
import top.zylsite.jxml.report.validator.XmlFileValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * xml解析器 获取整个report.xml文件内容
 * 静态内部类单例模式
 *
 * @author zhaoyl
 * @date 2021/09/26 10:40
 **/
public class XmlResolver {

    private XmlResolver() {

    }

    private static class XmlResolverHolder {
        private final static XmlResolver INSTANCE = new XmlResolver();
    }

    public static XmlResolver getInstance() {
        return XmlResolverHolder.INSTANCE;
    }

    public ExcelProperty getExcelReport(String xmlPath) throws FileNotFoundException, DocumentException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        return getExcelReport(new File(xmlPath));
    }

    @SuppressWarnings("unchecked")
    public ExcelProperty getExcelReport(File xmlFile) throws FileNotFoundException, DocumentException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        ExcelProperty excel;
        WorkbookProperty workbook;
        TemplateProperty template;
        SheetProperty rSheet;
        List<SheetProperty> sheetList;

        Element root = XmlUtil.getRootNode(xmlFile);

        excel = new ExcelProperty();
        workbook = new WorkbookProperty();
        template = new TemplateProperty();
        sheetList = new ArrayList<>();

        Element eTemplate = root.element(Constant.TEMPLATE);
        template.setName(eTemplate.elementText("name"));
        template.setSuffix(eTemplate.elementText("suffix"));
        template.setContent(eTemplate.elementText("content"));
        // 校验模板
        TemplateValidator.verify(template);

        Element eWorkbook = root.element(Constant.WORKBOOK);
        List<Element> eSheets = eWorkbook.elements(Constant.SHEET);
        Element eSheet = null;
        String sheetName = null;
        for (int i = 0, size = eSheets.size(); i < size; i++) {
            eSheet = eSheets.get(i);
            sheetName = eSheet.elementText("name");
            if (StringUtils.isBlank(sheetName)) {
                throw new IllegalReportXmlFileException("the name of the sheet index of " + (i + 1) + " is null");
            }
            rSheet = new SheetProperty();
            rSheet.setName(sheetName);

            List<TargetProperty> targets = getTargets(eSheet, sheetName);
            rSheet.setTargets(targets);

            sheetList.add(rSheet);
        }

        workbook.setSheets(sheetList);

        excel.setWorkbook(workbook);
        excel.setTemplate(template);

        return excel;
    }

    @SuppressWarnings("unchecked")
    private List<TargetProperty> getTargets(Element sheet, String sheetName)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<Element> eTargetsList = sheet.elements(Constant.TARGETS);
        if (null == eTargetsList || eTargetsList.size() == 0) {
            throw new IllegalReportXmlFileException("the " + sheetName + "must contain " + XmlFileValidator.ONE
                    + " " + Constant.TARGETS + " node");
        }
        if (eTargetsList.size() > 1) {
            throw new IllegalReportXmlFileException("the " + sheetName + "must contain "
                    + XmlFileValidator.AT_MOST_ONE + " " + Constant.TARGETS + " node");
        }
        Element eTargets = eTargetsList.get(0);
        List<Element> eTargetList = eTargets.elements(Constant.TARGET);
        if (null == eTargetList || eTargetList.size() == 0) {
            throw new IllegalReportXmlFileException("the " + Constant.TARGETS + " node of the " + sheetName
                    + " must contain " + XmlFileValidator.AT_LEAST_ONE + " " + Constant.TARGET + " node");
        }

        List<TargetProperty> targets = new ArrayList<>();
        TargetProperty target;
        Map<String, Class<?>> attrMap = TargetProperty.getAttrMap();

        String attrKey;
        String attrValue;
        Class<?> attrType;
        Element eTarget;
        for (int i = 1, size = eTargetList.size(); i <= size; i++) {
            eTarget = eTargetList.get(i - 1);
            target = new TargetProperty();
            for (Map.Entry<String, Class<?>> entry : attrMap.entrySet()) {
                attrKey = entry.getKey();
                attrType = entry.getValue();
                attrValue = eTarget.elementText(attrKey);

                setTargetValue(target, attrType, attrKey, attrValue.trim());
            }

            // 验证属性值是否合法
            TargetValidator.verify(target, i, sheetName);

            targets.add(target);
        }

        return targets;
    }

    private void setTargetValue(TargetProperty target, Class<?> attrType, String attrKey, String attrValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(attrKey);
        f.setAccessible(true);
        f.set(target, attrValue);
    }

}
