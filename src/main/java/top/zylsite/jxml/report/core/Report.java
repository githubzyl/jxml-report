package top.zylsite.jxml.report.core;

import java.io.File;

/**
 * 报表接口
 *
 * @author zhaoyl
 * @date 2021/09/26 10:39
 **/
public interface Report {

    /**
     * 根据文件生成报表文件
     *
     * @param reportFile
     * @return java.io.File
     * @throws Exception
     * @author zhaoyl
     * @date 2021/09/26 10:38
     */
    File generateReport(File reportFile) throws Exception;

}
