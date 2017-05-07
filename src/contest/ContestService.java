package contest;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class ContestService 
{
	static public Record contest = null;
	
	
	static public Record GetContest(Integer cid) 
	{
		return Db.findFirst("select * from contest where cid = ? and type = 2", cid);
	}
	static public List<Record> GetTeam(Integer cid, int idx)
	{
		return Db.find("Select * from team where cid = ? LIMIT ?, 20", cid, (idx - 1) * 20);
	}
	static public List<Record> GetAcTeam(Integer cid)
	{
		return Db.find("Select * from team where cid = ? and status = 1", cid);
	}
	static public Boolean CanReg(Integer uid, Integer cid)
	{
		Record contest = GetContest(cid);
		long now = System.currentTimeMillis();
		if(now  >= (contest.getInt("startTime") - 24*60*60) * 1000L) return false;
		Record team = TeamService.GetTeam(uid, cid);
		if(team == null) return true;
		return false;
	}

	static public Integer GetTeamNum(Integer cid)
	{
		String s = Db.query("select count(*) from team where cid = ?", cid).toString();
		s = s.substring(1,s.length() - 1);
		return Integer.parseInt(s);
	}
	static public Integer GetAccptedTeamNum(Integer cid)
	{
		String s = Db.query("select count(*) from team where cid = ? and status = 1 and isSpecialTeam = 0", cid).toString();
		s = s.substring(1,s.length() - 1);
		return Integer.parseInt(s);
	}
	static public Integer GetRookieTeamNum(Integer cid)
	{
		String s = Db.query("select count(*) from team where cid = ? and status = 1 and isRookieTeam", cid).toString();
		s = s.substring(1,s.length() - 1);
		return Integer.parseInt(s);
	}
	static public Integer GetGirlTeamNum(Integer cid)
	{
		String s = Db.query("select count(*) from team where cid = ? and status = 1 and isGirlTeam", cid).toString();
		s = s.substring(1,s.length() - 1);
		return Integer.parseInt(s);
	}
}
