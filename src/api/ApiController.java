package api;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jfinal.core.Controller;
import user.UserService;

public class ApiController extends Controller
{
	public void login()
	{
		setAttr("lasturl", ApiService.lastUrl);
		if(UserService.GetUid() != 0)
		{
			setAttr("msg", "已经登陆");
			redirect(ApiService.lastUrl);
		}
		else
		{
			String username = getPara("username");
			if(username == null)
			{
				setAttr("msg", null);
				render("/view/login.html");
			}
			else
			{
				Boolean loginResult = ApiService.LoginCheck(username, getPara("password"));
				setAttr("msg",ApiService.msg);
				if(loginResult.equals(true))
					redirect(ApiService.lastUrl);
				else
					render("/view/login.html");
			}
		}
	}
	public void logout()
	{
		UserService.logout();
		redirect(ApiService.lastUrl);
	}
}
