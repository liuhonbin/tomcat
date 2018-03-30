package servlet;

import java.io.IOException;

import http.HttpServletRequestWrapper;
import http.HttpResponse;
import http.HttpServletRequest;
import http.Servlet;

public class MyServlet extends Servlet {

	@Override
	public void doGet(HttpServletRequest request, HttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.writeHTML("hello,world");
	}

}
