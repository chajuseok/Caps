import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class SavingInquiry 
{
	public void Inq()
	{
		String address = "https://testapi.openbanking.or.kr/v2.0/account/balance/fin_num?";
		Properties parmas = new Properties();
		parmas.setProperty("bank_tran_id", "M202200556U123456781");
		parmas.setProperty("fintech_use_num", "120220055688941039888349");
		parmas.setProperty("tran_dtime", "20220328103410");
		
		try
		{
			URL url = new URL(address+new EncodeString().encodedString(parmas));
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization","BearereyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAxMDA1MjM5Iiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2NTg2NjU2MjcsImp0aSI6ImZiNjFjNTRlLTMwYmYtNDNjNi05ODIyLTlmY2QzZTQ0ZWI2MSJ9.A6JgGQDPcwh1B2fXT4xRojudkkdmbNUgRBFDuKS1OCE" );
			connection.setDoInput(true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			//응답 내용
			String line = null;
			while((line = in.readLine()) != null) 
			{
				System.out.println(line);
			}
			in.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
