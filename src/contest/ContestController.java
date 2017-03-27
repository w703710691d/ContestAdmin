package contest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
		
		render("/view/contest.html");
	}
	public void register()
	{
		if(getSessionAttr("uid") == null)
		{
			redirect("/login");
			return ;
		}
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			setSessionAttr("messege", "参数错误");
			redirect("/");
			return ;
		}
		String title = Db.queryFirst("select title from contest where cid = ?",cid);
		if(title == null)
		{
			setSessionAttr("messege", "注册比赛不存在");
			redirect("/");
			return ;
		}
		Integer uid = getSessionAttr("uid");
		Integer tid = Db.queryInt("select tid from team where cid = ? and uid = ?", cid, uid);
		if(tid != null)
		{
			setSessionAttr("messege", "已经注册该比赛");
			redirect("/contest/show" + cid);
		}
		setSessionAttr("lasturl", "/contest/register/" +  getParaToInt(0));
		setAttr("title",title);
		setAttr("username",getSessionAttr("username"));
		setAttr("cid", cid);
		setAttr("messege",getSessionAttr("messege"));
		removeSessionAttr("messege");
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