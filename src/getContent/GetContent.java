package getContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

public class GetContent {
	private BufferedReader bufferedReader = null;
	private StringBuilder conTent = null;

	/**
	 * 爬取指定网页的全部内容
	 * 
	 * @param urlin
	 *            传入地址
	 * @return 爬取指定网页的全部内容
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public String getContent(String urlin) throws URISyntaxException, IOException {
		// 获取爬取网络的地址
		URI url = new URIBuilder().setScheme("https").setHost(urlin).build();
		conTent = new StringBuilder();

		HttpGet httpGet = new HttpGet(url);
		/*
		 * httpGet.setHeader("User-Agent",
		 * "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"
		 * );
		 */
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = Pool.httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			// 获取编码信息
			InputStream is = entity.getContent();
			String line = "";
			// 转换为缓冲流，提高效率，可以按行读取
			bufferedReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			while ((line = bufferedReader.readLine()) != null) {
				conTent.append(line);
			}
			is.close();
			return conTent.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (httpResponse != null) {
				httpResponse.close();
			}
		}
	}

	/**
	 * 爬取指定内容中的总分页数
	 * 
	 * @param text
	 *            指定网页源码内容
	 * @return 总分页数
	 */
	public String PageAll(String text) {
		String regex = "/共(\\d+?)页";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		boolean is = matcher.find();
		if (is) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
}