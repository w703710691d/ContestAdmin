package com.main.admin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import api.ApiService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import contest.ContestService;
import contest.TeamService;

public class AdminController extends Controller
{

	public void index()
	{
		if(getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return ;
		}
		if(getSessionAttr("admin").equals(false))
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
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

		setAttr("msg", getSessionAttr("msg"));
		setAttr("team",team);
		setAttr("tid", tid);
		setAttr("username", getSessionAttr("username"));
		setAttr("lasturl", getSessionAttr("lasturl"));
		setAttr("admin", getSessionAttr("admin"));

		setSessionAttr("lasturl","/admin/" + tid);
		setSessionAttr("msg", null);
		render("/view/detail.html");
	}
	public void update()
	{
		if(getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return ;
		}
		if(getSessionAttr("admin").equals(false))
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
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
		Integer status = getParaToInt("status");
		String comment = getPara("comment");
		if(status == null) status = 0;
		if(comment == null) comment = "";

		String history = team.getStr("history");
		if(history == null) history = "";
		history = AdminService.BuildHistory(history, getSessionAttr("username").toString(), team.getInt("status"), status);

		int now = (int)(System.currentTimeMillis() / 1000);
		team.set("mtime", now).set("history", history).set("comment", comment).set("status", status);

		TeamService.UpdateTeam(team);
		setSessionAttr("msg" ,"修改成功");

		Integer cid = team.getInt("cid");
		redirect( "/contest/show/" + cid);
	}

	public void modify()
	{
		if(getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return ;
		}
		if(getSessionAttr("admin").equals(false))
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
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
		Record contest = ContestService.GetContest(team.getInt("cid"));
		setAttr("username",getSessionAttr("username"));
		setAttr("team",team);
		setAttr("modify",true);
		setAttr("msg", getSessionAttr("msg"));
		setAttr("title", contest.getStr("title"));
		setAttr("lasturl", getSessionAttr("lasturl"));
		setSessionAttr("msg", null);
		setSessionAttr("lasturl", "/admin/modify/"+ tid);
		render("/view/register.html");
	}
	public void generate()
	{
		if(getSessionAttr("uid").equals(0))
		{
			setSessionAttr("msg", "请先登陆");
			redirect("/api/login");
			return ;
		}
		if(getSessionAttr("admin").equals(false))
		{
			setSessionAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Integer cid = getParaToInt(0);
		if(cid == null)
		{
			setSessionAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record  contest = ContestService.GetContest(cid);
		if(contest == null)
		{
			setSessionAttr("msg", "该比赛不存在");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		if (contest.getInt("atime") != null)
		{
			setSessionAttr("msg", "该比赛账号已经生产");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		contest.set("atime", (int)(System.currentTimeMillis() / 1000));
		Db.update("contest","cid",contest);

		File file = AdminService.getAccountFile(cid);
		renderFile(file);

	}
}
