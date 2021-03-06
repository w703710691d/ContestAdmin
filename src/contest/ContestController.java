package contest;

import com.jfinal.plugin.activerecord.Record;


import com.jfinal.core.Controller;

public class ContestController extends Controller
{
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

		Integer currentPage = getParaToInt(1);

		if(currentPage == null) currentPage = 1;
		int totalPage = (ContestService.GetTeamNum(cid) + 19) / 20;

		setAttr("cid", cid);
		setAttr("currentPage", currentPage);
		setAttr("totalPage", totalPage);
		setAttr("username", getSessionAttr("username"));
		setAttr("admin", getSessionAttr("admin"));
		setAttr("msg", getSessionAttr("msg"));
		setAttr("uid", uid);
		setAttr("TeamList", ContestService.GetTeam(cid, currentPage));
		setAttr("reg", ContestService.CanReg(uid, cid));
		setAttr("contest", contest);
		setAttr("accepted", ContestService.GetAccptedTeamNum(cid));
		setAttr("rookie", ContestService.GetRookieTeamNum(cid));
		setAttr("girl", ContestService.GetGirlTeamNum(cid));
		setAttr("id",(currentPage - 1) * 20);

		setSessionAttr("msg", null);
		setSessionAttr("lasturl", "/contest/show/" + cid + "-" + currentPage);
		
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
			setSessionAttr("msg", "已经注册该比赛或现在不能注册该比赛");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		setAttr("title", contest.getStr("title"));
		setAttr("username", getSessionAttr("username"));
		setAttr("cid", cid);
		setAttr("msg", getSessionAttr("msg"));
		setAttr("lasturl", getSessionAttr("lasturl"));
		setAttr("team",new Record());

		setSessionAttr("lasturl", "/contest/register/" +  getParaToInt(0));
		setSessionAttr("msg", null);

		render("/view/register.html");
	}
	public void detail()
	{
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setSessionAttr("msg",  "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		int uid = getSessionAttr("uid");
		if(uid == 0)
		{
			setSessionAttr("msg", "请先登录");
			redirect("/api/login");
			return ;
		}
		Record team = TeamService.GetTeam(tid);
		if(team == null)
		{
			setSessionAttr("msg", "没有该队伍");
			redirect(getSessionAttr("lasturl").toString());
			return;
		}
		if(team.getInt("uid") != uid)
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record contest = ContestService.GetContest(team.getInt("cid"));

		setAttr("contest",contest);
		setAttr("msg", getSessionAttr("msg"));
		setAttr("team",team);
		setAttr("tid", tid);
		setAttr("username", getSessionAttr("username"));
		setAttr("lasturl", getSessionAttr("lasturl"));
		setAttr("admin", getSessionAttr("admin"));

		setSessionAttr("lasturl","/contest/detail/" + tid);
		setSessionAttr("msg", null);

		render("/view/detail.html");
	}
	public void modify()
	{
		int uid = getSessionAttr("uid");
		if(uid == 0)
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return ;
		}
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setSessionAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record team = TeamService.GetTeam(tid);
		if(team == null)
		{
			setSessionAttr("msg", "该队伍不存在");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}

		if(team.getInt("uid") != uid)
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record contest = ContestService.GetContest(team.getInt("cid"));
		long now = System.currentTimeMillis();
		if(now  >= (contest.getInt("startTime") - 24*60*60) * 1000L)
		{
			setSessionAttr("msg", "现在无法修改报名信息");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}

		setAttr("username",getSessionAttr("username"));
		setAttr("team",team);
		setAttr("modify",true);
		setAttr("msg", getSessionAttr("msg"));
		setAttr("title", contest.getStr("title"));
		setAttr("lasturl", getSessionAttr("lasturl"));
		setSessionAttr("msg", null);
		setSessionAttr("lasturl", "/contest/modify/"+ tid);
		render("/view/register.html");
	}
}