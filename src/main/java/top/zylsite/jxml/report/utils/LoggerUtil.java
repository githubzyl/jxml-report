package top.zylsite.jxml.report.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtil {

	public static Logger getLogger(Class<?> clazz){
		return LoggerFactory.getLogger(clazz);
	}
	
	
}
