package api;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.main.admin.AdminService;
import contest.ContestService;
import contest.TeamService;
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
					render("/view/login.html");
					return;
				}
				Integer uid = UserService.login(username, password);
				if(uid == 0)
				{
					setSessionAttr("msg", "该用户不存在");
					render("/view/login.html");
					return;
				}
				if(uid == -1)
				{
					setSessionAttr("msg", "密码错误");
					render("/view/login.html");
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
	public void signin()
	{
		if(!getSessionAttr("uid").equals(0))
		{
			renderJson("{\"success\":false, \"result\":\"已经登陆\"}");
			return;
		}
		else
		{
			String username = getPara("username");
			String password = getPara("password");

			if(username == null || username.replace(" ", "").equals("") ||
					password == null || password.replace(" ", "").equals(""))
			{
				renderJson("{\"success\":false, \"result\":\"账号密码不能为空\"}");
				return;
			}

			Integer uid = UserService.login(username, password);
			if(uid == 0)
			{
				renderJson("{\"success\":false, \"result\":\"该用户不存在\"}");
				return;
			}
			if(uid == -1)
			{
				renderJson("{\"success\":false, \"result\":\"密码错误\"}");
				return;
			}

			setSessionAttr("admin", UserService.isadmin(uid));
			setSessionAttr("uid", uid);
			setSessionAttr("username", UserService.GetUserName(uid));
			renderJson("{\"success\":true}");
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

	public void register()
	{
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			setSessionAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		Integer uid = getSessionAttr("uid");
		if(uid == 0)
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return;
		}
		Record contest = ContestService.GetContest(cid);
		if(contest == null)
		{
			setSessionAttr("msg", "该比赛不存在");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		if(!ContestService.CanReg(uid, cid))
		{
			setSessionAttr("msg", "已经注册该比赛，无法再次注册");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record rd = new Record();
		String teamNameChinese = getPara("teamNameChinese");
		if(teamNameChinese.length() > 20)
		{
			setSessionAttr("msg", "中文名太长，大于了20字节");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		String teamNameEnglish = getPara("teamNameEnglish");
		if(teamNameEnglish.length() > 20)
		{
			setSessionAttr("msg", "英文名太长，大于了20字节");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		if(!ApiService.CheckAscii(teamNameEnglish))
		{
			setSessionAttr("msg", "英文名包含非英文字符");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		rd.set("teamNameChinese", teamNameChinese);
		rd.set("teamNameEnglish", teamNameEnglish);
		for(int i = 1; i <= 3; i++)
		{
			rd.set("name" + i, getPara("name" + i));
			rd.set("stuId" + i, getPara("stuId" + i));
			rd.set("college" + i, getPara("college" + i));
			rd.set("class" + i, getPara("class" + i));
			rd.set("gender" + i, getPara("gender" + i));
			rd.set("contact" + i, getPara("contact" + i));
		}
		rd.set("isSpecialTeam", getPara("isSpecialTeam"));
		rd = TeamService.FormatTeam(rd);
		if(rd == null)
		{
			setSessionAttr("msg", "队员信息不完整");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		rd.set("ctime", (int)(System.currentTimeMillis() / 1000));
		rd.set("mtime", (int)(System.currentTimeMillis() / 1000));
		rd.set("uid",uid);
		rd.set("cid", cid);
		rd.set("status", 0);
		rd.set("notice", 0);
		TeamService.SaveTeam(rd);
		setSessionAttr("msg", "注册成功");
		redirect("/contest/show/" + cid);
	}

	public void update()
	{
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setSessionAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		Record rd = TeamService.GetTeam(tid);
		if(rd == null)
		{
			setSessionAttr("msg", "该队伍不存在");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		int uid = getSessionAttr("uid");
		if(uid == 0)
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return;
		}
		if(getSessionAttr("admin").equals(false) && rd.getInt("uid") != uid)
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		String teamNameChinese = getPara("teamNameChinese");
		if(teamNameChinese.length() > 20)
		{
			setSessionAttr("msg", "中文名太长，大于了20字节");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		String teamNameEnglish = getPara("teamNameEnglish");
		if(teamNameEnglish.length() > 20)
		{
			setSessionAttr("msg", "英文名太长，大于了20字节");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		if(!ApiService.CheckAscii(teamNameEnglish))
		{
			setSessionAttr("msg", "英文名包含非英文字符");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		rd.set("teamNameChinese", teamNameChinese);
		rd.set("teamNameEnglish", teamNameEnglish);
		for(int i = 1; i <= 3; i++)
		{
			rd.set("name" + i, getPara("name" + i));
			rd.set("stuId" + i, getPara("stuId" + i));
			rd.set("college" + i, getPara("college" + i));
			rd.set("class" + i, getPara("class" + i));
			rd.set("gender" + i, getPara("gender" + i));
			rd.set("contact" + i, getPara("contact" + i));
		}
		rd.set("isSpecialTeam", getPara("isSpecialTeam"));
		rd = TeamService.FormatTeam(rd);
		if(rd == null)
		{
			setSessionAttr("msg", "队员信息不完整");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		String history = rd.getStr("history");
		if(history == null) history = "";
		history = AdminService.BuildHistory(history, getSessionAttr("username").toString(), rd.getInt("status"), 0);
		int now = (int)(System.currentTimeMillis() / 1000);
		rd.set("mtime", now).set("history", history).set("status", 0);

		TeamService.UpdateTeam(rd);
		setSessionAttr("msg" ,"修改成功");

		Integer cid = rd.getInt("cid");
		redirect( "/contest/show/" + cid);

	}
}
