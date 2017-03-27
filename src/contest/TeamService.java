package contest;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import api.ApiService;
import user.UserService;

public class TeamService 
{
	public static Record team = null;
	public static Integer GetUid()
	{
		if(team == null) return 0;
		return team.getInt("uid");
	}
	public static Boolean CanAccess()
	{
		if(UserService.GetUid() == 0) return false;
		if(team == null) return false;
		if(UserService.isadmin()) return true;
		if(UserService.GetUid() == GetUid()) return true;
		return false;
	}
	public static void GetTeam(Integer tid)
	{
		team = 	Db.findFirst("select * from team where cid = ?", tid);
		if(team == null)
		{
			ApiService.msg = "该队伍不存在";
		}
	}
}
