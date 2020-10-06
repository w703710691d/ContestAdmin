package com.core;


import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.main.MainController;

import api.ApiController;
import contest.ContestController;
import com.main.admin.AdminController;

public class CoreConfig extends JFinalConfig {
    public void configConstant(Constants me) {
        me.setDevMode(true);
    }

    public void configRoute(Routes me) {
        me.add("/", MainController.class);
        me.add("/contest", ContestController.class);
        me.add("/admin", AdminController.class);
        me.add("/api", ApiController.class);
    }

    public void configPlugin(Plugins me) {
        loadPropertyFile("db_config.txt");
        DruidPlugin druidPlugin = new DruidPlugin(getProperty("url"), getProperty("user"), getProperty("password"));
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        druidPlugin.addFilter(wall);
        me.add(druidPlugin);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        me.add(arp);
    }

    public void configInterceptor(Interceptors me) {
        me.addGlobalActionInterceptor(new GlobalActionInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new ContextPathHandler("ctx_path"));
    }

    public static void main(String[] args) {
        JFinal.start("WebRoot", 80, "/", 5);
    }
}