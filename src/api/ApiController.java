package api;

import com.jfinal.core.Controller;
import user.UserService;

public class ApiController extends Controller
{
	public void login()
	{	
		if(!getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "已经登陆");
			redirect(getSessionAttr("lasturl").toString());
		}
		else
		{
			setAttr("lasturl", getSessionAttr("lasturl").toString());
			setAttr("msg", getSessionAttr("msg"));
			
			String username = getPara("username");
			if(username == null)
			{
				setSessionAttr("msg", null);
				render("/view/login.html");
			}
			else
			{
				String password = getPara("password");
				if(username.replace(" ", "").equals("") || password.replace(" ", "").equals(""))
				{
					setSessionAttr("msg", "账号密码不能为空");
					redirect("/api/login");
					return;
				}
				Integer uid = UserService.login(username, password);
				if(uid == 0)
				{
					setSessionAttr("msg", "该用户不存在");
					redirect("/api/login");
					return;
				}
				if(uid == -1)
				{
					setSessionAttr("msg", "密码错误");
					redirect("/api/login");
					return;
				}
				setSessionAttr("msg", "登录成功");
				setSessionAttr("admin", UserService.isadmin(uid));
				setSessionAttr("uid", uid);
				setSessionAttr("username", UserService.GetUserName(uid));
				redirect(getSessionAttr("lasturl").toString());
			}
		}
	}
	public void logout()
	{
		if(getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "未登录，无法注销");
		}
		else
		{
			setSessionAttr("uid", 0);
			setSessionAttr("admin", false);
			setSessionAttr("username", null);
			setSessionAttr("msg", "注销成功");
		}
		redirect(getSessionAttr("lasturl").toString());
	}
}
