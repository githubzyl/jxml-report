package top.zylsite.jxml.report.utils;

import java.util.regex.Pattern;

public class PatternUtil {

	public static boolean matchPattern(String value, String reg) {
		Pattern p = Pattern.compile(reg);
		if (!p.matcher(value).matches()) {
			return false;
		}
		return true;
	}
	
}
