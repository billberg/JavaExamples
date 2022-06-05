package data.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


/**
 * 
 * @version V2
 * @author DiffWind
 *
 */
public class HttpUtil {

	private static Logger logger = Logger.getLogger(HttpUtil.class);

	@Deprecated
	List<NameValuePair> requestParams = new ArrayList<NameValuePair>();

	/**
	 * 
	 * @param requestUrl
	 * @param headers
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public static byte[] doGetRawContent(String requestUrl, Map<String, String> headers, int timeout)
			throws IOException {

		HttpGet httpGet = new HttpGet(requestUrl);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.build();
		httpGet.setConfig(requestConfig);

		// 设置请求头
		if (headers != null) {
			for (Entry<String, String> head : headers.entrySet()) {
				httpGet.addHeader(head.getKey(), head.getValue());
			}
		}

		logger.info("Request: " + requestUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		logger.info("Response Code: " + httpResponse.getStatusLine().getStatusCode());
		if (200 != httpResponse.getStatusLine().getStatusCode()) {
			throw new HttpAntiSpiderException(httpResponse.getStatusLine().getStatusCode(),
					"http请求失败，可能遇到了反爬虫异常，响应码: " + httpResponse.getStatusLine().getStatusCode());
		}

		try {
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				byte[] rawContent = EntityUtils.toByteArray(entity);
				logger.info("字节数: " + rawContent.length);
				
				return rawContent;
			}
		} finally {
			httpResponse.close();
		}

		return null;
	}

	/**
	 * 未使用
	 * 
	 * @return
	 */
	protected String doPost(String requestUrl) {
		String ret = null;
		try {
			HttpClient client = HttpClients.createDefault();
			// HttpGet httpGet = new HttpGet(requestUrl);

			HttpPost httpPost = new HttpPost(requestUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(requestParams, Consts.UTF_8));

			logger.info("Request: " + requestUrl);
			// add request header
			// request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(httpPost);

			logger.info("Response Code: " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(), Consts.UTF_8));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			// String ret = response.asString();
			ret = result.toString();
			logger.info("Response Body: " + ret);

			// response = HttpClient.httpPostRequest(BASE_URL + requestUrl,
			// URLEncoder.encode(jsonParams.toString(),"UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

	/**
	 * TODO 自定义异常类型，便于调用方识别
	 * @param requestUrl
	 * @param headers
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public static String doGet(String requestUrl, Map<String, String> headers, int timeout) throws IOException {

		return doGet(requestUrl, headers, timeout, "UTF-8");
	}
	
	public static String doGet(String requestUrl, CookieStore cookieStore, Map<String,String> headers, int timeout) throws IOException {

		return doGet(requestUrl, cookieStore, headers, timeout, "UTF-8");
	}

	/**
	 * 
	 * 如果想从外部关闭，使用CloseableHttpClient/CloseableHttpResponse
	 * 
	 * 
	 * @param requestUrl
	 * @param headers
	 * @param timeout
	 * @param charset
	 * @return 如果响应码不为200则抛出异常
	 * @throws IOException
	 */
	public static String doGet(String requestUrl, Map<String, String> headers, int timeout, String charset)
			throws IOException {

		HttpGet httpGet = new HttpGet(requestUrl);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.build();
		httpGet.setConfig(requestConfig);

		// 设置请求头
		if (headers != null) {
			for (Entry<String, String> head : headers.entrySet()) {
				httpGet.addHeader(head.getKey(), head.getValue());
			}
		}

		//logger.info("Request: " + requestUrl);
		System.out.println("Request: " + requestUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		//CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		//logger.info("Response Code: " + httpResponse.getStatusLine().getStatusCode());
		System.out.println("Response Code: " + httpResponse.getStatusLine().getStatusCode());
		if (200 != httpResponse.getStatusLine().getStatusCode()) {
			throw new HttpAntiSpiderException(httpResponse.getStatusLine().getStatusCode(),
					"http请求失败，可能遇到了反爬虫异常，响应码: " + httpResponse.getStatusLine().getStatusCode());
		}

		try {
			//部分网站使用gb2312字符集但解析出现乱码
			//gb2312支持的字符集编码比较小，GBK兼容并且大
			if (charset.equalsIgnoreCase("gb2312"))
				charset = "GBK";
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent(), charset));
			StringBuffer response = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();

		} finally {
			httpResponse.close();
		}

	}
	
	
	public static String doGet(String requestUrl, CookieStore cookieStore, Map<String,String> headers, int timeout, String charset)
			throws IOException {

		HttpGet httpGet = new HttpGet(requestUrl);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.build();
		httpGet.setConfig(requestConfig);

		// 设置请求头
		if (headers != null) {
			for (Entry<String, String> head : headers.entrySet()) {
				httpGet.addHeader(head.getKey(), head.getValue());
			}
		}

		logger.info("Request: " + requestUrl);

		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		logger.info("Response Code: " + httpResponse.getStatusLine().getStatusCode());
		if (200 != httpResponse.getStatusLine().getStatusCode()) {
			throw new HttpAntiSpiderException(httpResponse.getStatusLine().getStatusCode(),
					"http请求失败，可能遇到了反爬虫异常，响应码: " + httpResponse.getStatusLine().getStatusCode());
		}

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent(), charset));
			StringBuffer response = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();

		} finally {
			httpResponse.close();
		}

	}

	/**
	 * 获取Cookie
	 * @param requestUrl
	 * @param headers
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public static CookieStore getCookie(String requestUrl, Map<String, String> headers, int timeout) throws IOException {

		// 全局请求设置
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		// 创建cookie store的本地实例
		CookieStore cookieStore = new BasicCookieStore();
		// 创建HttpClient上下文
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);

		// 创建一个HttpClient
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.setDefaultCookieStore(cookieStore).build();

		HttpGet httpGet = new HttpGet(requestUrl);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.build();
		httpGet.setConfig(requestConfig);

		// 设置请求头
		if (headers != null) {
			for (Entry<String, String> head : headers.entrySet()) {
				httpGet.addHeader(head.getKey(), head.getValue());
			}
		}

		HttpResponse httpResponse = httpClient.execute(httpGet);

		logger.info("Response Code: " + httpResponse.getStatusLine().getStatusCode());
		if (200 != httpResponse.getStatusLine().getStatusCode()) {
			throw new HttpAntiSpiderException(httpResponse.getStatusLine().getStatusCode(),
					"http请求失败，可能遇到了反爬虫异常，响应码: " + httpResponse.getStatusLine().getStatusCode());
		}

		return cookieStore;

	}

	/**
	 * 先通过主页获取cookie再请求
	 * @param requestUrl
	 * @param headers
	 * @param timeout
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public String doGetWithCookie(String requestUrl, Map<String, String> headers, int timeout, String charset)
			throws IOException {

		// 全局请求设置
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		// 创建cookie store的本地实例
		CookieStore cookieStore = new BasicCookieStore();
		// 创建HttpClient上下文
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);

		// 创建一个HttpClient
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.setDefaultCookieStore(cookieStore).build();

		CloseableHttpResponse res = null;

		// 创建本地的HTTP内容

		try {
			// 创建一个get请求用来获取必要的Cookie，如_xsrf信息
			HttpGet get = new HttpGet("http://xueqiu.com/");

			res = httpClient.execute(get);
			// 获取常用Cookie,包括_xsrf信息,放在发送请求之后
			System.out.println("访问知乎首页后的获取的常规Cookie:===============");
			for (Cookie c : cookieStore.getCookies()) {
				System.out.println(c.getName() + ": " + c.getValue());
			}
			res.close();

			// 构造一个新的get请求，用来测试登录是否成功
			HttpGet httpGet = new HttpGet(requestUrl);

			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.build();
			httpGet.setConfig(requestConfig);

			// 设置请求头
			if (headers != null) {
				for (Entry<String, String> head : headers.entrySet()) {
					httpGet.addHeader(head.getKey(), head.getValue());
				}
			}

			logger.info("Request: " + requestUrl);

			res = httpClient.execute(httpGet, context);

			logger.info("Response Code: " + res.getStatusLine().getStatusCode());
			if (200 != res.getStatusLine().getStatusCode()) {
				throw new HttpAntiSpiderException(res.getStatusLine().getStatusCode(),
						"http请求失败，可能遇到了反爬虫异常，响应码: " + res.getStatusLine().getStatusCode());
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(res.getEntity().getContent(), charset));
				StringBuffer response = new StringBuffer();
				String line = "";
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}

				return response.toString();
			} finally {
				res.close();
			}

		} finally {
			httpClient.close();
		}

	}

	protected void addParam(String param, String value) {
		requestParams.add(new BasicNameValuePair(param, value));
	}

	protected void addParams(String[] params, String[] values) {
		for (int i = 0; i < params.length; i++)
			addParam(params[i], values[i]);
	}

	protected void clearParams() {
		requestParams.clear();
	}


	public static void main(String[] args) {

		String requestUrl = "https://xueqiu.com";

	
	}

}
