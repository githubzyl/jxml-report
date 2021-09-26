package top.zylsite.jxml.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import top.zylsite.jxml.report.common.Constant;
import top.zylsite.jxml.report.common.JavaScriptEngine;
import top.zylsite.jxml.report.common.ResourceReader;
import top.zylsite.jxml.report.core.XmlResolver;
import top.zylsite.jxml.report.property.ExcelProperty;
import top.zylsite.jxml.report.utils.EncdDecd;
import top.zylsite.jxml.report.utils.ExcelUtil;
import top.zylsite.jxml.report.utils.LoggerUtil;
import top.zylsite.jxml.report.utils.XmlUtil;
import top.zylsite.jxml.report.validator.XmlFileValidator;

public class TestReader {

	private final Logger logger = LoggerUtil.getLogger(TestReader.class);

	@Test
	public void readXml() {
		try {
			File file = ResourceReader.getResourceAsFile("templates/report_test.xml");
			XmlFileValidator.verify(file);
			ExcelProperty excel = XmlResolver.getInstance().getExcelReport(file);
			System.out.println(excel);
		} catch (Exception e) {
			logger.error("文件解析异常:", e);
		}

	}

	@Test
	public void testBase64() throws DocumentException, IOException {
		File excel = new File("F:/ismapp/test.xlsx");

		File file = ResourceReader.getResourceAsFile("templates/report_test.xml");
		Document document = XmlUtil.getDocument(file);
		String content = EncdDecd.encodeBase64(excel);
		Element root = document.getRootElement();
		Element template = root.element(Constant.TEMPLATE);
		Element t_content = template.element("content");

		logger.info(t_content.elementText("content"));

		t_content.setText(content);

		file = new File("F:/ismapp/report_test.xml");
		XmlUtil.writeXml(file, document, "UTF-8");
		EncdDecd.decodeBase64(content, "F:/ismapp/report_test.xlsx");

	}

	@Test
	public void testWriteExcel() throws Exception {
		JSONObject excelData = getTestData();
		System.out.println(excelData.toString());
		
//		File xmlFile = new File("F:/ismapp/report_test.xml");
//		ExcelReport report = new ExcelReport(excelData, xmlFile);
//		File file = report.generateReport();
//		System.out.println(file.getAbsolutePath());
	}

	@Test
	public void testWriteExcel2() throws Exception {
		// testBase64();
		// testWriteExcel();
		File outExcelFile = new File("F:/ismapp/report_test.xlsx");
		Workbook workbook = ExcelUtil.getWorkBook(outExcelFile);

		Sheet sheet = workbook.getSheet("sheet1");
		System.out.println(sheet.getLastRowNum());

		sheet.shiftRows(11, sheet.getLastRowNum(), 5, true, false);

		int row = 11;
		int col = 2;
		int shiftRow = 13;
		for (int i = row; i < row + 4; i++) {// 共5行
			sheet.shiftRows(row - 1, shiftRow, 1, true, false);
			for (int j = col; j < 5; j++) {
				ExcelUtil.setCreateCellValue(workbook, sheet, i, j, "测试数据" + (i + j));
			}
		}

		// 写入excel文件
		ExcelUtil.writeDataToExcel(workbook, outExcelFile);
	}

	private JSONObject getTestData() {
		JSONObject root = new JSONObject();

		List<Map<String, Object>> dataList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("本月基金数量", 1245);
		dataList.add(map);
		root.put("data1", JSONObject.toJSON(dataList));

		dataList = new ArrayList<>();
		map = new HashMap<>();
		map.put("上月基金数量", 1032);
		dataList.add(map);
		root.put("data2", JSONObject.toJSON(dataList));

		dataList = new ArrayList<>();
		int time = 20170915;
		for (int i = 0; i < 5; i++) {
			map = new HashMap<>();
			map.put("报表日期", getDate(time));
			time = time - 20000 * i;
			map.put("成立日期", getDate(time));
			time = 20170915;
			time = time - i;
			map.put("净值最新日期", getDate(time));
			dataList.add(map);
			time = 20170915;
		}
		root.put("data3", JSONObject.toJSON(dataList));

		dataList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			map = new HashMap<>();
			map.put("基金名称", "基金" + i);
			map.put("基金经理", "经理" + i);
			map.put("管理人", "管理人" + i);
			dataList.add(map);
		}
		root.put("data4", JSONObject.toJSON(dataList));

		return root;
	}

	private String getDate(int time) {
		String str = String.valueOf(time);
		String year = str.substring(0, 4);
		String month = str.substring(4, 6);
		String day = str.substring(6, 8);
		return year + "-" + month + "-" + day;
	}

	/**
	 * JS计算
	 * 
	 * @param script
	 * @return
	 * @throws ScriptException
	 */
	@Test
	public void jsCalculate() throws ScriptException {
		String script = "concat('1','2')";
		Object obj = JavaScriptEngine.resolve(script);
		System.out.println(obj);
	}

	@Test
	public void testInsertRow() throws Exception {
		// TODO code application logic here
		FileInputStream myxls = new FileInputStream("F:/ismapp/workbook.xlsx");
		Workbook wb = new XSSFWorkbook(myxls);
		Sheet sheet = wb.getSheetAt(0);

		sheet.shiftRows(2, 2, 1);

		FileOutputStream myxlsout = new FileOutputStream("F:/ismapp/workbook.xlsx");
		wb.write(myxlsout);
		myxlsout.close();

	}

	@Test
	public void testField() {
		String f = "concat(\"本月基金数量\"-\"b\"+\"c\",constStr(元))";
		String f1 = "concat(\"本月基金数量\"-\"b\"+\"c\",constStr(元))";
		System.out.println("替换前的字符串：" + f);
		String currStr = "";
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < f.length(); i++) {
			currStr = String.valueOf(f.charAt(i));
			if ("\"".equals(currStr)) {
				list.add(i);
			}
		}
		for (int j = 0; j < list.size(); j += 2) {
			String s1 = f.substring(list.get(j), list.get(j + 1) + 1);
			String s2 = f.substring(list.get(j) + 1, list.get(j + 1));
			f1 = f1.replace(s1, "getValue(" + s2 + ")");
		}
		System.out.println("替换后的字符串:" + f1);
	}
	
}
