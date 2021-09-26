package top.zylsite.jxml.report.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性值校验
 *
 * @author zhaoyl
 * @date 2021/09/26 10:23
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidField {

    boolean required() default true;

    String pattern() default "";

    String patternPromptMsg() default "";

    String enumClassName() default "";

}
