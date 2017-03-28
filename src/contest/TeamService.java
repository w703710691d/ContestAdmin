package contest;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class TeamService 
{
	public static Record GetTeam(int uid, int cid)
	{
		return Db.findFirst("select * from team where cid = ? and uid = ?", cid, uid);
	}
	public static Record GetTeam(int tid)
	{
		return Db.findFirst("select * from team where tid = ?", tid);
	}
	public static void SaveTeam(Record team)
	{
		Db.save("team", team);
	}
	public static  void UpdateTeam(Record team)
	{
		Db.update("team", "tid", team);
	}
}
