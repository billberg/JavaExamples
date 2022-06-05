package com.github.billberg.utils;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;

public class ExcelUtil {
	private static Logger logger = Logger.getLogger(ExcelUtil.class);
	
	//三色阶阈值颜色3-Color Scale  绿-黄-红
	private static XSSFColor[] triColors = new XSSFColor[]{new XSSFColor(new Color(99,190,123)),new XSSFColor(new Color(255,235,132)),
			new XSSFColor(new Color(248,105,107))};
	//三色阶阈值颜色，实际使用5种颜色，不支持
	/*
	private static XSSFColor[] triColors = new XSSFColor[]{new XSSFColor(new Color(99,190,123)),new XSSFColor(new Color(177,212,127)),
			new XSSFColor(new Color(255,235,132)),new XSSFColor(new Color(252,170,120)),new XSSFColor(new Color(248,105,107)) };
	*/		
	
	public static void exportTables2Excel(String[] tableNames, List<Object[]>[] tableDatas, boolean includingHeader, boolean isRound, String outputFileName) throws IOException {
		exportTables2Excel(tableNames, tableDatas, includingHeader, isRound, 2, outputFileName);
	}
	
	public static void exportTables2Excel(String tableName, List<Object[]> tableData, boolean includingHeader, boolean isRound, String outputFileName) throws IOException {
		exportTables2Excel(new String[]{tableName}, new List[]{tableData}, includingHeader, isRound, 2, outputFileName);
	}
	
	/**
	 * 输出多表单<br>
	 * header的最后一个元素为样式集合{冻结窗格,列分组,突出显示的列,条件格式区域,条件规则阈值,突出显示的单元格,超链接}
	 * 
	 * @20180318: 设置单元格样式
	 * 
	 * @param tableNames
	 * @param tableDatas
	 * @param includingHeader
	 * @param isRound
	 * @param outputFileName
	 * @throws IOException
	 */
	public static void exportTables2Excel(String[] tableNames, List<Object[]>[] tableDatas, boolean includingHeader, boolean isRound, int roundScale, String outputFileName) throws IOException {

		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();  
		
		for (int t = 0; t < tableNames.length; t++) {
			Sheet sheet = wb.createSheet();
			//默认列宽设置为10个字符--适应日期格式
			sheet.setDefaultColumnWidth(10);
			wb.setSheetName(t, tableNames[t]);
			
			Iterator<Object[]> iter = tableDatas[t].iterator();
			//header
			//Object[] header = tableDatas[t].get(0);
			Object[] header = iter.next();
			
			//样式集合{冻结窗格,列分组,突出显示的列,条件格式区域,条件规则阈值,突出显示的单元格,超链接}
			boolean includingStyles = false; 
			Object[] styles = new Object[0];
			if (includingHeader) {
				styles = (Object[])header[header.length-1];
				if (styles.length > 0) {
					includingStyles = true;
				}
			}
			
			//突出显示列样式
			CellStyle highlightStyle = wb.createCellStyle();
			highlightStyle.setBorderLeft(BorderStyle.MEDIUM);
			highlightStyle.setBorderRight(BorderStyle.THIN);
			highlightStyle.setLeftBorderColor(IndexedColors.LIGHT_BLUE.index);
			highlightStyle.setRightBorderColor(IndexedColors.LIGHT_BLUE.index);
			//设置字体
			XSSFFont hightlightFont = (XSSFFont)wb.createFont();
			//hightlightFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			hightlightFont.setBold(true);
			hightlightFont.setFontHeightInPoints((short) 12);  
			highlightStyle.setFont(hightlightFont);
			
			
			//突出显示单元格样式
			CellStyle highlightCellStyle = wb.createCellStyle();
			highlightCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			highlightCellStyle.setFillForegroundColor(IndexedColors.GOLD.index);
			
			if (includingStyles && styles[0] != null) {
				//冻结窗口
				int[] freezePane = (int[])styles[0];
				
				//sheet.createFreezePane(5,1,5,1);
				sheet.createFreezePane(freezePane[0],freezePane[1]);
			}
			
			if (includingStyles && styles[1] != null) {
				//列分组
				int[][] columnGroups = (int[][])styles[1];
				
				//列分组
				for (int i = 0; i < columnGroups.length; i++) {
					sheet.groupColumn(columnGroups[i][0], columnGroups[i][1]);
					//sheet.setColumnGroupCollapsed(columnNumber, collapsed);
				}
			
			}
			
			//行、列序序号都从0开始
			int rowNo = 0;
			//输出头部列名
			//突出显示的列
			//int[] highlightCols = new int[0];
			if (includingHeader) {
				//筛选
				sheet.setAutoFilter(new CellRangeAddress(0, tableDatas[t].size()-1, 0, header.length-2));
				
				//如果你需要使用换行符,你需要设置  
				//单元格的样式wrap=true,代码如下:  
				XSSFCellStyle cs = (XSSFCellStyle)wb.createCellStyle();  
				cs.setWrapText(true); 
				//cs.setFillForegroundColor(IndexedColors.BLUE_GREY.index);// 设置背景色
				cs.setFillForegroundColor(new XSSFColor(new Color(248,171,166)));//#f8aba6珊瑚色
				cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				//设置字体
				Font font = wb.createFont();
				//font.setColor(IndexedColors.WHITE.getIndex());
				font.setFontName("黑体");
				font.setFontHeightInPoints((short) 8);
				cs.setFont(font);
				
				Row r0 = sheet.createRow(rowNo++);
				//增加行的高度以适应2行文本的高度,设置高度单位(像素)  
				r0.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));  
				
				//Object[] header = iter.next();
				//注: header的最后一列为样式列表
				//highlightCols = (int[])header[header.length-1];
				for (int i = 0; i < header.length-1; i++) {
					//非String类型的header字段过滤，header尾部可添加扩展域留作他用
					if (header[i] instanceof String) {
						Cell c = r0.createCell(i);
						c.setCellValue((String)header[i]);
						c.setCellStyle(cs); 
					}
				}
			}
			
			//输出数据
			if (!iter.hasNext()) { //无数据
				continue;
			}
			
			//奇数行设置背景色
			XSSFCellStyle oddRowCellStyle = (XSSFCellStyle)wb.createCellStyle();
			oddRowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			oddRowCellStyle.setFillForegroundColor(new XSSFColor(new Color(242,234,218)));//#f2eada象牙色
			int cellNo = 0;
			while (iter.hasNext()) {
				Object[] dr = iter.next();
				
				Row r = sheet.createRow(rowNo++);

				for (int i = 0; i < dr.length; i++) {
					
					Cell c = r.createCell(i);
					
					//样式中包含超链接
					if (styles.length >= 7 && i == (int)styles[6]) {
						//int hlinkColumn = (int)styles[6];
						Hyperlink hlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
						hlink.setAddress("'"+tableNames[rowNo-1]+"'!A1");  
						c.setHyperlink(hlink);  
					    //c.setCellStyle(hlink_style);  
					}
				      
					//奇数行设置背景色
					/*
					if (rownum%2 == 1) {
						c.setCellStyle(oddRowCellStyle); 
					}*/
					
					//设置样式
					if (includingStyles && styles[2] != null) {
						//突出显示列
						int[] highlightCols = (int[])styles[2];
						
						if (Functions.in(i, highlightCols)) {
							c.setCellStyle(highlightStyle); 
						}
					}
					
					//设置单元格格式
					//单元格表示为表单中的序号
					if (includingStyles && styles[5] != null) {
						int[] highlightCells = (int[])styles[5];
						if (highlightCells[cellNo] == 1) {
							c.setCellStyle(highlightCellStyle);
						}
					}
					
					if (dr[i] instanceof String)  {
						c.setCellValue((String) dr[i]);
					} else if (dr[i] instanceof Character)  {
						c.setCellValue(dr[i].toString());
					} else if (dr[i] instanceof Date)  {
						CellStyle csDate = wb.createCellStyle();
						csDate.cloneStyleFrom(c.getCellStyle());
						DataFormat format = wb.createDataFormat();  
						//csDate.cloneStyleFrom(cs);
						//csDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy/mm/dd"));
						csDate.setDataFormat(format.getFormat("yyyy/MM/dd")); 
						c.setCellStyle(csDate);
						
						c.setCellValue((Date) dr[i]);
					} else if (dr[i] instanceof Double || dr[i] instanceof Float)  {
						//test
						//logger.info("******** "+Arrays.toString(dr));
						//logger.info("******** "+dr[i]);
						
						double d = ((Number)dr[i]).doubleValue();
						
						if (Double.isNaN(d)) {//缺失
							c.setCellValue("");
						} else {
							c.setCellValue((isRound)? Functions.round(d,roundScale) :d);
						}
					} else if (dr[i] instanceof Integer) {
						c.setCellValue(Double.valueOf(dr[i].toString()));
					}  else if(dr[i] != null && dr[i].getClass().isArray() ) {  
						   //如果是数组的话 然后进行操作  
						logger.info(JSON.toJSONString(dr[i]));
						c.setCellValue(Arrays.toString((double[])dr[i]));
					} else if(dr[i] instanceof List ) {  
						   //如果是List的话 然后进行操作  
						c.setCellValue(dr[i].toString());
					} 
					
					
					cellNo++;
				}
				
			}
			
			//条件格式
			if (includingStyles && styles[3] != null) {
				//三色阶格式化区域集合
				Object[] regionGroups =  (Object[])styles[3];
				//三色阶阈值集合
				Object[] thresholdGroups = null;
				if (styles[4] != null) {
					thresholdGroups =  (Object[])styles[4];
				}
				
				//TODO: 外部传入条件规则区域集合与三色阶阈值集合
				//条件规则
				SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
				for (int i = 0; i < regionGroups.length; i++) {
					int[][] ranges = (int[][])regionGroups[i];
					
					CellRangeAddress [] regions1 = new CellRangeAddress[ranges.length];
					for (int j = 0; j < ranges.length; j++) {
						regions1[j] = new CellRangeAddress(1, tableDatas[t].size()-1, ranges[j][0], ranges[j][1]);
					}
					 
					ConditionalFormattingRule cfRule = sheetCF.createConditionalFormattingColorScaleRule();
					ColorScaleFormatting clrFmt = cfRule.getColorScaleFormatting();
					//logger.info("debug: " + clrFmt.getNumControlPoints());
					//clrFmt.setNumControlPoints(5);
					//logger.info("debug: " + clrFmt.getNumControlPoints() + ", " + clrFmt.getThresholds().length);
					//clrFmt.getColors();
					clrFmt.setColors(triColors);
					
					if (thresholdGroups == null
						|| (thresholdGroups.length == regionGroups.length && thresholdGroups[i].equals(0)) ) { //默认以0作为中间值
						/*
						clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
						clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
						clrFmt.getThresholds()[1].setValue(25d);
						clrFmt.getThresholds()[2].setRangeType(RangeType.PERCENT);
						clrFmt.getThresholds()[2].setValue(50d);
						clrFmt.getThresholds()[3].setRangeType(RangeType.PERCENT);
						clrFmt.getThresholds()[3].setValue(75d);
					    clrFmt.getThresholds()[4].setRangeType(RangeType.MAX);
					    */
					   
						clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
						//clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
						//clrFmt.getThresholds()[1].setValue(50d);
						clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
					    clrFmt.getThresholds()[1].setValue(0d);
					    clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
					} else if (thresholdGroups.length == regionGroups.length && thresholdGroups[i].equals("P")) { //百分比
						clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
						clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
						clrFmt.getThresholds()[1].setValue(50d);
					    clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
					} else { //阈值
						double[] triThresholds = (double[])thresholdGroups[i];
						//clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
						clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
						clrFmt.getThresholds()[0].setValue(triThresholds[0]);
						//clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
					    clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
					    clrFmt.getThresholds()[1].setValue(triThresholds[1]);
					    //clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
					    clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
						clrFmt.getThresholds()[2].setValue(triThresholds[2]);
							
					} 
					sheetCF.addConditionalFormatting(regions1, cfRule);
				        
				}
				
			}
			
			/*
			ConditionalFormattingRule cfRule = sheetCF.createConditionalFormattingColorScaleRule();
			ColorScaleFormatting clrFmt = cfRule.getColorScaleFormatting();
			
			//clrFmt.getColors();
			clrFmt.setColors(triColors);
			//clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
			clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
			clrFmt.getThresholds()[0].setValue(0d);
			 //clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
	        clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
	        clrFmt.getThresholds()[1].setValue(0.4d);
	        //clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
	        clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
			clrFmt.getThresholds()[2].setValue(1d);
			
			//条件规则
			ConditionalFormattingRule cfRule2 = sheetCF.createConditionalFormattingColorScaleRule();
			ColorScaleFormatting clrFmt2 = cfRule2.getColorScaleFormatting();
			
			clrFmt2.setColors(triColors);
			clrFmt2.getThresholds()[0].setRangeType(RangeType.NUMBER);
			clrFmt2.getThresholds()[0].setValue(-30d);
			clrFmt2.getThresholds()[1].setRangeType(RangeType.NUMBER);
	        clrFmt2.getThresholds()[1].setValue(10d);
	        clrFmt2.getThresholds()[2].setRangeType(RangeType.NUMBER);
	        clrFmt2.getThresholds()[2].setValue(50d);
	        
	        //CellRangeAddress [] regions = { CellRangeAddress.valueOf("L:M") };
	        //CellRangeAddress [] regions = { CellRangeAddress.valueOf("L2:M27") };
	        //行列索引都是从0开始
	        CellRangeAddress [] regions1 = {new CellRangeAddress(1, tableDatas[t].size()-1, 11, 12),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 14, 15),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 17, 18),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 20, 21),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 23, 24)};
	        sheetCF.addConditionalFormatting(regions1, cfRule);
	        
	        CellRangeAddress [] regions2 = {new CellRangeAddress(1, tableDatas[t].size()-1, 37, 41),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 62, 101)};
	        sheetCF.addConditionalFormatting(regions2, cfRule2);
	        */
	        
		}

		//String dir = Config.config.getProperty("DiffWind.output") + DateUtil.yyyyMMdd(new Date()) + "/";
		//new File(dir).mkdirs();
		//FileOutputStream out = new FileOutputStream(dir + outputFileName);
		
		FileOutputStream out = new FileOutputStream(outputFileName);
		wb.write(out);
		wb.close();
		out.close();
	}
	
	/**
	 * V2 样式单独作为参数输入  TODO 未实现
	 * @param tableNames
	 * @param tableDatas
	 * @param includingHeader
	 * @param isRound
	 * @param outputFileName
	 * @throws IOException
	 */
	@Deprecated
	public static void exportTables2Excel(String[] tableNames, List<Object[]>[] tableDatas, boolean includingHeader, Object[] styles, boolean isRound, String outputFileName) throws IOException {
		exportTables2Excel(tableNames, tableDatas, includingHeader, styles, isRound, 2, outputFileName);
	}
	
	/**
	 * V2 样式单独作为参数输入 TODO 未实现
	 * TODO 自动对列数据进行条件格式（三色阶）处理
	 * @param tableNames
	 * @param tableDatas
	 * @param includingHeader
	 * @param styles
	 * @param isRound
	 * @param roundScale
	 * @param outputFileName
	 * @throws IOException
	 */
	@Deprecated
	public static void exportTables2Excel(String[] tableNames, List<Object[]>[] tableDatas, boolean includingHeader, Object[] styles, boolean isRound, int roundScale, String outputFileName) throws IOException {

		Workbook wb = new XSSFWorkbook();
		
		for (int t = 0; t < tableNames.length; t++) {
			Sheet sheet = wb.createSheet();
			//默认列宽设置为10个字符--适应日期格式
			sheet.setDefaultColumnWidth(10);
			wb.setSheetName(t, tableNames[t]);
			
			Iterator<Object[]> iter = tableDatas[t].iterator();
			//header
			//Object[] header = tableDatas[t].get(0);
			Object[] header = iter.next();
			
			//样式集合{冻结窗格,列分组,突出显示的列,条件格式区域,条件规则阈值,突出显示的单元格}
			boolean includingStyles = false; 
			if (styles != null && styles.length > 0) {
				includingStyles = true;
			}
			
			//突出显示列样式
			CellStyle highlightColumnStyle = wb.createCellStyle();
			highlightColumnStyle.setBorderLeft(BorderStyle.MEDIUM);
			highlightColumnStyle.setBorderRight(BorderStyle.THIN);
			highlightColumnStyle.setLeftBorderColor(IndexedColors.LIGHT_BLUE.index);
			highlightColumnStyle.setRightBorderColor(IndexedColors.LIGHT_BLUE.index);
			//设置字体
			XSSFFont hightlightFont = (XSSFFont)wb.createFont();
			//hightlightFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			hightlightFont.setBold(true);
			hightlightFont.setFontHeightInPoints((short) 12);  
			highlightColumnStyle.setFont(hightlightFont);
			
			
			//突出显示单元格样式
			CellStyle highlightCellStyle = wb.createCellStyle();
			highlightCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			highlightCellStyle.setFillForegroundColor(IndexedColors.GOLD.index);
			
			if (includingStyles && styles[0] != null) {
				//冻结窗口
				int[] freezePane = (int[])styles[0];
				
				//sheet.createFreezePane(5,1,5,1);
				sheet.createFreezePane(freezePane[0],freezePane[1]);
			}
			
			if (includingStyles && styles[1] != null) {
				//列分组
				int[][] columnGroups = (int[][])styles[1];
				
				//列分组
				for (int i = 0; i < columnGroups.length; i++) {
					sheet.groupColumn(columnGroups[i][0], columnGroups[i][1]);
					//sheet.setColumnGroupCollapsed(columnNumber, collapsed);
				}
			
			}
			
			//行、列序序号都从0开始
			int rownum = 0;
			//输出头部列名
			//突出显示的列
			//int[] highlightCols = new int[0];
			if (includingHeader) {
				//筛选
				sheet.setAutoFilter(new CellRangeAddress(0, tableDatas[t].size()-1, 0, header.length-1));
				
				//如果你需要使用换行符,你需要设置  
				//单元格的样式wrap=true,代码如下:  
				XSSFCellStyle cs = (XSSFCellStyle)wb.createCellStyle();  
				cs.setWrapText(true); 
				//cs.setFillForegroundColor(IndexedColors.BLUE_GREY.index);// 设置背景色
				cs.setFillForegroundColor(new XSSFColor(new Color(248,171,166)));//#f8aba6珊瑚色
				cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				//设置字体
				Font font = wb.createFont();
				//font.setColor(IndexedColors.WHITE.getIndex());
				font.setFontName("黑体");
				font.setFontHeightInPoints((short) 8);
				cs.setFont(font);
				
				Row r0 = sheet.createRow(rownum++);
				//增加行的高度以适应2行文本的高度,设置高度单位(像素)  
				r0.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));  
				
				for (int i = 0; i < header.length; i++) {
					Cell c = r0.createCell(i);
					c.setCellStyle(cs); 
					if (header[i] instanceof String)  {
						c.setCellValue((String)header[i]);
					} else if (header[i] instanceof Date)  {
						c.setCellValue(DateUtil.yyyyMMddSlash((Date)header[i]));
					} else {
						c.setCellValue(header[i].toString());
					}
					
					
				}
			}
			
			//输出数据
			if (!iter.hasNext()) { //无数据
				continue;
			}
			
			
			//奇数行设置背景色
			XSSFCellStyle oddRowCellStyle = (XSSFCellStyle)wb.createCellStyle();
			oddRowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			oddRowCellStyle.setFillForegroundColor(new XSSFColor(new Color(242,234,218)));//#cde6c7白绿色
			int cellNo = 0;
			while (iter.hasNext()) {
				Object[] dr = iter.next();
				
				Row r = sheet.createRow(rownum++);
				
				
				for (int i = 0; i < dr.length; i++) {
					
					Cell c = r.createCell(i);
					
					//奇数行设置背景色
					if (rownum%2 == 1) {
						c.setCellStyle(oddRowCellStyle); 
					}
					
					//设置样式
					if (includingStyles && styles[2] != null) {
						//突出显示列
						int[] highlightCols = (int[])styles[2];
						
						if (Functions.in(i, highlightCols)) {
							c.setCellStyle(highlightColumnStyle); 
						}
					}
					
					//设置单元格格式
					//单元格表示为表单中的序号
					if (includingStyles && styles[5] != null) {
						int[] highlightCells = (int[])styles[5];
						if (highlightCells[cellNo] == 1) {
							c.setCellStyle(highlightCellStyle);
						}
					}
					
					if (dr[i] instanceof String)  {
						c.setCellValue((String) dr[i]);
					} else if (dr[i] instanceof Character)  {
						c.setCellValue(dr[i].toString());
					} else if (dr[i] instanceof Date)  {
						CellStyle csDate = wb.createCellStyle();
						csDate.cloneStyleFrom(oddRowCellStyle);
						DataFormat format = wb.createDataFormat();  
						//csDate.cloneStyleFrom(cs);
						//csDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy/mm/dd"));
						csDate.setDataFormat(format.getFormat("yyyy/MM/dd")); 
						c.setCellStyle(csDate);
						
						c.setCellValue((Date) dr[i]);
					} else if (dr[i] instanceof Double || dr[i] instanceof Float)  {
						//test
						//logger.info("******** "+Arrays.toString(dr));
						//logger.info("******** "+dr[i]);
						
						double d = ((Number)dr[i]).doubleValue();
						
						if (Double.isNaN(d)) {//缺失
							c.setCellValue("");
						} else {
							c.setCellValue((isRound)? Functions.round(d,roundScale) :d);
						}
					} else if (dr[i] instanceof Integer) {
						c.setCellValue(Double.valueOf(dr[i].toString()));
					}  else if(dr[i] != null && dr[i].getClass().isArray() ) {  
						   //如果是数组的话 然后进行操作  
						logger.info(JSON.toJSONString(dr[i]));
						c.setCellValue(Arrays.toString((double[])dr[i]));
					} 
					
					
					cellNo++;
				}
				
			}
			
			
			
			
			//条件格式
			if (includingStyles && styles[3] != null && styles[4] != null) {
				//三色阶格式化区域集合
				//Object[] regionGroups = new Object[]{new int[][]{{11,12},{14,15},{17,18},{20,21},{23,24}}, new int[][]{{37,41},{62,101}}};
				Object[] regionGroups =  (Object[])styles[3];
				//三色阶阈值集合
				//Object[] thresholdGroups = new Object[]{new double[]{0d,0.4d,1d}, new double[]{-30d,10d,50d}};
				Object[] thresholdGroups =  (Object[])styles[4];
				
				//TODO: 外部传入条件规则区域集合与三色阶阈值集合
				//三色阶阈值颜色  3-color Scale
				XSSFColor[] triColors = new XSSFColor[]{new XSSFColor(new Color(98,162,56)),new XSSFColor(new Color(252,232,112)),new XSSFColor(new Color(230,104,38))};
				
				//条件规则
				SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
				for (int i = 0; i < regionGroups.length; i++) {
					
					int[][] ranges = (int[][])regionGroups[i];
					double[] triThresholds = (double[])thresholdGroups[i];
					
					CellRangeAddress [] regions1 = new CellRangeAddress[ranges.length];
					for (int j = 0; j < ranges.length; j++) {
						regions1[j] = new CellRangeAddress(1, tableDatas[t].size()-1, ranges[j][0], ranges[j][1]);
					}
					 
					ConditionalFormattingRule cfRule = sheetCF.createConditionalFormattingColorScaleRule();
					ColorScaleFormatting clrFmt = cfRule.getColorScaleFormatting();
						
						//clrFmt.getColors();
						clrFmt.setColors(triColors);
						//clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
						clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
						clrFmt.getThresholds()[0].setValue(triThresholds[0]);
						 //clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
				        clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
				        clrFmt.getThresholds()[1].setValue(triThresholds[1]);
				        //clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
				        clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
						clrFmt.getThresholds()[2].setValue(triThresholds[2]);
						
				        sheetCF.addConditionalFormatting(regions1, cfRule);
				        
				}
				
			}
			
			/*
			ConditionalFormattingRule cfRule = sheetCF.createConditionalFormattingColorScaleRule();
			ColorScaleFormatting clrFmt = cfRule.getColorScaleFormatting();
			
			//clrFmt.getColors();
			clrFmt.setColors(triColors);
			//clrFmt.getThresholds()[0].setRangeType(RangeType.MIN);
			clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
			clrFmt.getThresholds()[0].setValue(0d);
			 //clrFmt.getThresholds()[1].setRangeType(RangeType.PERCENT);
	        clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
	        clrFmt.getThresholds()[1].setValue(0.4d);
	        //clrFmt.getThresholds()[2].setRangeType(RangeType.MAX);
	        clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
			clrFmt.getThresholds()[2].setValue(1d);
			
			//条件规则
			ConditionalFormattingRule cfRule2 = sheetCF.createConditionalFormattingColorScaleRule();
			ColorScaleFormatting clrFmt2 = cfRule2.getColorScaleFormatting();
			
			clrFmt2.setColors(triColors);
			clrFmt2.getThresholds()[0].setRangeType(RangeType.NUMBER);
			clrFmt2.getThresholds()[0].setValue(-30d);
			clrFmt2.getThresholds()[1].setRangeType(RangeType.NUMBER);
	        clrFmt2.getThresholds()[1].setValue(10d);
	        clrFmt2.getThresholds()[2].setRangeType(RangeType.NUMBER);
	        clrFmt2.getThresholds()[2].setValue(50d);
	        
	        //CellRangeAddress [] regions = { CellRangeAddress.valueOf("L:M") };
	        //CellRangeAddress [] regions = { CellRangeAddress.valueOf("L2:M27") };
	        //行列索引都是从0开始
	        CellRangeAddress [] regions1 = {new CellRangeAddress(1, tableDatas[t].size()-1, 11, 12),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 14, 15),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 17, 18),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 20, 21),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 23, 24)};
	        sheetCF.addConditionalFormatting(regions1, cfRule);
	        
	        CellRangeAddress [] regions2 = {new CellRangeAddress(1, tableDatas[t].size()-1, 37, 41),
	        		new CellRangeAddress(1, tableDatas[t].size()-1, 62, 101)};
	        sheetCF.addConditionalFormatting(regions2, cfRule2);
	        */
	        
		}

		//String dir = Config.config.getProperty("DiffWind.output") + DateUtil.yyyyMMdd(new Date()) + "/";
		//new File(dir).mkdirs();
		//FileOutputStream out = new FileOutputStream(dir + outputFileName);
		
		FileOutputStream out = new FileOutputStream(outputFileName);
		wb.write(out);
		wb.close();
		out.close();
	}
	
	/**
	 * 导出csv格式
	 * @param tableData
	 * @param includingHeader
	 * @param isRound
	 * @param outputFileName
	 * @throws IOException
	 */
	public static void export2Csv(List<String[]> tableData, String outputFileName) throws IOException {
		
		FileOutputStream fos = new FileOutputStream(outputFileName);
	    OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");

	    //CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("姓名", "年龄", "家乡");
	    CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');  // 每条记录间隔符
	    CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
	        
	    for (String[] record : tableData) {
	        csvPrinter.printRecord(record);
	    }

	    csvPrinter.flush();
	    csvPrinter.close();

		return;
	}
	
	
	 /**
     * 多工作表
     * @param inputStream
     * @param ignoreHeader
     * @param headerCount
     * @return
     */
    public static Map<String,List<String[]>> readXLSXRecords(InputStream inputStream, int headerCount, boolean ignoreHeader) {  
    	Map<String,List<String[]>> result = new HashMap<String,List<String[]>>();
         
        try{  
            //XSSFWorkbook wbs = new XSSFWorkbook(inputStream);  
            Workbook wbs = new XSSFWorkbook(inputStream);  
            int sheets = wbs.getNumberOfSheets();
            
            for (int s = 0; s < sheets; s++) {
            	List<String[]> poiList = new ArrayList<String[]>(); 
            	
	            //XSSFSheet childSheet = wbs.getSheetAt(s); 
	            Sheet childSheet = wbs.getSheetAt(s);
	            String sheetName = childSheet.getSheetName();
	            
	            //logger.info(sheetName);
	            
	            //获取表头  
	            int begin = childSheet.getFirstRowNum();   
	            //XSSFRow firstRow = childSheet.getRow(begin); 
	            Row firstRow = childSheet.getRow(begin); 
	            int cellTotal = firstRow.getPhysicalNumberOfCells();  
	            
	            //logger.info(cellTotal);
	            
	            //是否跳过表头解析数据  
	            if(ignoreHeader){  
	                 begin += headerCount;  
	            }  
	            for(int i = begin;i <= childSheet.getLastRowNum();i++){  
	            	//一行的所有单元格格式都是常规的情况下，返回的row为null  
	                //XSSFRow row = childSheet.getRow(i);  
	                Row row = childSheet.getRow(i);  
	                if(null != row){  
	                    String[] cells = new String[cellTotal];  
	                    for(int k=0;k<cellTotal;k++){  
	                        //XSSFCell cell = row.getCell(k);  
	                        Cell cell = row.getCell(k); 
	                        cell.setCellType(CellType.STRING);
	                        if (cell != null) {
	                        	cells[k] = cell.getStringCellValue();  
	                        	//System.out.println(cells[k]);
	                        }
	                    }  
	                    poiList.add(cells);  
	                }  
	            }
	            
	            result.put(sheetName, poiList);
            
            }
        }catch(Exception e){  
            logger.error("excel解析:", e);  
        }  
        return result;  
     }  
	
    
    /**
     * 读取xls或xlsx格式文件
     * 
     * @param filePath
     * @param headerCount
     * @param ignoreHeader
     * @return
     */
    public static Map<String,List<String[]>> readExcelRecords(String filePath, int headerCount, boolean ignoreHeader) {  
    	Map<String,List<String[]>> result = new HashMap<String,List<String[]>>();
         
        try{  
        	String extString = filePath.substring(filePath.lastIndexOf("."));
            InputStream is = new FileInputStream(filePath);
            /*Workbook wbs = null;
            if(".xls".equals(extString)){
                wbs = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                wbs = new XSSFWorkbook(is);
            }else{
                wbs = null;
            }
            */
            
            Workbook wbs = WorkbookFactory.create(is);
                
            //XSSFWorkbook wbs = new XSSFWorkbook(inputStream);  
            //Workbook wbs = new XSSFWorkbook(inputStream);  
            int sheets = wbs.getNumberOfSheets();
            
            for (int s = 0; s < sheets; s++) {
            	List<String[]> poiList = new ArrayList<String[]>(); 
            	
	            //XSSFSheet childSheet = wbs.getSheetAt(s); 
	            Sheet childSheet = wbs.getSheetAt(s);
	            String sheetName = childSheet.getSheetName();
	            
	            //logger.info(sheetName);
	            
	            //获取表头  
	            int begin = childSheet.getFirstRowNum();   
	            //XSSFRow firstRow = childSheet.getRow(begin); 
	            Row firstRow = childSheet.getRow(begin); 
	            //int cellTotal = firstRow.getPhysicalNumberOfCells();  
	            
	            //logger.info(cellTotal);
	            
	            //是否跳过表头解析数据  
	            if(ignoreHeader){  
	                 begin += headerCount;  
	            }  
	            
	            logger.info("行数: " + childSheet.getLastRowNum());
	            //以第一个数据行为基准
	            //int cellTotal = childSheet.getRow(begin).getPhysicalNumberOfCells(); //错误，null不计数
	            int cellTotal = childSheet.getRow(begin).getLastCellNum(); //真实列数
	            for(int i = begin; i <= childSheet.getLastRowNum(); i++){  
	            	//一行的所有单元格格式都是常规的情况下，返回的row为null  
	                //XSSFRow row = childSheet.getRow(i);  
	                Row row = childSheet.getRow(i);  
	                if(null != row){  
	                	
	                    String[] cells = new String[cellTotal];  
	                    for(int k = 0; k < cellTotal; k++) {  
	                        //XSSFCell cell = row.getCell(k);  
	                        Cell cell = row.getCell(k); 
	                        if (cell != null) {
	                        	//cells[k] = cell.getStringCellValue();  
	                        	cells[k] = getCellValue(cell);
	                        	
	                        	if(cells[k].startsWith("java.lang.Object@")) {
	                        		System.out.println(filePath+": 解析问题(行:"+i+")");
	                        		
	                        	}
	                        }
	                    }
	                    
	                    //去掉空白行
	                    boolean isValidRow = false;
	                    for(int j = 0; j < cellTotal; j++) {
	                    	if (cells[j] != null && !cells[j].trim().isEmpty() )
	                    		isValidRow = true;
	                    }
	                    
	                    if (isValidRow)
	                        poiList.add(cells);  
	                }  
	            }
	            
	            result.put(sheetName, poiList);
            
            }
        }catch(Exception e){  
            logger.error("excel解析:", e);  
        }  
        return result;  
     }  
    
    public static List<String[]> readCsvRecords(String filePath, int headerCount, boolean ignoreHeader) {  
    	List<String[]> result = new ArrayList<String[]>();
         
        try{  
        	InputStream inputStream = new FileInputStream(filePath);  
		    InputStreamReader isr = new InputStreamReader(inputStream);  
		    Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(isr);  
		    for (CSVRecord record : records) {  
		    	String[] oneRecord = IteratorUtils.toArray(record.iterator(),String.class);
		    	for (int i = 0; i < oneRecord.length; i++) {
		    		oneRecord[i] = oneRecord[i].trim();
		    	}
		    		
		    	result.add(oneRecord);
		    }  
            
        }catch(Exception e){  
            logger.error("readCsvRecords:", e);  
        }  
        return result;  
     }  
    
    private static String getCellValue(Cell cell) {
		Object value = new String();
		switch (cell.getCellTypeEnum()) {
		case _NONE:
			break;
		case STRING:
			value = cell.getStringCellValue();
			break;
		case NUMERIC:
			value = cell.getNumericCellValue();  //直接显示
			break;
		case BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case BLANK:
			//value = ",";
			//value = cell.getStringCellValue();
			break;
		default:
			value = cell.getStringCellValue();
		}
		
		return value.toString().trim();
	}

}
