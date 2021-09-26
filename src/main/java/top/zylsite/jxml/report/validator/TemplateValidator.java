package top.zylsite.jxml.report.validator;

import org.apache.commons.lang3.StringUtils;
import top.zylsite.jxml.report.annotation.ValidField;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.exception.IllegalReportXmlFileException;
import top.zylsite.jxml.report.property.TemplateProperty;
import top.zylsite.jxml.report.utils.PatternUtil;

import java.lang.reflect.Field;

/**
 * 验证指标中的属性值是否合法
 *
 * @author zhaoyl
 * @date 2021/09/26 13:34
 **/
public class TemplateValidator {

    public static void verify(TemplateProperty template) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = template.getClass().getDeclaredFields();
        boolean flag;
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(template);

            if (field.isAnnotationPresent(ValidField.class)) {
                ValidField validField = field.getAnnotation(ValidField.class);
                // 必填
                flag = validField.required() && (null == fieldValue || Constant.BLANK_STR.equals(fieldValue));
                if (flag) {
                    throwNullPointException(fieldName);
                }
                if (StringUtils.isNotBlank(validField.pattern())
                        && !PatternUtil.matchPattern(fieldValue.toString(), validField.pattern())) {
                    throwRegularMatchException(fieldName, validField.patternPromptMsg());
                }
            }

        }
    }

    private static void throwNullPointException(String attr) {
        throw new IllegalReportXmlFileException("the " + attr + " of the template is null");
    }

    private static void throwRegularMatchException(String attr, String prompt) {
        throw new IllegalReportXmlFileException("the " + attr + " of the template " + prompt);
    }

}
