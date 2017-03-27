package com.main;
import java.util.List;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import api.ApiService;
import user.UserService;

public class MainController extends Controller
{
	public void index() 
	{
		List<Record> list = MainService.GetMainList();
		setAttr("NodeList", list);
		setAttr("username", UserService.GetUserName());
		setAttr("msg", ApiService.msg);
		ApiService.lastUrl = "/";
		ApiService.msg = null;
		render("/view/index.html");
	}
}