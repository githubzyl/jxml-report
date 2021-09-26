package top.zylsite.jxml.report.property;

import com.alibaba.fastjson.JSONArray;

/**
 * 带有数据的目标
 *
 * @author zhaoyl
 * @date 2021/09/26 13:17
 **/
public class TargetPropertyWithData extends TargetProperty {

    private static final long serialVersionUID = 1L;

    /**
     * 数据集
     */
    private JSONArray data;

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

}
