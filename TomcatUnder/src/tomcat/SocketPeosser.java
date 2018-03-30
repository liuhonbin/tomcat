package tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Pattern;

import http.HttpResponse;
import http.HttpServletRequest;
import http.HttpServletRequestWrapper;
import http.Servlet;
import util.GetConn;
import util.HttpResponseContentUtil;

public class SocketPeosser implements Runnable{
	
	private ServerSocket serverSocket;
	private Socket client;
	
	private Map<Pattern, Class<?>> servletMapping;
	
	public SocketPeosser(ServerSocket serverSocket,Map<Pattern, Class<?>> servletMapping) {
		this.serverSocket = serverSocket;
		this.servletMapping = servletMapping;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				client = serverSocket.accept();
				System.err.println("```````````````````");
				peosser(client);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						os.write(HttpResponseContentUtil.NotFount());
						os.flush();
					}
				}	
			}
		} catch (Exception e) {
			os.write(HttpResponseContentUtil.NotFount());
			os.flush();
		}
		is.close();
		os.close();
		client.close();
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
}
