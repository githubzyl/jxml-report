package top.zylsite.jxml.report.core;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

/**
 * xml report
 *
 * @author zhaoyl
 * @date 2021/09/26 10:28
 **/
public abstract class AbstractXmlReport implements Report {

    protected JSONObject excelData;
    protected File xmlFile;

    public AbstractXmlReport(JSONObject excelData, File xmlFile) {
        super();
        this.excelData = excelData;
        this.xmlFile = xmlFile;
    }

}
