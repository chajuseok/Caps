import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebCall 
{
	public void call(String url)
	{
		try 
		{
			Desktop.getDesktop().browse(new URI(url));
		}catch (IOException e)
		{
			e.printStackTrace();
		}catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
}
