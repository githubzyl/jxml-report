package top.zylsite.jxml.report.property;

import java.io.Serializable;

/**
 * report.xml根节点
 *
 * @author zhaoyl
 * @date 2021/09/26 13:10
 **/
public class ExcelProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作簿
     */
    private WorkbookProperty workbook;
    /**
     * excel文件模板
     */
    private TemplateProperty template;

    public WorkbookProperty getWorkbook() {
        return workbook;
    }

    public void setWorkbook(WorkbookProperty workbook) {
        this.workbook = workbook;
    }

    public TemplateProperty getTemplate() {
        return template;
    }

    public void setTemplate(TemplateProperty template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "ExcelProperty [workbook=" + workbook + ", template=" + template + "]";
    }

}
