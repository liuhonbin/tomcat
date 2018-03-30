package tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import http.HttpServletRequestWrapper;
import http.HttpResponse;
import http.HttpServletRequest;
import http.Servlet;
import util.DateUtil;
import util.GetConn;

@SuppressWarnings("rawtypes")
public class SocketServerCore {
	private int port;//端口号
	private Socket client;//Socket 套接字 封装了TCP协议
	private ServerSocket serverSocket;
	//读取配置文件
	private String WEB_INFO = getClass().getResource("/").getPath() + "web.properties";
	
	@SuppressWarnings("unchecked")
	private Map<Pattern, Class<?>> servletMapping = new HashMap();
	private Properties webxml = new Properties();
	public SocketServerCore() {
	}

	public SocketServerCore(int port) {// 指定端口号
		this.port = port;
	}
	
	private void init() throws Exception {
		FileInputStream fis = new FileInputStream(new File(this.WEB_INFO));
		this.webxml.load(fis);
		for (
		Iterator localIterator = this.webxml.keySet().iterator(); localIterator.hasNext();) {
			String key = localIterator.next().toString();
			if (key.endsWith(".url")) {
				String servletName = key.replaceAll("\\.url", "");
				String url = this.webxml.getProperty(key);
				String servletClassName = this.webxml.getProperty(servletName + ".className");
				Class servletCalss = Class.forName(servletClassName);
				Pattern pattern = Pattern.compile(url);
				this.servletMapping.put(pattern, servletCalss);
			}
		}
	}

	private void peosser(Socket client) throws IOException {
		InputStream is = client.getInputStream();
		OutputStream os = client.getOutputStream();
		try {
			HttpServletRequest request = new HttpServletRequestWrapper(is);
			HttpResponse response = new HttpResponse(os);
			String url = request.getRequestURI();
			if(url.endsWith("www.")) {
				os.write(GetConn.sendget(url).getBytes());
				os.flush();
			}else {
				if (isStatic(url)) {// 判断是否为静态资源
					if (url.indexOf("js?_") > 0) {
						response.writeHTML(url.substring(1, url.indexOf("js?_")));
					} else if (url.indexOf("/") == 0)
						response.writeHTML(url.substring(1));
					else {
						response.writeHTML(url);
					}
				} else {
					boolean isPattern = false;
					for (Map.Entry entry : this.servletMapping.entrySet()) {
						if (((Pattern) entry.getKey()).matcher(url).matches()) {
							Servlet servlet = (Servlet) ((Class) entry.getValue()).newInstance();
							servlet.service(request, response);
							isPattern = true;
						}
					}
					if (!isPattern) {
						StringBuffer httpResponseContent = new StringBuffer();
						httpResponseContent.append("HTTP/1.1 404 OK\n");
						httpResponseContent.append("Date: " + DateUtil.getChinaTime() + "\n");
						httpResponseContent.append("Content-Type: text/html;charset=utf-8\n");
						httpResponseContent.append("\r\n");
						os.write(httpResponseContent.toString().getBytes());
						os.write("<h1>404 Not-Fount</h1>".getBytes());
						os.flush();
					}
				}
			}
			
		} catch (Exception e) {
			StringBuffer httpResponseContent = new StringBuffer();
			httpResponseContent.append("HTTP/1.1 404 OK\n");
			httpResponseContent.append("Date: " + DateUtil.getChinaTime() + "\n");
			httpResponseContent.append("Content-Type: text/html;charset=utf-8\n");
			httpResponseContent.append("\r\n");
			os.write(httpResponseContent.toString().getBytes());
			os.write(("<h1>404 Not-Fount</h1>" + e.getMessage()).getBytes());
			os.flush();
		}
		is.close();
		os.close();
		client.close();
	}

	public void start() {
		try {
			init();//初始化
			this.serverSocket = new ServerSocket(this.port);
			System.out.println("服务器启以启动——————————————监听端口：" + this.port);
			SocketPeosser socketPeosser = new SocketPeosser(serverSocket, servletMapping);
			new Thread(socketPeosser).run();
			
//			while (true) {
//				this.client = this.serverSocket.accept();
//				peosser(this.client);
//			}
		} catch (Exception e) {
			System.out.println("Runtime Exception  " + e.getMessage());
		}
	}

	
	public boolean isStatic(String url) {
		String[] isStatic = { "css", "shtml", "html", "js", "mp3", "mp4", "swf", "jpg", "png", "less", "eot", "svg",
				"ttf", "woff", "map" };
		for (String string : isStatic) {
			if (url.indexOf("?_") > 0) {
				if (url.substring(1, url.indexOf("?_")).endsWith("." + string)) {
					return true;
				}
			} else if (url.endsWith("." + string)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		new SocketServerCore(9090).start();
	}
	
}
