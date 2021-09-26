package top.zylsite.jxml.report.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import top.zylsite.jxml.report.exception.IllegalExcelFileException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Excel工具类
 *
 * @author zhaoyl
 * @date 2021/09/26 13:03
 **/
public class ExcelUtil {

    private final static String XLS = ".xls";
    private final static String XLSX = ".xlsx";

    /**
     * 获取总列数
     *
     * @param startCol
     * @param endCol
     * @return int
     * @author zhaoyl
     * @date 2021/09/26 13:03
     */
    public static int getColCount(String startCol, String endCol) {
        int startColIndex = excelColStrToNum(startCol);
        int endColIndex = excelColStrToNum(endCol);
        return Math.abs(endColIndex - startColIndex) + 1;
    }

    /**
     * 获取总行数
     *
     * @param startRow
     * @param endRow
     * @return int
     * @author zhaoyl
     * @date 2021/09/26 13:03
     */
    public static int getRowCount(String startRow, String endRow) {
        int startRowIndex = Integer.parseInt(startRow);
        int endRowIndex = Integer.parseInt(endRow);
        return Math.abs(endRowIndex - startRowIndex) + 1;
    }

    /**
     * 获取工作簿
     *
     * @param filePath
     * @return org.apache.poi.ss.usermodel.Workbook
     * @author zhaoyl
     * @date 2021/09/26 13:04
     */
    public static Workbook getWorkBook(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis;
        if (file.exists()) {
            fis = new FileInputStream(file);
            // 2003版本的excel，用.xls结尾
            if (filePath.endsWith(XLS)) {
                return new HSSFWorkbook(fis);
            }
            // 2007版本的excel，用.xlsx结尾
            else if (filePath.endsWith(XLSX)) {
                return new XSSFWorkbook(fis);
            } else {
                throw new IllegalExcelFileException("this is a illegal excel file");
            }
        }
        throw new FileNotFoundException("the file [" + filePath + "] not be found");
    }

    /**
     * 获取工作簿
     *
     * @param excelFile
     * @return org.apache.poi.ss.usermodel.Workbook
     * @author zhaoyl
     * @date 2021/09/26 13:04
     */
    public static Workbook getWorkBook(File excelFile) throws IOException {
        if (excelFile.exists()) {
            String filePath = excelFile.getAbsolutePath();
            return getWorkBook(filePath);
        }
        return null;
    }

    /**
     * 将数据写入excel
     *
     * @param workbook
     * @param filePath
     * @author zhaoyl
     * @date 2021/09/26 13:04
     */
    public static void writeDataToExcel(Workbook workbook, String filePath) {
        writeDataToExcel(workbook, new File(filePath));
    }

    /**
     * 将数据写入excel
     *
     * @param workbook
     * @param excelFile
     * @author zhaoyl
     * @date 2021/09/26 13:04
     */
    public static void writeDataToExcel(Workbook workbook, File excelFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件直接写入流
     *
     * @param workbook
     * @param outputStream
     * @author zhaoyl
     * @date 2021/09/26 13:05
     */
    public static void writeDataToExcel(Workbook workbook, OutputStream outputStream) {
        try {
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置cell的值
     *
     * @param workbook
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @param value
     * @author zhaoyl
     * @date 2021/09/26 13:05
     */
    public static void setCellValue(Workbook workbook, Sheet sheet, int rowIndex, int colIndex, Object value) {
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (null == cell) {
            cell = row.createCell(colIndex);
        }
        if (null == value) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof Number) {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.parseDouble(value.toString()));
        } else {
            cell.setCellValue(value.toString());
        }
        //cell.setCellStyle(getCellStyle(workbook));
    }

    /**
     * 获取单元格值
     *
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @return java.lang.Object
     * @author zhaoyl
     * @date 2021/09/26 13:05
     */
    public static Object getCellValue(Sheet sheet, int rowIndex, int colIndex) {
        if (null != sheet) {
            Row row = sheet.getRow(rowIndex);
            if (null != row) {
                Cell cell = row.getCell(colIndex);
                return getCellValue(cell, null);
            }
            return null;
        }
        return null;
    }

    /**
     * 获取单元格值
     *
     * @param cell
     * @param pattern
     * @return java.lang.Object
     * @author zhaoyl
     * @date 2021/09/26 13:06
     */
    public static Object getCellValue(Cell cell, String pattern) {
        if (null != cell) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                // 数值
                case Cell.CELL_TYPE_NUMERIC:
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    int cYear = Integer.parseInt(sdf.format(date).substring(0, 4));
                    if (1900 >= cYear) {
                        return cell.getNumericCellValue();
                    }
                    short format = cell.getCellStyle().getDataFormat();
                    boolean flag = format == 14 || format == 31 || format == 57 || format == 58 || (176 <= format && format <= 178)
                            || (182 <= format && format <= 196) || (210 <= format && format <= 213) || (208 == format);
                    // 日期
                    if (flag) {
                        sdf = new SimpleDateFormat(pattern);
                    } else { // 不是日期格式
                        return cell.getNumericCellValue();
                    }
                    if (date == null || "".equals(date)) {
                        return cell.getNumericCellValue();
                    }
                    try {
                        return sdf.format(date);
                    } catch (Exception e) {
                        return cell.getNumericCellValue();
                    }
                case Cell.CELL_TYPE_BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                // 公式
                case Cell.CELL_TYPE_FORMULA:
                    return String.valueOf(cell.getNumericCellValue());
                // 空值
                case Cell.CELL_TYPE_BLANK:
                    return null;
                // 故障
                case Cell.CELL_TYPE_ERROR:
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    public static void setCreateCellValue(Workbook workbook, Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle(workbook));
    }

    public static CellStyle getCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        //水平对齐方式
        // style.setAlignment(CellStyle.ALIGN_RIGHT);
        // 垂直对齐方式
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        return style;
    }

    public static int excelColStrToNum(String colStr) {
        int num = 0;
        int result = 0;
        int length = colStr.length();
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }

}
