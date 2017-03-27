package com.main;
import java.util.List;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class MainController extends Controller
{
	public void index() 
	{
		List<Record> list = MainService.GetMainList();
		setAttr("NodeList", list);
		setAttr("username", getSessionAttr("username"));
		setAttr("msg", getSessionAttr("msg"));	
		setSessionAttr("lasturl", "/");
		setSessionAttr("msg", null);
		
		render("/view/index.html");
	}
}