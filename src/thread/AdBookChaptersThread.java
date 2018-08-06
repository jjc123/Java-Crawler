package thread;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Book;
import getContent.GetContent;
import write.FileReaderWriter;

/**
 * @author 下載一本书
 *
 */
public class AdBookChaptersThread implements Runnable {
	private Book book;
	private GetContent content = new GetContent();

	public AdBookChaptersThread(Book book) {
		this.book = book;
	}

	/**
	 * 爬取该书本信息
	 * 
	 * @param book
	 * @return
	 */
	public boolean getBookInformation(Book book) {
		// FileReaderWriter.writeIntoFile("zz1", "D:/知乎-编辑推荐.txt", false);
		String xx;
		try {
			xx = content.getContent(book.getUrl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("获取书本信息失败");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// book中目前只有地址和书名
		String regex = "id=\"info\">.*?<a.+?>(.+?)</a>.*?" + "<a.+?>(.*?)</a>.*?" + "<p>状态：(.+?)</p>.*?"
				+ "<p>更新时间：(.+?)</p>.*?class=\"infocontent\">(.+?)</div>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(xx);
		boolean is = matcher.find();
		String information = "";
		if (is) {
			book.setAuthor(matcher.group(1));
			book.setType(matcher.group(2));
			book.setStatus(matcher.group(3));
			book.setUpdate_time(matcher.group(4));
			information = matcher.group(5);
			information = information.replaceAll("<br />", "\r\n");
			book.setBook_Introduction(information);
			return true;
		}
		return false;
	}

	/**
	 * 下载该书籍简介等信息
	 * 
	 * @param bookName
	 * @return
	 */
	public boolean getInformation() {
		if (getBookInformation(book)) {
			FileReaderWriter.writeIntoFile(book.toString(), "D:/书籍/" + book.getBook_name() + ".txt", true);
			System.out.println(book.getBook_name() + "信息爬取成功");
			return true;
		}
		return false;
	}

	/**
	 * @return 获取章节页面链接
	 */
	public String getChapterUrl(String ulString) {

		String url = book.getUrl();
		int index = url.indexOf("/");
		// 截取首页www.bookbao8.com
		StringBuilder dir = new StringBuilder(url.substring(0, index));
		dir.append(ulString);
		return dir.toString();
	}

	/**
	 * @return 爬起小说的章节列表区域源代码
	 */
	public String getTextAll() {
		String text = "";
		String url = book.getUrl();
		try {
			text = content.getContent(url);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// 获取章节的列表内容，而不是其他如热门排行榜
		String regex = "class=\"wp b2 info_chapterlist\">.*</ul>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		boolean is = matcher.find();
		if (is) {
			String ulString = matcher.group();
			return ulString;
		}
		return null;
	}

	/**
	 * 爬取指定章节链接的内容
	 * 
	 * @param url
	 *            章节链接
	 * @return 返回的文章内容
	 */
	public String getText(String url) {
		// http://www.bookbao8.com/views/201708/29/id_XNTg3MTc5_7.html
		String text = "";
		try {
			text = content.getContent(url);
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regex = "id=\"contents\".*?>(.+?)</dd>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		boolean is = matcher.find();
		if (is) {
			String One = matcher.group(1);
			One = One.replace("<br />", "\r\n");
			return One;
		}
		return null;
	}

	@Override
	public void run() {

		System.out.println("开启获取书籍线程：" + Thread.currentThread().getName());
		String bookName = book.getBook_name();
		if (getInformation()) {
			System.out.println("爬取" + bookName + "信息成功");
		}
		// 获取该小说的章节源内容
		String ulString = getTextAll();
		// 获取标题内容 和链接
		String regex = "href=\"(.+?)\".+?>(.+?)</a>";
		String chapter = "";
		String contentText = "";
		StringBuilder All = new StringBuilder();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(ulString);
		while (matcher.find()) {
			// 获取章节链接	
			chapter = getChapterUrl(matcher.group(1));
			// 爬取文章内容
			contentText = getText(chapter);
			// 下载标题
			All.append(matcher.group(2));
			All.append("\r\n\r\n");
			//下载章节内容
			All.append(contentText);
			All.append("\r\n\r\n\r\n");
			FileReaderWriter.writeIntoFile(All.toString(), "D:/书籍/" + bookName + ".txt", true);
			System.out.println(book.getBook_name() + "章节爬取成功！");
		}
		System.out.println(bookName + "全部爬取成功！");
	}
}
