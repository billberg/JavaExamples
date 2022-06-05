package com.github.billberg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.poi.util.ArrayUtil;

public class ProjectsDependencyFileHandler {

	private static Logger logger = Logger.getLogger(ProjectsDependencyFileHandler.class);
	
	public static void main(String[] args) {
		
		//递归处理文件夹下所有的xlsx,xls和csv文件
		String inputFolder = "D:/架构管理/Maven工程组件清单/";
		
		/*
		String outputFileName = "组件清单(汇总).csv";
		
		List<String[]> allRecords = read(new File(inputFolder));
		System.out.println("记录数: " + allRecords.size());
		
		List[] tableDatas = new List[] {allRecords};
		String[] tableNames = {"组件清单"};
		try {
			
			ExcelUtil.export2Csv(allRecords, outputFileName);
			
		} catch (IOException e) {
			logger.error("输出文件出错", e);
		}	
		*/
		
		String outputFileName = "组件清单(汇总).xlsx";
		//每个中心/事业部汇总一个文件
		File[] filelist = new File(inputFolder).listFiles();
		List[] tableDatas = new List[filelist.length];
		String[] tableNames = new String[filelist.length];
        for (int i = 0; i < filelist.length; i++) {
        	List<String[]> folderRecords = read(filelist[i]);
        	
        	List<Object[]> objRecords = new ArrayList<Object[]>();
        	for (String[] strRecord : folderRecords) {
        		Object[] objRecord = new Object[strRecord.length];
        		for(int j = 0; j < strRecord.length; j++) {
        			objRecord[j] = strRecord[j];
        		}
        		objRecords.add(objRecord);
        	}
        	
        	tableDatas[i] = objRecords;
        	tableNames[i] = filelist[i].getName();
        	
    		
        }
        
        try {
			
			ExcelUtil.exportTables2Excel(tableNames, tableDatas, false, false,
					outputFileName);
			
		} catch (IOException e) {
			logger.error("输出文件出错", e);
		}	
		
			
		
	}
	
	
    public static List<String[]> read(File fileOrDirectory) {
    	List<String[]> allRecords = new ArrayList<String[]>();
    	
    	if (fileOrDirectory.isFile()) {
    		if (fileOrDirectory.getName().endsWith(".xlsx") || fileOrDirectory.getName().endsWith(".xls") ) {
    			
    			Map<String, List<String[]>> oneFileRecords = ExcelUtil.readExcelRecords(fileOrDirectory.getAbsolutePath(), 0, false);
    		
    			for (List<String[]> sheet : oneFileRecords.values()) {
    				allRecords.addAll(sheet);
    			}
    			
    			System.out.println(fileOrDirectory.getName() + ": 处理完成, 记录数: " + allRecords.size());
    		} else if (fileOrDirectory.getName().endsWith(".csv")) {
    			List<String[]> oneFileRecords = ExcelUtil.readCsvRecords(fileOrDirectory.getAbsolutePath(), 0, false);
    		
    			allRecords.addAll(oneFileRecords);
    			
    			System.out.println(fileOrDirectory.getName() + ": 处理完成, 记录数: " + allRecords.size());
    		}
    		
    	} else if (fileOrDirectory.isDirectory()) {
    		
			File[] filelist = fileOrDirectory.listFiles();

            for (File f : filelist) {
            	List<String[]> subRecords = read(f);
            	allRecords.addAll(subRecords);
            }
		}
    	
    	return allRecords;    	
    }
}
