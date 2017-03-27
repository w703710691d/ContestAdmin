package contest;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


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
			setSessionAttr("msg", "参数非法");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record contest = ContestService.GetContest(cid);
		if(contest == null)
		{
			setSessionAttr("msg", "该比赛不存在");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		if(contest.getInt("type") != 2)
		{
			setSessionAttr("msg", "无法查看该比赛注册信息");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		int uid = getSessionAttr("uid");
		
		setAttr("username", getSessionAttr("username"));
		setAttr("admin", getSessionAttr("admin"));
		setAttr("msg", getSessionAttr("msg"));
		setAttr("uid", uid);
		setAttr("title", contest.getStr("title"));
		setAttr("cid", contest.getInt("cid"));
		setAttr("TeamList", ContestService.GetTeam(cid));
		setAttr("reg", ContestService.CanReg(uid, cid));
		
		setSessionAttr("msg", null);
		setSessionAttr("lasturl", "/contest/show/" + cid);
		
		render("/view/contest.html");
	}
	public void register()
	{
		Integer uid = getSessionAttr("uid");
		if(uid == 0)
		{
			setSessionAttr("msg", "请先登录");
			redirect("/api/login");
			return ;
		}
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			setSessionAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record contest = ContestService.GetContest(cid);
		if(contest == null)
		{
			setSessionAttr("msg", "注册比赛不存在");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		if(!ContestService.CanReg(uid, cid))
		{
			setSessionAttr("msg", "已经注册该比赛");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		
		
		setAttr("title", contest.getStr("title"));
		setAttr("username", getSessionAttr("username"));
		setAttr("cid", cid);
		setAttr("msg", getSessionAttr("msg"));
		
		setSessionAttr("lasturl", "/contest/register/" +  getParaToInt(0));
		setSessionAttr("msg", null);
		
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