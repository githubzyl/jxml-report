package top.zylsite.jxml.report.property;

import java.io.Serializable;
import java.util.List;

/**
 * 工作簿节点
 *
 * @author zhaoyl
 * @date 2021/09/26 13:24
 **/
public class WorkbookProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作簿中的sheet集合
     */
    private List<SheetProperty> sheets;

    public List<SheetProperty> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetProperty> sheets) {
        this.sheets = sheets;
    }

    @Override
    public String toString() {
        return "WorkbookProperty [sheets=" + sheets + "]";
    }

}
