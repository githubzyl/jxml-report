package top.zylsite.jxml.report.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 解析xml的工具类
 *
 * @author: zhaoyl
 * @since: 2017年9月13日 下午3:59:56
 * @history:
 */
public class XmlUtil {

    public static Element getRootNode(String filePath) throws FileNotFoundException, DocumentException {
        return getRootNode(new FileInputStream(filePath));
    }

    public static Element getRootNode(File file) throws FileNotFoundException, DocumentException {
        return getRootNode(new FileInputStream(file));
    }

    public static Element getRootNode(InputStream inputStream) throws DocumentException {
        return getDocument(inputStream).getRootElement();
    }

    public static Document getDocument(File file) throws DocumentException {
        return getSaxReader().read(file);
    }

    public static Document getDocument(String filePath) throws DocumentException {
        return getSaxReader().read(filePath);
    }

    public static Document getDocument(InputStream inputStream) throws DocumentException {
        return getSaxReader().read(inputStream);
    }

    public static void writeXml(String filePath, Document doc, String encode) throws IOException {
        writeXml(new File(filePath), doc, encode);
    }

    public static void writeXml(File file, Document doc, String encode) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        writeXml(new FileOutputStream(file), doc, encode);
    }

    public static void writeXml(FileOutputStream out, Document doc, String encode) throws IOException {
        XMLWriter writer = null;
        try {
            // 指定文本的写出的格式：漂亮格式：有空格换行
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(encode);
            // 1.创建写出对象
            writer = new XMLWriter(out, format);
            // 2.写出Document对象
            writer.write(doc);
        } finally {
            writer.close();
            out.flush();
            out.close();
        }
    }

    private static SAXReader getSaxReader() {
        return new SAXReader();
    }

}
