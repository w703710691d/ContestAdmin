package com;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
public class admin extends Controller
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
		setAttr("messege", getSessionAttr("messege"));
		removeSessionAttr("messege");
		if(!isadmin())
		{
			setSessionAttr("messege","没有权限");
			redirect("/");
			return;
		}
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
			setSessionAttr("messege","参数错误");
			redirect("/");
			return;
		}
		setAttr("team",team);
		setAttr("tid", tid);
		setSessionAttr("lasturl","/admin" + tid);
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
	boolean isrookie(String stuid)
	{
		if(stuid.startsWith("51")) stuid= stuid.substring(2);
		if(stuid.length()<4) return false;
		Integer grand = 0;
		try
		{
			grand = Integer.valueOf(stuid.substring(0, 4));
		}
		catch(Exception ex)
		{
			return false;
		}
		Calendar cal = Calendar.getInstance();
		int year =  cal.get(Calendar.YEAR);
		int mouth = cal.get(Calendar.MONTH) + 1;
		if(mouth >= 9) 
		{
			return grand == year;
		}
		else 
		{
			return grand == year - 1;
		}
	}
	public void register()
	{
		Integer cid = getParaToInt(0);
		Integer uid = getSessionAttr("uid");
		if(cid == null ||  uid == null)
		{
			redirect("/");
			return;
		}
		Integer tid = Db.queryInt("select tid from team where cid = ? and uid = ?", cid, uid);
		if(tid != null)
		{
			setSessionAttr("messege", "已经注册，无法再次注册");
			redirect("/contest/show/" + cid);
			return ;
		}
		Record rd = new Record();
		rd.set("ctime", (int)(System.currentTimeMillis()/1000));
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
			setSessionAttr("messege", "填写信息错误");
			redirect("/contest/register/" + cid);
			return ;
		}
		else
		{
			if(!isrookie(rd.getStr("stuId1")))
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
			if(!isrookie(rd.getStr("stuId2")))
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
			if(!isrookie(rd.getStr("stuId3")))
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
		Db.save("team", rd);
		setSessionAttr("messege", "注册成功");
		redirect("/contest/show/" + cid);
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
			if(!isrookie(rd.getStr("stuId1")))
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
			if(!isrookie(rd.getStr("stuId2")))
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
			if(!isrookie(rd.getStr("stuId3")))
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
