package top.zylsite.jxml.report.common;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import top.zylsite.jxml.report.utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取资源配置文件
 *
 * @author c
 * @date 2021/09/26 10:26
 **/
public class ResourceReader {

    private static final Logger logger = LoggerUtil.getLogger(ResourceReader.class);

    /**
     * 读取Properties配置文件转换成map
     *
     * @param propPath
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author zhaoyl
     * @date 2021/09/26 10:26
     */
    public static Map<String, String> readProperties(String propPath) {
        InputStream is = null;
        Map<String, String> dataMap = new HashMap<>(16);
        try {
            //加载配置文件
            is = getResourceAsInputStream(propPath);
            if (null == is) {
                return dataMap;
            }
            //读取配置文件
            Properties prop = new Properties();
            prop.load(is);

            Enumeration<String> en = (Enumeration<String>) prop.propertyNames();
            String key;
            while (en.hasMoreElements()) {
                key = en.nextElement();
                logger.info(key + "=" + prop.getProperty(key));
                dataMap.put(key, prop.getProperty(key));
            }
            return dataMap;
        } catch (IOException e) {
            logger.error(propPath + " 文件未找到");
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataMap;
    }

    /**
     * 将读取的资源文件转换成输入流
     *
     * @param resourcePath
     * @return java.io.InputStream
     * @author zhaoyl
     * @date 2021/09/26 10:26
     */
    public static InputStream getResourceAsInputStream(String resourcePath) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:" + resourcePath);
            if (null != resources && resources.length > 0) {
                return resources[0].getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getResourceAsFile(String resourcePath) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:" + resourcePath);
            if (null != resources && resources.length > 0) {
                return resources[0].getFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
