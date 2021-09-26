package top.zylsite.jxml.report.validator;

import org.apache.commons.lang3.StringUtils;
import top.zylsite.jxml.report.annotation.ValidField;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.enums.ExcelWriteDirection;
import top.zylsite.jxml.report.enums.ExcelWriteWay;
import top.zylsite.jxml.report.exception.IllegalReportXmlFileException;
import top.zylsite.jxml.report.property.TargetProperty;
import top.zylsite.jxml.report.utils.ExcelUtil;
import top.zylsite.jxml.report.utils.PatternUtil;

import java.lang.reflect.Field;

/**
 * 验证指标中的属性值是否合法
 *
 * @author: zhaoyl
 * @since: 2017年9月14日 上午9:58:52
 * @history:
 */
public class TargetValidator {

    public static void verify(TargetProperty target, int targetIndex, String sheetName)
            throws IllegalArgumentException, IllegalAccessException {
        // 验证单个字段值的合法性
        verifySingleField(target, targetIndex, sheetName);
        // 验证字段组合值的合法性
        verifyCombField(target, targetIndex, sheetName);
    }

    private static void verifySingleField(TargetProperty target, int targetIndex, String sheetName)
            throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = target.getClass().getDeclaredFields();
        boolean flag;
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(target);

            if (field.isAnnotationPresent(ValidField.class)) {
                ValidField validField = field.getAnnotation(ValidField.class);
                // 必填
                flag = validField.required() && (null == fieldValue || Constant.BLANK_STR.equals(fieldValue));
                if (flag) {
                    throwNullPointException(fieldName, targetIndex, sheetName);
                }
                if (null != fieldValue && !Constant.BLANK_STR.equals(fieldValue)) {
                    if (StringUtils.isNotBlank(validField.pattern())
                            && !PatternUtil.matchPattern(fieldValue.toString(), validField.pattern())) {
                        throwRegularMatchException(fieldName, targetIndex, sheetName, validField.patternPromptMsg());
                    }
                }
                if (StringUtils.isNotBlank(validField.enumClassName())) {
                    if (ExcelWriteWay.class.getName().equals(validField.enumClassName())) {
                        if (!ExcelWriteWay.contain(fieldValue.toString())) {
                            throwOptionalValueException(fieldName, targetIndex, sheetName, ExcelWriteWay.getCustomValues());
                        }
                    }
                    if (ExcelWriteDirection.class.getName().equals(validField.enumClassName())) {
                        if (!ExcelWriteDirection.contain(fieldValue.toString())) {
                            throwOptionalValueException(fieldName, targetIndex, sheetName,
                                    ExcelWriteDirection.getCustomValues());
                        }
                    }
                }
            }

        }

    }

    /**
     * 验证组合值
     *
     * @param target
     * @param targetIndex
     * @param sheetName
     * @author zhaoyl
     * @date 2021/09/26 13:54
     */
    private static void verifyCombField(TargetProperty target, int targetIndex, String sheetName) {
        String startColIndex = target.getStartColIndex();
        String endColIndex = target.getEndColIndex();
        String startRowIndex = target.getStartRowIndex();
        String endRowIndex = target.getEndRowIndex();
        // 获取总字段数
        int fieldCount = target.getFieldNames().split(Constant.FIELD_NAME_SEPARATOR).length;

        if (StringUtils.isNotBlank(target.getCellList())) {
            int cellListSize = target.getCellList().split(",").length;
            if (cellListSize != fieldCount) {
                throw new IllegalReportXmlFileException(
                        "the size of the cellList must be equal with the count of the fieldNames"
                                + promptSuffix(targetIndex, sheetName));
            }
            return;
        }
        // 单个单元格写入
        if (ExcelWriteWay.SINGLE_CELL.getCode().equals(target.getWriteWay())) {
            if (!startColIndex.equals(endColIndex) || !startRowIndex.equals(endRowIndex)) {
                throw new IllegalReportXmlFileException(
                        "the startColIndex must be equal with the endColIndex and the startRowIndex must be equal with the endRowIndex where the writeway is 1"
                                + promptSuffix(targetIndex, sheetName));
            }
            if (1 != fieldCount) {
                throw new IllegalReportXmlFileException(
                        "the count of the fieldNames after being split with \"" + Constant.FIELD_NAME_SEPARATOR
                                + "\" must be one where the writeway is 1" + promptSuffix(targetIndex, sheetName));
            }
        }
        // 纵向
        else {
            if (ExcelWriteDirection.HORIZONTAL.getCode().equals(target.getWriteDirection())) {
                int rowCount = ExcelUtil.getRowCount(startRowIndex, endRowIndex);
                if (rowCount != fieldCount) {
                    throw new IllegalReportXmlFileException("the count of the fieldNames after being split with \""
                            + Constant.FIELD_NAME_SEPARATOR + "\" must be equal with the total row count"
                            + promptSuffix(targetIndex, sheetName));
                }
            } else {
                // 获取总列数
                int colCount = ExcelUtil.getColCount(startColIndex, endColIndex);
                if (colCount != fieldCount) {
                    throw new IllegalReportXmlFileException("the count of the fieldNames after being split with \""
                            + Constant.FIELD_NAME_SEPARATOR + "\" must be equal with the total column count"
                            + promptSuffix(targetIndex, sheetName));
                }
            }
        }

    }

    private static void throwNullPointException(String attr, int targetIndex, String sheetName) {
        throw new IllegalReportXmlFileException(promptPrefix(attr, targetIndex, sheetName) + " is null");
    }

    private static void throwOptionalValueException(String attr, int targetIndex, String sheetName, String prompt) {
        throw new IllegalReportXmlFileException(promptPrefix(attr, targetIndex, sheetName) + " must be in " + prompt);
    }

    private static void throwRegularMatchException(String attr, int targetIndex, String sheetName, String prompt) {
        throw new IllegalReportXmlFileException(promptPrefix(attr, targetIndex, sheetName) + " must be " + prompt);
    }

    private static String promptPrefix(String attr, int targetIndex, String sheetName) {
        return "the value of [" + attr + "]" + promptSuffix(targetIndex, sheetName);
    }

    private static String promptSuffix(int targetIndex, String sheetName) {
        return " in the first " + targetIndex + " " + Constant.TARGET + " node of the " + sheetName;
    }

}
