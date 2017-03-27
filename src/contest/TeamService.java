package contest;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class TeamService 
{
	public static Record GetTeam(int uid, int cid)
	{
		return Db.findFirst("select * from team where cid = ? and uid = ?", cid, uid);
	}
}
