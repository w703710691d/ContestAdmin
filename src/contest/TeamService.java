package contest;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.Calendar;


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
	public static Record FormatTeam(Record team)
	{
		Record rd = new Record();
		int count  = 0;
		for(int i = 1; i <= 3; i++)
		{
			String name = team.getStr("name" + i);
			if(name == null) continue;
			name = name.replaceAll(" ","");
			if(name.equals("")) continue;
			count++;
			rd.set("name" + count, team.getStr("name" + i));
			rd.set("stuId" + count, team.getStr("stuId" + i));
			rd.set("college" + count, team.getStr("college" + i));
			rd.set("class" + count, team.getStr("class" + i));
			rd.set("contact" + count, team.getStr("contact" + i));
			rd.set("gender" + count, team.getStr("gender" + i));
		}
		if(count == 0) return null;
		for(int i = count + 1; i <= 3; i++)
		{
			rd.set("name" + i, null);
			rd.set("stuId" + i ,null);
			rd.set("college" + i, null);
			rd.set("class" + i, null);
			rd.set("contact" + i, null);
			rd.set("gender" + i, null);
		}
		rd.set("isRookieTeam",IsRookieTeam(rd));
		rd.set("isGirlTeam", IsGirlTeam(rd));
		if(team.get("tid") != null) rd.set("tid", team.get("tid"));
		if(team.get("uid") != null) rd.set("uid", team.get("uid"));
		if(team.get("notice") != null) rd.set("notice", team.get("notice"));
		if(team.get("status") != null) rd.set("status", team.get("status"));
		if(team.get("ctime") != null) rd.set("ctime", team.get("ctime"));
		if(team.get("mtime") != null) rd.set("mtime", team.get("mtime"));
		if(team.get("history") != null) rd.set("history", team.get("history"));
		if(team.get("cid") != null) rd.set("cid", team.get("cid"));
		if(team.get("isSpecialTeam") != null) rd.set("isSpecialTeam", team.get("isSpecialTeam"));
		if(team.get("teamNameChinese") != null) rd.set("teamNameChinese", team.get("teamNameChinese"));
		if(team.get("teamNameEnglish") != null) rd.set("teamNameEnglish", team.get("teamNameEnglish"));
		return rd;
	}
	public static boolean IsGirlTeam(Record team)
	{
		boolean isGirlTeam = true;
		for(int i = 1; i <= 3; i++)
		{
			String gender = team.getStr("gender" + i);
			if(gender == null) continue;
			if(gender.equals("male"))
			{
				isGirlTeam = false;
				break;
			}
		}
		return isGirlTeam;
	}
	public static boolean IsRookieTeam(Record team)
	{
		boolean isRookieTeam = true;
		for(int i = 1; i <=3 ; i++)
		{
			String stuId = team.getStr("stuId" + i);
			if(stuId == null) continue;
			if(!IsRookie(stuId))
			{
				isRookieTeam = false;
				break;
			}
		}
		return isRookieTeam;
	}
	public static boolean IsRookie(String stuid)
	{
		if(stuid.startsWith("51")) stuid = stuid.substring(2);
		if(stuid.length() < 4) return false;
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
}
