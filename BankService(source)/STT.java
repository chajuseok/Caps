import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class STT 
{
	public String Get()
	{
		String value = null;
		String line = null;
		byte b[] = new byte[1024];
		String address = "https://kakaoi-newtone-openapi.kakao.com/v1/recognize";
		
		try
		{
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Transfer-Encoding", "chunked");
			connection.setRequestProperty("Authorization", "KakaoAK c0826b5f67478adfc61a2c3c039c68fe");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			FileInputStream fis = new FileInputStream("heykakao.wav");
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			OutputStream os = connection.getOutputStream();
			
			
			while(bis.read(b) != -1 )
			{
				os.write(b);
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = in.readLine()) != null )
			{
				System.out.println(line);
			}
			in.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}
}
