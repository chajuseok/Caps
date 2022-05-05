import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GetCode 
{
	public String Get()
	{
		int maxBufferSize = 1024;
		byte[] recvBuffer = new byte[maxBufferSize];
		String response = null;
		try
		{
			ServerSocket listener = new ServerSocket(8000);
			Socket serv_sock = listener.accept();
			InputStream in = serv_sock.getInputStream();
			in.read(recvBuffer);
			response = new String(recvBuffer);
			listener.close();
			serv_sock.close();
			if(response.indexOf("code") >= 0)
			{
				response =  response.substring(response.indexOf("code")+5,response.indexOf("&"));
			}
			else
			{
				response = "Error";
			}
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return response;
	}
}
