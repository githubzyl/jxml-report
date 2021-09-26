package top.zylsite.jxml.report.common;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * javascript脚本解析器
 *
 * @author zhaoyl
 * @date 2021/09/26 10:25
 **/
public class JavaScriptEngine {

    private final static ThreadLocal<ScriptEngine> THREAD_LOCAL = new ThreadLocal<>();

    private final static Object OBJECT = new Object();

    private final static String JAVA_SCRIPT = "javascript";

    private final static String JS_FUNCTION_FILE = "config/function.js";

    private static String js = "";

    public static Object resolve(String script) throws ScriptException {
        return getJavaScriptEngine().eval(js + script);
    }

    private static ScriptEngine getJavaScriptEngine() {
        ScriptEngine engine = THREAD_LOCAL.get();
        if (null == engine) {
            synchronized (OBJECT) {
                if (null == engine) {
                    engine = new ScriptEngineManager().getEngineByName(JAVA_SCRIPT);
                    THREAD_LOCAL.set(engine);
                    js = getJavaScriptFunction();
                }
            }
        }
        return engine;
    }

    /**
     * 读取js中的方法
     *
     * @return java.lang.String
     * @author zhaoyl
     * @date 2021/09/26 13:54
     */
    private static String getJavaScriptFunction() {
        InputStream inputStream = ResourceReader.getResourceAsInputStream(JS_FUNCTION_FILE);
        return getStreamString(inputStream);
    }

    public static String getStreamString(InputStream tInputStream) {
        String str = "";
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                str = new String(tStringBuffer.toString().getBytes(), "UTF-8");
            } catch (Exception ex) {
                str = "";
            }
        }
        return str;
    }

}
