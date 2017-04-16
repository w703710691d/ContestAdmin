package com.main.admin;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import contest.ContestService;
import jodd.util.BCrypt;

import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminService
{
    public static String getstatus(int status)
    {
        if(status == 0)
            return "<p class = \"btn btn-info\" style=\"width: 70px;\">Pending</p>";
        else if(status == 1)
            return "<p class = \"btn btn-success\" style=\"width: 70px;\">Accepted</p>";
        else return
                    "<p class = \"btn btn-danger\" style=\"width: 70px;\">Reject</p>";
    }
    public static String BuildHistory(String history, String username, int status, int newStatus)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        history += "<div>" + df.format(new Date()) + " " + username + ":" + AdminService.getstatus(status)
                + "<i class=\"fa fa-arrow-right\"></i>" + AdminService. getstatus(newStatus) + "</div>";
        return history;
    }
    public static String getPassowrd()
    {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 8; i++)
        {
            int number = (int)Math.floor(Math.random() * base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public static File getAccountFile(int cid)
    {
        File file = new File("/var/www/download/account-" + cid + ".txt");
        try
        {
            PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
            List<Record> Team = ContestService.GetAcTeam(cid);
            int idx = 0;
            for(Record team : Team)
            {
                Record user = Db.findById("user","uid", team.getInt("uid"));
                if(user == null) continue;
                user.remove("uid");
                user.set("tid",team.getInt("tid"));
                String name = "team" + cid + String.format("%03d", ++idx);
                user.set("name", name);
                String password = getPassowrd();
                user.set("password", BCrypt.hashpw(password, BCrypt.gensalt()));

                String nick = "";
                if(team.getStr("teamNameChinese") != null) nick +=  team.getStr("teamNameChinese") ;
                if(team.getStr("teamNameEnglish") != null)
                {
                    if(nick.equals("")) nick += team.getStr("teamNameEnglish");
                    else nick += " " + team.getStr("teamNameEnglish");
                }
                nick.replaceAll("null", "");
                user.set("nick", nick);
                String realName = team.getStr("name1");
                if(team.getStr("name2") != null) realName = realName + " " + team.getStr("name2");
                if(team.getStr("name3") != null) realName = realName + " " + team.getStr("name3");
                user.set("realName", realName);
                user.set("school", team.getStr("college1"));
                user.set("solved", 0);
                user.set("accepted", 0);
                user.set("submission", 0);
                user.set("ctime", (int)(System.currentTimeMillis() / 1000));
                user.set("mtime", (int)(System.currentTimeMillis() / 1000));
                user.set("loginTime", (int)(System.currentTimeMillis() / 1000));
                user.set("loginIP", "127.0.0.1");
                user.set("token", null);
                Db.save("user","uid", user);

                Integer uid  = user.getBigInteger("uid").intValue();

                Record user_ext = Db.findFirst("select * from user_ext where uid = ?", team.getInt("uid"));
                user_ext.set("uid", uid);
                Db.save("user_ext", user_ext);

                Record user_role = new Record();
                user_role.set("uid", uid).set("rid", 10).set("status",1);
                Db.save("user_role", user_role);

                Record contest_user = new Record();
                contest_user.set("uid", uid);
                contest_user.set("cid", cid);
                contest_user.set("special", team.getBoolean("isSpecialTeam"));
                contest_user.set("freshman", team.getBoolean("isRookieTeam"));
                contest_user.set("girls", team.getBoolean("isGirlTeam"));
                contest_user.set("nick", nick + "<br>" + realName);
                contest_user.set("ctime", (int)(System.currentTimeMillis() / 1000));
                contest_user.set("teamName", team.getStr("teamNameEnglish"));
                Db.save("contest_user", contest_user);

                writer.write(idx + "\t");
                writer.write(nick + "\t");
                writer.write(realName + "\t");
                writer.write(name + "\t");
                writer.write(password + "\r\n");
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }
}
