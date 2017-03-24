package com.main;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class MainService
{
	static List<Record> GetMainList()
	{
		List<Record> list = Db.find("select "
				+ "cid, title, startTime, endTime "
				+ "from contest"
				+ " where type = 2 "
				+ "order by startTime DESC");
		return list;
	}
}
