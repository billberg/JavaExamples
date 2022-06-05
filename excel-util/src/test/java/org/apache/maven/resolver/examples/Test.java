package org.apache.maven.resolver.examples;

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

import com.github.billberg.utils.ExcelUtil;

public class Test {

	private static Logger logger = Logger.getLogger(Test.class);
	
	public static void main(String[] args) {
		
		Object value = new Object();
		System.out.println("value: " + value.toString());
		
		Object value2 = new String();
		System.out.println("value2:" + value2.toString());
		
		//递归处理文件夹下所有的xlsx,xls和csv文件
		String filePath = "D:/架构管理/Maven工程组件清单/交易平台事业部/交易平台事业部maven依赖清单-汇总.xlsx";
		
		ExcelUtil.readExcelRecords(filePath, 0, false);
	}
	
	
    
}
