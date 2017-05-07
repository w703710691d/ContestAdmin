package com.main;
import java.util.List;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class MainController extends Controller
{
	public void index() 
	{
		Integer currentPage = getParaToInt(0);
		if(currentPage == null) currentPage = 1;
		int totalPage = (MainService.GetContestNum() + 19) / 20;

		setAttr("currentPage", currentPage);
		setAttr("totalPage", totalPage);

		List<Record> list = MainService.GetMainList(currentPage);
		setAttr("NodeList", list);
		setAttr("username", getSessionAttr("username"));
		setAttr("msg", getSessionAttr("msg"));	
		setSessionAttr("lasturl", "/");
		setSessionAttr("msg", null);
		
		render("/view/index.html");
	}
}