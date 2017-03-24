package com.core;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import api.ApiService;

public class GlobalActionInterceptor implements Interceptor {

	public void intercept(Invocation inv)
	{
		if(ApiService.lastUrl == null) 
			ApiService.lastUrl = "/";
		inv.invoke();
	}

}
