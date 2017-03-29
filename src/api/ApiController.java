package api;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.main.admin.AdminService;
import com.sun.prism.impl.Disposer;
import contest.ContestService;
import contest.TeamService;
import user.UserService;

import javax.mail.search.RecipientStringTerm;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		rd.set("ctime", (int)(System.currentTimeMillis() / 1000));
		rd.set("mtime", (int)(System.currentTimeMillis() / 1000));
		rd.set("name1", getPara("name1"));
		rd.set("stuId1", getPara("stuId1"));
		rd.set("college1", getPara("college1"));
		rd.set("class1", getPara("class1"));
		rd.set("contact1", getPara("contact1"));
		rd.set("gender1", getPara("gender1"));
		rd.set("name2", getPara("name2"));
		rd.set("stuId2", getPara("stuId2"));
		rd.set("college2", getPara("college2"));
		rd.set("class2", getPara("class2"));
		rd.set("contact2", getPara("contact2"));
		rd.set("gender2", getPara("gender2"));
		rd.set("name3", getPara("name3"));
		rd.set("stuId3", getPara("stuId3"));
		rd.set("college3", getPara("college3"));
		rd.set("class3", getPara("class3"));
		rd.set("contact3", getPara("contact3"));
		rd.set("gender3", getPara("gender3"));
		rd.set("isSpecialTeam", getPara("isSpecialTeam"));
		boolean isgirl = true, isrookie = true;
		if(rd.getStr("name1").replaceAll(" ","").isEmpty() || rd.getStr("stuId1").replaceAll(" ","").isEmpty() || rd.getStr("college1").replaceAll(" ","").isEmpty()
				||rd.getStr("class1").replaceAll(" ","").isEmpty() || rd.getStr("contact1").replaceAll(" ","").isEmpty())
		{
			setSessionAttr("msg", "队员一信息必须完整");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId1")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender1").equals("male"))
			{
				isgirl = false;
			}
		}
		if(rd.getStr("name2").replaceAll(" ","").isEmpty())
		{
			rd.set("name2", null).set("stuId2", null).set("college2", null).set("class2", null).set("gender2", null).set("contact2", null);
			rd.set("name3", null).set("stuId3", null).set("college3", null).set("class3", null).set("gender3", null).set("contact3", null);
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId2")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender2").equals("male"))
			{
				isgirl = false;
			}
		}
		if(rd.getStr("name3") == null || rd.getStr("name3").replaceAll(" ","").isEmpty())
		{
			rd.set("name3", null).set("stuId3", null).set("college3", null).set("class3", null).set("gender3", null).set("contact3", null);
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId3")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender3").equals("male"))
			{
				isgirl = false;
			}
		}
		rd.set("uid",uid);
		rd.set("cid", cid);
		rd.set("status", 0);
		rd.set("notice", 0);
		rd.set("isRookieTeam", isrookie);
		rd.set("isGirlTeam", isgirl);
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
		rd.set("name1", getPara("name1"));
		rd.set("stuId1", getPara("stuId1"));
		rd.set("college1", getPara("college1"));
		rd.set("class1", getPara("class1"));
		rd.set("contact1", getPara("contact1"));
		rd.set("gender1", getPara("gender1"));
		rd.set("name2", getPara("name2"));
		rd.set("stuId2", getPara("stuId2"));
		rd.set("college2", getPara("college2"));
		rd.set("class2", getPara("class2"));
		rd.set("contact2", getPara("contact2"));
		rd.set("gender2", getPara("gender2"));
		rd.set("name3", getPara("name3"));
		rd.set("stuId3", getPara("stuId3"));
		rd.set("college3", getPara("college3"));
		rd.set("class3", getPara("class3"));
		rd.set("contact3", getPara("contact3"));
		rd.set("gender3", getPara("gender3"));
		rd.set("isSpecialTeam", getPara("isSpecialTeam"));
		boolean isgirl = true, isrookie = true;
		if(rd.getStr("name1").replaceAll(" ","").isEmpty() || rd.getStr("stuId1").replaceAll(" ","").isEmpty() || rd.getStr("college1").replaceAll(" ","").isEmpty()
				||rd.getStr("class1").replaceAll(" ","").isEmpty() || rd.getStr("contact1").replaceAll(" ","").isEmpty())
		{
			setSessionAttr("msg", "队员一信息必须完整");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId1")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender1").equals("male"))
			{
				isgirl = false;
			}
		}
		if(rd.getStr("name2").replaceAll(" ","").isEmpty())
		{
			rd.set("name2", null).set("stuId2", null).set("college2", null).set("class2", null).set("gender2", null).set("contact2", null);
			rd.set("name3", null).set("stuId3", null).set("college3", null).set("class3", null).set("gender3", null).set("contact3", null);
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId2")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender2").equals("male"))
			{
				isgirl = false;
			}
		}
		if(rd.getStr("name3") == null || rd.getStr("name3").replaceAll(" ","").isEmpty())
		{
			rd.set("name3", null).set("stuId3", null).set("college3", null).set("class3", null).set("gender3", null).set("contact3", null);
		}
		else
		{
			if(!ApiService.isrookie(rd.getStr("stuId3")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender3").equals("male"))
			{
				isgirl = false;
			}
		}
		rd.set("notice", 0);
		rd.set("isRookieTeam", isrookie);
		rd.set("isGirlTeam", isgirl);
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
