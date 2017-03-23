package com;

import java.util.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.core.Controller;

public class contest extends Controller
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
			redirect("/");
			return ;
		}
	    
		String title = Db.queryFirst("select title from contest where cid = ?",cid);
		if(title == null)
		{
			redirect("/");
			return ;
		}
		setSessionAttr("lasturl", "/contest/show/" + cid);
		setAttr("username",getSessionAttr("username"));
		setAttr("admin",getSessionAttr("admin"));
		setAttr("title",title);
		setAttr("cid",cid);
		List<Record> list = Db.find("Select * from team where cid = ?", cid);
		int cnt = 0;
		for(Record i : list)	
		{
			i.set("id", ++cnt);
		}
		setAttr("TeamList",list);
		setAttr("messege",getSessionAttr("messege"));
		removeSessionAttr("messege");
		Integer uid = getSessionAttr("uid");
		if(uid == null)
		{
			setAttr("reg",true);
		}
		else
		{
			Integer tid = Db.queryInt("select tid from team where cid = ? and uid = ? and status != 2", cid, uid);
			if(tid == null)
			{
				setAttr("reg",true);
			}
			else 
			{
				setAttr("reg",false);
			}
		}
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
			setSessionAttr("messege","��������");
			redirect("/");
			return ;
		}
		String title = Db.queryFirst("select title from contest where cid = ?",cid);
		if(title == null)
		{
			setSessionAttr("messege","ע�����������");
			redirect("/");
			return ;
		}
		Integer uid = getSessionAttr("uid");
		Integer tid = Db.queryInt("select tid from team where cid = ? and uid = ?", cid, uid);
		if(tid != null)
		{
			setSessionAttr("messege","�Ѿ�ע��ñ���");
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
			setSessionAttr("messege","��������");
			redirect("/");
			return;
		}
		Record team = Db.findFirst("select * from team where tid = ?", tid);
		if(team == null)
		{
			setSessionAttr("messege","��������");
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
			setSessionAttr("messege","��������");
			redirect("/");
			return;
		}
		Record team = Db.findFirst("select * from team where tid = ?", tid);
		if(team == null)
		{
			setSessionAttr("messege","��������");
			redirect("/");
			return;
		}
		boolean can = isadmin() || (getSessionAttr("uid")!=null && team.getInt("uid").equals(getSessionAttr("uid")));
		if(!can)
		{
			setSessionAttr("messege","û��Ȩ��");
			redirect("/");
			return;
		}
		setAttr("username",getSessionAttr("username"));
		setAttr("team",team);
		
		render("/view/modify.html");
	}
}