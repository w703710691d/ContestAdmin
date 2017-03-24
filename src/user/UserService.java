package user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import api.ApiService;
import jodd.util.BCrypt;

public class UserService
{
	public static Record user = null;
	public static Integer GetUid()
	{
		if(user == null) return 0;
		return user.getInt("uid");
	}
	public static String GetUserName()
	{
		if(user == null) return null;
		return user.getStr("name");
	}
	public static void login(String username, String password)
	{
		user = Db.findFirst("select * from user where name = ?", username);
		if(user == null)
		{
			ApiService.msg =  "û�д��û�!";
			return;
		}
		if(!BCrypt.checkpw(password, user.getStr("password")))
		{
			ApiService.msg =  "�������!";
			user = null;
			return;
		}
		Boolean admin = Db.findFirst("select rid from user_role where uid = ?", GetUid()).getInt("rid").equals(1);
		user.set("admin", admin);
		ApiService.msg = "��½�ɹ�";
	}
	public static Boolean isadmin()
	{
		if(user == null) return false;
		return user.getBoolean("admin");
	}
	public static void logout()
	{
		if(user == null)
		{
			ApiService.msg = "δ��¼���޷�ע��!";
			return;
		}
		user = null;
		ApiService.msg = "ע���ɹ�!";
	}
}
