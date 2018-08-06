package getContent;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import thread.AddBookUrlThread;

public class Main {
	
	/** 下载urlFirst网页中的全部书籍
	 * @param urlFirst
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void GetAll(String urlFirst) throws Exception {
		GetContent content = new GetContent();
		String text = content.getContent(urlFirst); 
		String pageTotalString = content.PageAll(text);
		
		if (pageTotalString != null) {
			Integer pageTotal = Integer.valueOf(pageTotalString);
			//值为1万多  为了测试起见  下面for循环的pageTotal可以改成1 表示一个分页 （也有十几本小说）
			for (int i = 1; i <= pageTotal; i++) {
				//小说的网址
				String url = "www.bookbao8.com/booklist-p_" + i + "-c_0-t_2-o_1.html";
				//加入线程池并发处理
				Pool.executorServicePage.execute(new AddBookUrlThread(url));
			}
		}
	}
	public static void main(String[] args) throws Exception {
		String url = "www.bookbao8.com/BookList-c_0-t_2-o_1.html";
		GetAll(url);
		ExecutorService executorServicePage = Pool.executorServicePage;
		ExecutorService executorServiceBook = Pool.executorServiceBook;
		
		//等待加入线程全部执行完毕
		executorServicePage.shutdown();
		//awaitTermination限制每10秒循环一次是否全部结束，
		while (!executorServicePage.awaitTermination(5, TimeUnit.SECONDS));
		//如果线程全部结束isTerminated则为true
		boolean PageEnd = executorServicePage.isTerminated();
		
		if(PageEnd) {
			System.out.println("获取小说书名信息和链接成功！");
			executorServiceBook.shutdown();
		} 
		System.out.println("*************************************************");
		while (!executorServiceBook.awaitTermination(3, TimeUnit.SECONDS));
		
		//如果线程全部结束isTerminated则为true
		boolean BooksEnd = executorServicePage.isTerminated();
		if(BooksEnd) {
			System.out.println("获取小说成功！");
		} 
	}
}
