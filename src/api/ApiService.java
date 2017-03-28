package api;
import java.util.Calendar;

public class ApiService 
{
	public static boolean isrookie(String stuid)
	{
		if(stuid.startsWith("51")) stuid = stuid.substring(2);
		if(stuid.length() < 4) return false;
		Integer grand = 0;
		try
		{
			grand = Integer.valueOf(stuid.substring(0, 4));
		}
		catch(Exception ex)
		{
			return false;
		}
		Calendar cal = Calendar.getInstance();
		int year =  cal.get(Calendar.YEAR);
		int mouth = cal.get(Calendar.MONTH) + 1;
		if(mouth >= 9)
		{
			return grand == year;
		}
		else
		{
			return grand == year - 1;
		}
	}
}
