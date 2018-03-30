package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetConn {


	/**
	 * 以get方式向http发送请求
	 * 
	 * @param httpUrl
	 * @param RequestMethod
	 * @throws MalformedURLException 
	 * @throws Exception
	 */
	public static String sendget(String httpUrl) throws MalformedURLException,IOException{
		URL url = new URL(httpUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(240000);
		connection.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); // 获取输入流
		String line = null;
		StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
	}

	
    

}


