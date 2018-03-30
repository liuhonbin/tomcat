package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public interface ServletRequest {
	public Object getAttribute(String name);

	public Enumeration<String> getAttributeNames();

	public String getCharacterEncoding();

	public int getContentLength();

	public String getContentType();

	public InputStream getInputStream() throws IOException;

	public String getParameter(String name);

	public Enumeration<String> getParameterNames();

	public String[] getParameterValues(String name);

	public Map<String, String[]> getParameterMap();

	public String getProtocol();

	public String getServerName();

	public int getServerPort();

	public BufferedReader getReader() throws IOException;

	public String getRemoteAddr();

	public String getRemoteHost();

	public void setAttribute(String name, Object o);

	public void removeAttribute(String name);

	public Locale getLocale();

	public Enumeration<Locale> getLocales();

	public boolean isSecure();
}
