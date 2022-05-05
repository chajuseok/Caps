import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

public class EncodeString 
{
	public String encodedString(Properties params)
	{
		StringBuffer sb = new StringBuffer(256);
		Enumeration keys = params.propertyNames();
		try 
		{
			while(keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				String value = params.getProperty(key);
				sb.append(URLEncoder.encode(key, "UTF-8")+"="+URLEncoder.encode(value, "UTF-8"));
				
				if(keys.hasMoreElements())
					sb.append("&");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
}
