package getContent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class Pool {
	public static CloseableHttpClient httpClient = null;
	public static ExecutorService executorServicePage = null;
	public static ExecutorService executorServiceBook = null;
	static {
		PoolingHttpClientConnectionManager cm1 = new PoolingHttpClientConnectionManager();
		cm1.setMaxTotal(200);// 设置最大连接数
		cm1.setDefaultMaxPerRoute(200);// 对每个指定连接的服务器（指定的ip）可以创建并发20 socket进行访问
		httpClient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler())// 设置请求超时后重试次数
				.setConnectionManager(cm1).build();
		//设置处理分页的线程池
		executorServicePage = Executors.newFixedThreadPool(100);
		executorServiceBook = Executors.newFixedThreadPool(100);
	}
}
