package contest;

import java.util.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import api.ApiService;
import user.UserService;

import com.jfinal.core.Controller;

public class ContestController extends Controller
{
	boolean isadmin()
	{
		return getSessionAttr("admin") != null && getSessionAttr("admin").equals(true);
	}
	public void show()
	{
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			ApiService.msg = "参数非法!";
			redirect("/");
			return ;
		}
		if(!ContestService.GetContest(cid))
		{
			redirect("/");
			return ;
		}
		if(ContestService.GetType() != 2)
		{
			ApiService.msg =  "无法查看该比赛注册信息!";
			redirect("/");
			return ;
		}
		ApiService.lastUrl = "/contest/show/" + cid;
		setAttr("username", UserService.GetUserName());
		setAttr("admin", UserService.isadmin());
		setAttr("title", ContestService.GetTitle());
		setAttr("cid", ContestService.GetCid());
		
		List<Record> list = ContestService.GetTeam();
		
		setAttr("TeamList",list);
		setAttr("msg", ApiService.msg);
		ApiService.msg = null;
		
		Boolean canReg = ContestService.CanReg();
		setAttr("reg", canReg);
		setAttr("uid", UserService.GetUid());
		render("/view/contest.html");
	}
	public void register()
	{
		if(UserService.GetUid() == 0)
		{
			ApiService.msg = "请先登录";
			redirect("/api/login");
			return ;
		}
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			ApiService.msg = "参数错误";
			redirect("/");
			return ;
		}
		ContestService.GetContest(cid);
		if(ContestService.GetCid() == 0)
		{
			ApiService.msg = "注册比赛不存在";
			redirect("/");
			return ;
		}
		if(!ContestService.CanReg())
		{
			ApiService.msg = "已经注册该比赛";
			redirect("/contest/show" + cid);
			return ;
		}
		ApiService.lastUrl = "/contest/register/" +  getParaToInt(0);
		setAttr("title", ContestService.GetTitle());
		setAttr("username", UserService.GetUserName());
		setAttr("cid", cid);
		setAttr("msg", ApiService.msg);
		ApiService.msg = null;
		render("/view/register.html");
	}
	public void detail()
	{
		setAttr("messege", getSessionAttr("messege"));
		removeSessionAttr("messege");
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setSessionAttr("messege",  "参数错误");
			redirect("/");
			return;
		}
		Record team = Db.findFirst("select * from team where tid = ?", tid);
		if(team == null)
		{
			setSessionAttr("messege", "参数错误");
			redirect("/");
			return;
		}
		setAttr("canmodify", isadmin() || (getSessionAttr("uid")!=null && team.getInt("uid").equals(getSessionAttr("uid"))));
		setAttr("team",team);
		setAttr("tid", tid);
		setSessionAttr("lasturl","/contest/detail/" + tid);
		render("/view/detail.html");
	}
	public void modify()
	{
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setSessionAttr("messege","参数错误");
			redirect("/");
			return;
		}
		Record team = Db.findFirst("select * from team where tid = ?", tid);
		if(team == null)
		{
			setSessionAttr("messege", "参数错误");
			redirect("/");
			return;
		}
		boolean can = isadmin() || (getSessionAttr("uid")!=null && team.getInt("uid").equals(getSessionAttr("uid")));
		if(!can)
		{
			setSessionAttr("messege","没有权限");
			redirect("/");
			return;
		}
		setAttr("username",getSessionAttr("username"));
		setAttr("team",team);
		
		render("/view/modify.html");
	}
}