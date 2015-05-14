package com.storm.fliplayout.appliancation;

import android.content.Context;

import com.baidu.frontia.FrontiaApplication;

public class MyApplication extends FrontiaApplication {

	public static Context applicationContext;
	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();

	

		// 全局异常捕捉
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		
		//facebook调试android工具类
//		Stetho.initialize(
//			    Stetho.newInitializerBuilder(this)
//			        .enableDumpapp(
//			                Stetho.defaultDumperPluginsProvider(this))
//			        .enableWebKitInspector(
//			                Stetho.defaultInspectorModulesProvider(this))
//			        .build());

	}

	public static MyApplication getInstance() {
		return instance;
	}

	
}
