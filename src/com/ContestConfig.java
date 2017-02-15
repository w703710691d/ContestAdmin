package com;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
public class ContestConfig extends JFinalConfig 
{
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}
	public void configRoute(Routes me) {
		me.add("/", show.class);
		me.add("/contest", contest.class);
		me.add("/admin", admin.class);
	}
	public void configPlugin(Plugins me)
	{
		loadPropertyFile("db_config.txt"); 
    	C3p0Plugin  cp  =  new  C3p0Plugin(getProperty("url"), getProperty("user"), getProperty("password"));
    	me.add(cp);
    	ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
    	me.add(arp);
	}
	public void configInterceptor(Interceptors me) {}
	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx_path"));
	}
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
	}
}