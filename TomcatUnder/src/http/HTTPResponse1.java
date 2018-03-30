package http;

import java.io.IOException;

public interface HTTPResponse1 {

	void write(String str);
	
	
	void writeHTML(String str) throws IOException;
	
}
