import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;

public class Authorization 
{
	public void Auth()
	{
		String address = "https://testapi.openbanking.or.kr/oauth/2.0/authorize?";
		Properties parmas = new Properties();
		parmas.setProperty("response_type", "code");
		parmas.setProperty("client_id", "1eaec044-0b78-41fe-99cd-a6afad2cdeba");
		parmas.setProperty("redirect_uri", "http://localhost:8000");
		parmas.setProperty("scope", "login inquiry transfer");
		parmas.setProperty("state", "b80BLsfigm9OokPTjy03elbJqRHOfGSY");
		parmas.setProperty("auth_type", "0");
		
		String url =address+new EncodeString().encodedString(parmas);
		new WebCall().call(url);
		
	}
}