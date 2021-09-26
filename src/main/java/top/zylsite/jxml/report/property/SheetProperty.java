package top.zylsite.jxml.report.property;

import java.io.Serializable;
import java.util.List;

/**
 * 工作簿中的sheet节点，代表excel中一个个的sheet
 *
 * @author zhaoyl
 * @date 2021/09/26 13:11
 **/
public class SheetProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * sheet的名称
     */
    private String name;
    /**
     * sheet需要填充的指标集合
     */
    private List<TargetProperty> targets;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TargetProperty> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetProperty> targets) {
        this.targets = targets;
    }

    @Override
    public String toString() {
        return "SheetProperty [name=" + name + ", targets=" + targets + "]";
    }

}
