package contest;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import api.ApiService;
import user.UserService;

public class ContestService 
{
	static public Record contest = null;
	
	static public String GetTitle()
	{
		if(contest == null) return null;
		else return contest.getStr("title");
	}
	
	static public Integer GetCid()
	{
		if(contest == null) return 0;
		else return contest.getInt("cid");
	}
	
	static public Boolean GetContest(Integer cid) 
	{
		contest = Db.findFirst("select * from contest where cid = ?", cid);
		if(contest == null)
		{
			ApiService.msg = "该比赛不存在";
			return false;
		}
		return true;
	}
	static public Integer GetType()
	{
		if(contest == null) return 0;
		else return contest.getInt("type");
	}
	static public List<Record> GetTeam() 
	{
		Integer cid = GetCid();
		List<Record> list = Db.find("Select * from team where cid = ?", cid);
		if(cid == 0) ApiService.msg = "该比赛不存在";
		return list;
	}
	
	static public Boolean CanReg()
	{
		Integer cid = GetCid();
		if(cid == 0) return false;
		Integer uid = UserService.GetUid();
		Integer type = GetType();
		if(type != 2) return false;
		if(uid == 0) return true;
		Integer tid = Db.queryInt("select tid from team where cid = ? and uid = ? and status != 2", cid, uid);
		if(tid == null) return true;
		return false;
	}
	
}
