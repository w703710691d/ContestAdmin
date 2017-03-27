package user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import jodd.util.BCrypt;

public class UserService
{
	public static Record user = null;
	public static Integer GetUid()
	{
		if(user == null) return 0;
		return user.getInt("uid");
	}
	public static String GetUserName(int uid)
	{
		return Db.queryStr("select name from user where uid = ?", uid);
	}
	public static Integer login(String username, String password) 
	{
		user = Db.findFirst("select * from user where name = ?", username);
		if(user == null)
		{
			return 0;
		}
		if(!BCrypt.checkpw(password, user.getStr("password")))
		{
			return -1;
		}
		return user.getInt("uid");
	}
	public static Boolean isadmin(int uid)
	{
		Integer role = Db.queryInt("select rid from user_role where uid = ?", uid);
		if(role == null || role != 1) return false;
		return true;
	}

}
