package com;
import java.util.List;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import jodd.util.BCrypt;
public class show extends Controller
{
	public void index() 
	{
		List<Record> list = Db.find("select cid,title,startTime,endTime from contest where type = 2 order by startTime DESC");
		setAttr("NodeList",list);
		setAttr("username",getSessionAttr("username"));
		setAttr("messege",getSessionAttr("messege"));
		setSessionAttr("lasturl", "/");
		removeSessionAttr("messege");
		render("/view/index.html");
	}
	public void login()
	{
		if(getSessionAttr("lasturl") == null)
		{
			setSessionAttr("lasturl" , "/");
		}
		setAttr("lasturl",getSessionAttr("lasturl"));
		String str = getRequest().getRemoteUser();
		System.out.println(str);
		if(getSessionAttr("uid") != null)
		{
			setSessionAttr("messege","已经登陆");
			redirect(getSessionAttr("lasturl").toString());
		}
		else
		{
			String username = getPara("username");
			if(username == null)
			{
				setAttr("loginmessege", "");
				render("view/login.html");
			}
			else
			{
				String password = getPara("password");
				if(username.replace(" ", "").equals("") || password.replace(" ", "").equals(""))
				{
					setAttr("loginmessege", "账号密码不能为空");
					render("view/login.html");
					return;
				}
				Record rd = Db.findFirst("select uid, password from user where name = ?", username);
				if(rd == null)
				{
					setAttr("loginmessege", "没有此用户");
					render("view/login.html");
					return;
				}
				if(BCrypt.checkpw(password, rd.getStr("password")))
				{
					Integer uid = rd.getInt("uid");
					boolean isadmin = Db.findFirst("select rid from user_role where uid = ?", uid).getInt("rid").equals(1);
					setSessionAttr("uid", uid);
					setSessionAttr("admin", isadmin);
					setSessionAttr("username", username);
					redirect(getSessionAttr("lasturl").toString());
				}
				else 
				{
					setAttr("loginmessege", "密码错误");
					render("view/login.html");
				}
			}
		}
	}
	public void logout()
	{
		removeSessionAttr("uid");
		removeSessionAttr("username");
		removeSessionAttr("admin");
		redirect(getSessionAttr("lasturl").toString());
	}
	boolean isadmin()
	{
		return getSessionAttr("admin") != null && getSessionAttr("admin").equals(true);
	}
}