import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class Withdrawl 
{
	public void Withdraw()
	{
		String address = "https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num";
		String PostBody = "{\r\n"
				+ "    \"bank_tran_id\": \"M202200556U123456612\",\r\n"
				+ "    \"cntr_account_type\": \"N\",\r\n"
				+ "    \"cntr_account_num\": \"3021011435911\",\r\n"
				+ "    \"dps_print_content\": \"테스트\",\r\n"
				+ "    \"fintech_use_num\": \"120220055688941039511110\",\r\n"
				+ "    \"tran_amt\": \"1000\",\r\n"
				+ "    \"tran_dtime\": \"20220401113210\",\r\n"
				+ "    \"req_client_name\": \"김효정\",\r\n"
				+ "    \"req_client_num\": \"20220401319690596627\",\r\n"
				+ "    \"transfer_purpose\": \"TR\",\r\n"
				+ "    \"req_client_fintech_use_num\": \"120220055688941039534617\",\r\n"
				+ "    \"recv_client_name\": \"김주열\",\r\n"
				+ "    \"recv_client_bank_code\": \"011\",\r\n"
				+ "    \"recv_client_account_num\": \"3021011435911\"\r\n"
				+ "}";
		try
		{
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization","BearereyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAxMDA1NDI1Iiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2NTY1NjI0NDUsImp0aSI6IjE3NTk0MjUxLTNjYzctNDFmMS05MzVkLTAxN2JjNzJjZjJkNCJ9.3mmDtM6vdUept4_pvPsUJUYVRp-lTrThHo9zV-bN7G4");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			try(OutputStream os = connection.getOutputStream())
			{
				byte request_data[] = PostBody.getBytes("UTF-8");
				os.write(request_data, 0, request_data.length);
			}
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
