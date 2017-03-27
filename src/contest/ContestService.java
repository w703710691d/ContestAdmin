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
	static public List<Record> GetTeam(Integer cid) 
	{
		return Db.find("Select * from team where cid = ?", cid);
	}
	
	static public Boolean CanReg(Integer uid, Integer cid)
	{
		Record team = TeamService.GetTeam(uid, cid);
		if(team == null) return true;
		return false;
	}
	
}
