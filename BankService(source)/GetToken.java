import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
public class GetToken 
{
	public String Get(String code)
	{
		String address = "https://testapi.openbanking.or.kr/oauth/2.0/token";
		Properties params = new Properties();
		params.setProperty("code", code);
		params.setProperty("client_id", "1eaec044-0b78-41fe-99cd-a6afad2cdeba");
		params.setProperty("client_secret", "2482daed-0be3-40e2-9b7d-4ad9c632e3e6");
		params.setProperty("redirect_uri","http://localhost:8000");
		params.setProperty("grant_type", "authorization_code");
		String param = new EncodeString().encodedString(params);
		String line = null;
		
		try
		{
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			try(OutputStream os = connection.getOutputStream())
			{
				os.write(param.getBytes());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			line = in.readLine();
			in.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return line;
	}
}
