package data.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.billberg.utils.ExcelUtil;

/**
 * THS分红历史数据，数据质量最高
 * 下载中遇到反爬虫禁止下载，而且禁止时间很长（非常麻烦），下载策略加入时间控制，可以慢速下载
 * 
 * @version v2020.6
 * @author billberg
 * 
 */
public class GithubDataRobot {

	private static Logger logger = Logger.getLogger(GithubDataRobot.class);

	private static HashMap<String, String> httpHeaders = new HashMap<String, String>() {
		{
			put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:55.0) Gecko/20100101 Firefox/55.0");
			put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			//压缩
			put("Accept-Encoding", "gzip, deflate");
			put("Connection", "keep-alive");
		}
	};

	private final static String SOURCE_ENCODE = "utf-8";

	
	/**
	 * github project地址
	 * @param symbol
	 * @return {stars,description}
	 * @throws IOException
	 */
	private static JSONObject downloadHtml2Json(String requestUrl) {
		logger.info(requestUrl);

		JSONObject jsonObject=new JSONObject(true);
		jsonObject.put("url",requestUrl);
		
		try {
	
			//html下载解析
			String response = HttpUtil.doGet(requestUrl, httpHeaders, 20 * 1000, SOURCE_ENCODE);
					
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode tagNode = cleaner.clean(response);
			//<div class="inner_box">
			//Object[] divNode = tagNode.evaluateXPath("//div[@class='inner_box']");
				
			//根据<table><thead>头中含有“分红年度”“分红方案”字段，抽取出正确的<table>
			boolean isFind = false;
			Object[] bonusHist = tagNode.evaluateXPath("//div[@class='BorderGrid-cell']");
			
			//System.out.print(((TagNode)bonusHist[0]).getText());
			
			for (int i = 0; i < ((TagNode)bonusHist[0]).getChildTags().length; i++) {
				
				TagNode pageRightNode = ((TagNode)bonusHist[0]).getChildTags()[i];
				
				System.out.println("--------");
				System.out.println(pageRightNode.getName());
				System.out.print(pageRightNode.getText());
				System.out.println();
				
			
				if (i == 1) {
					String description = pageRightNode.getText().toString().trim();
					jsonObject.put("description", description);
				} else if (pageRightNode.getText().toString().trim().matches("\\d.*stars")){
					String stars = pageRightNode.getText().toString().trim();
					jsonObject.put("stars", stars);
				}
				
			}
			
			System.out.println(jsonObject);
			
			
				
		} catch(Throwable e) {
			logger.error(requestUrl +": ", e);
		}
		
		return jsonObject;

	}
	

	
	/**
	 * 全量下载，增量更新
	 * THS数据会更新，所以增量下载的本地数据有错误。
	 * 案例：000921 {"财年":2019,"报告期":"2019年报","董事会日期":"2020-04-15","分红方案说明":"10派3.95元(含税)","分红总额":35700,"方案进度":"董事会预案","股利支付率":29.92}
	 * @return 下载失败数
	 */
	public static int downloadData() {


		//String requestUrl = "https://github.com/apache/ant";
		List<String[]> tableData = new ArrayList<String[]>();
		tableData.add(new String[] {"url","stars","description"});
		
		JSONArray jsonArray = new JSONArray();
		List<String[]> records = ExcelUtil.readCsvRecords("D:/架构管理/Maven工程开源组件清单（导入清单）-202204.csv",1,false);
		for (int i = 1; i < records.size(); i++) {
			String[] proj = records.get(i);
			JSONObject jsonObj = downloadHtml2Json(proj[1]);
			jsonArray.add(jsonObj);
			
			String url = jsonObj.getString("url");
			String stars = jsonObj.getString("stars");
			String description = jsonObj.getString("description");
			tableData.add(new String[] {url,stars,description});
		}
		
		String outputFile = "D:/架构管理/GithubProjectsInfo-202205.csv";
		try {
			ExcelUtil.export2Csv(tableData, outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return 0;

	}
	
	public static void main(String[] args) {
		
		downloadData();
	}

}