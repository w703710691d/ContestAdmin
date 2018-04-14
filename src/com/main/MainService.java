package com.main;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class MainService
{
	static Integer GetContestNum()
	{
		String s = Db.query("select count(*) from contest where type = 2").toString();
		s = s.substring(1,s.length() - 1);
		return Integer.parseInt(s);
	}
	static List<Record> GetMainList(int currentPage)
	{
		List<Record> list = Db.find("select "
				+ "cid, title, startTime, endTime "
				+ "from contest"
				+ " where type = 2 "
				+ "order by startTime DESC "
				+ "LIMIT ?, 20", (currentPage - 1)*20);
		return list;
	}
}
