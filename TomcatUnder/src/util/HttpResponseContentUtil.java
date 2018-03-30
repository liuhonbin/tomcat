package util;

public class HttpResponseContentUtil {
	
	
	/**
	 * 404结果返回工具类
	 */
	public static byte [] NotFount() {
		StringBuffer httpResponseContent = new StringBuffer();
		httpResponseContent.append("HTTP/1.1 404 OK\n");
		httpResponseContent.append("Date: " + DateUtil.getChinaTime() + "\n");
		httpResponseContent.append("Content-Type: text/html;charset=utf-8\n");
		httpResponseContent.append("\r\n");
		httpResponseContent.append("<h1>404 Not-Fount</h1>");
		return httpResponseContent.toString().getBytes();
	}

}
