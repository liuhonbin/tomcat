package http;

import java.io.IOException;

public abstract class Servlet {

	public void service(HttpServletRequest request,HttpResponse response) throws IOException{
 		if("GET".equalsIgnoreCase(request.getMethod())){
 			doGet(request, response);
 		}else{
 			doPost(request, response);
 		}
 	};
	 
   public abstract void doGet(HttpServletRequest request,HttpResponse response) throws IOException;
   
   public abstract void doPost(HttpServletRequest request,HttpResponse response) throws IOException;
}
