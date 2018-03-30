package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import util.DateUtil;

public class HttpResponse {

	private OutputStream os;

	public HttpResponse(OutputStream os) {// 构造方法/字节输出流
		this.os = os;
	}

	public void write(String str) {// 写出
		try {
			this.os.write(str.getBytes());// 将字符串转成字节并输出
			this.os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeHTML(String str) throws IOException {//设置头和内容/传入路径和类型
		try {
			StringBuffer httpResponseContent = new StringBuffer();
			httpResponseContent.append("HTTP/1.1 200 OK\n");
			httpResponseContent.append("Date: " + DateUtil.getChinaTime() + "\n");
			if (str.endsWith(".css"))
				httpResponseContent.append("Content-Type: text/css;charset=utf-8\n");
			else {
				httpResponseContent.append("Content-Type: text/html;charset=utf-8\n");
			}
			httpResponseContent.append("\r\n");
			this.os.write(httpResponseContent.toString().getBytes());//写出httpResponseContent内容
			read(str);//读取路径内容并写出
			this.os.flush();
		} catch (Exception e) {
			StringBuffer httpResponseContent = new StringBuffer();
			httpResponseContent.append("HTTP/1.1 404 OK\n");
			httpResponseContent.append("Date: " + new Date().toGMTString() + "\n");
			httpResponseContent.append("Content-Type: text/html;charset=utf-8\n");
			httpResponseContent.append("\r\n");
			this.os.write(httpResponseContent.toString().getBytes());
			this.os.write("<h1>404 Not-Fount</h1>".getBytes());
			this.os.flush();
		}
	}

//	private byte[] readHTML(String path, String charset) throws Exception {
//		FileInputStream fis = new FileInputStream(new File(path));
//		String html = "";
//		int length = 0;
//		byte[] buff = new byte[1024];
//		while ((length = fis.read(buff)) > 0) {
//			String content = new String(buff, 0, length);
//			html = html + content;
//		}
//		fis.close();
//		return html.getBytes(charset);
//	}

	private byte[] read(String path) throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));
		byte[] buff = new byte[1024];
		int length = 0;
		while ((length = fis.read(buff)) > 0) {
			this.os.write(buff, 0, length);//将字符串输出
		}
		fis.close();
		return null;
	}

}
