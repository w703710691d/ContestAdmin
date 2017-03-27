package com.core;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class GlobalActionInterceptor implements Interceptor {

	public void intercept(Invocation inv)
	{
		if(inv.getController().getSessionAttr("lasturl") == null)
			inv.getController().setSessionAttr("lasturl", "/");
		if(inv.getController().getSessionAttr("uid") == null)
			inv.getController().setSessionAttr("uid", 0);
		if(inv.getController().getSessionAttr("admin") == null)
			inv.getController().setSessionAttr("admin", false);
		inv.invoke();
	}

}
