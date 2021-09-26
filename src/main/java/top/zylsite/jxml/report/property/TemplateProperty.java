package top.zylsite.jxml.report.property;

import top.zylsite.jxml.report.annotation.ValidField;

import java.io.Serializable;

/**
 * 模板节点
 *
 * @author zhaoyl
 * @date 2021/09/26 13:25
 **/
public class TemplateProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板文件的名称
     */
    private String name;
    /**
     * 模板文件的后缀,如：.xls/.xlsx
     */
    @ValidField(pattern = ".(xls|xlsx)", patternPromptMsg = "only support .xls or .xlsx")
    private String suffix;
    /**
     * 模板文件转换成的base64内容
     */
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TemplateProperty [name=" + name + ", suffix=" + suffix + ", content=" + content + "]";
    }

}
