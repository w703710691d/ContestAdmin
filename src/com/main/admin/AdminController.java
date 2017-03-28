package com.main.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import api.ApiService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import contest.TeamService;

public class AdminController extends Controller
{
	boolean isadmin()
	{
		return getSessionAttr("admin") != null && getSessionAttr("admin").equals(true);
	}
	String getstatus(int status)
	{
		if(status == 0) return "<font color = \"green\">Pending</font>";
		else if(status == 1) return "<font color = \"red\">Accepted</font>";
		else return "<font color = \"black\">Rejected</font>";
	}
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
			setAttr("msg", "没有权限");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Integer tid = getParaToInt(0);
		if(tid == null)
		{
			setAttr("msg", "参数错误");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}
		Record team = TeamService.GetTeam(tid);
		if(team == null)
		{
			setAttr("msg", "该队伍不存在");
			redirect(getSessionAttr("lasturl").toString());
			return ;
		}

		setAttr("msg", getSessionAttr("msg"));
		setAttr("team",team);
		setAttr("tid", tid);
		setAttr("username", getSessionAttr("username"));

		setSessionAttr("lasturl","/admin/" + tid);
		setSessionAttr("msg", null);
		render("/view/admin.html");
	}
	public void update()
	{
		setAttr("messege", getSessionAttr("messege"));
		removeSessionAttr("messege");
		if(!isadmin())
		{
			setSessionAttr("messege","没有权限");
			redirect("/");
			return ;
		}
		Integer tid = getParaToInt(0);
		Record  rd = Db.findFirst("select * from team where tid = ?", tid);
		Integer status = getParaToInt("status");
		String comment = getPara("comment");
		String username = getSessionAttr("username");
		Integer cid = rd.getInt("cid");
		String history = rd.getStr("history");
		if(history == null) history = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		history += df.format(new Date()) + " " + username + ":" + getstatus(rd.getInt("status")) + "-->" + getstatus(status) + "</br>";
		int now = (int)(System.currentTimeMillis()/1000);
		rd.set("mtime", now).set("history", history).set("comment", comment).set("status", status);
		Db.update("team", "tid", rd);
		setSessionAttr("messege","修改成功");
		redirect( "/contest/show/" + cid);
	}


	public void updateteam()
	{
		Integer tid = getParaToInt(0);
		Integer uid = getSessionAttr("uid");
		if(tid == null ||  uid == null)
		{
			setSessionAttr("messege","参数错误");
			redirect("/");
			return;
		}
		Record rd = Db.findById("team", "tid", tid);
		if(rd == null)
		{
			setSessionAttr("messege","参数错误");
			redirect("/");
			return ;
		}
		if(!isadmin() && uid != rd.getInt("uid"))
		{
			setSessionAttr("messege","没有权限!");
			redirect("/");
		}
		Integer cid = rd.getInt("cid");
		rd.set("mtime", (int)(System.currentTimeMillis()/1000));
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
			setSessionAttr("messege","填写信息错误");
			redirect("/contest/register/" + cid);
			return ;
		}
		else
		{
			if(rd.get("gender1") == null)
			{
				setSessionAttr("messege","填写信息错误");
				redirect("/contest/register/" + cid);
			}
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
			if(rd.get("gender2") == null)
			{
				setSessionAttr("messege","填写信息错误");
				redirect("/contest/register/" + cid);
			}
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
			if(rd.get("gender3") == null)
			{
				setSessionAttr("messege","填写信息错误");
				redirect("/contest/register/" + cid);
			}
			if(!ApiService.isrookie(rd.getStr("stuId3")))
			{
				isrookie = false;
			}
			if(rd.getStr("gender3").equals("male"))
			{
				isgirl = false;
			}
		}
		String history = rd.getStr("history");
		if(history == null) history = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		history += df.format(new Date()) + " " + getSessionAttr("username") + ":" + getstatus(rd.getInt("status")) + "-->" + getstatus(0) + "</br>";
		int now = (int)(System.currentTimeMillis()/1000);
		rd.set("uid",uid);
		rd.set("cid", cid);
		rd.set("status", 0);
		rd.set("notice", 0);
		rd.set("isRookieTeam", isrookie);
		rd.set("isGirlTeam", isgirl);
		rd.set("history", history);
		rd.set("mtime",now);
		Db.update("team", "tid", rd);
		setSessionAttr("messege","修改成功");
		redirect("/contest/show/" + cid);
	}
}
