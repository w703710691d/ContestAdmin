package api;
import user.UserService;

public class ApiService 
{
	public static String msg = null;
	public static String lastUrl = null;
	public static Boolean LoginCheck(String username, String password)
	{
		if(username.replace(" ", "").equals("") || password.replace(" ", "").equals(""))
		{
			msg = "账号密码不能为空";
			return false;
		}
		UserService.login(username, password);
		if(UserService.user == null)
			return false;
		return true;
	}
}
