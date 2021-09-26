package top.zylsite.jxml.report.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import top.zylsite.jxml.report.common.Constant;

/**
 * 自定义过滤器,用来解析过滤器
 *
 * @author zhaoyl
 * @date 2021/09/26 10:30
 **/
public class CustomFilter {

    public static JSONArray filterData(JSONArray jsonArray, String filter) {
        if (StringUtils.isNotBlank(filter) && null != jsonArray && jsonArray.size() > 1) {
            filter = filter.trim();
            String fieldName = filter.split(Constant.EQ)[0].trim();
            String value = filter.split(Constant.EQ)[1].trim();
            String fieldValue;
            JSONArray newArray = new JSONArray();
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (null != jsonObject.get(fieldName)) {
                    fieldValue = jsonObject.get(fieldName).toString();
                    if (value.equals(fieldValue)) {
                        newArray.add(jsonObject);
                    }
                }
            }
            return newArray;
        }
        return jsonArray;
    }

}
