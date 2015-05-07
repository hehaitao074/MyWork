package com.storm.fliplayout.appliancation;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.baidu.frontia.FrontiaApplication;
import com.facebook.stetho.Stetho;

public class MyApplication extends FrontiaApplication {

	public static Context applicationContext;
	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();

		// 百度推送初始化
		// FrontiaApplication.initFrontiaApplication(getApplicationContext());

		// 全局异常捕捉
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		
		//facebook调试android工具类
		Stetho.initialize(
			    Stetho.newInitializerBuilder(this)
			        .enableDumpapp(
			                Stetho.defaultDumperPluginsProvider(this))
			        .enableWebKitInspector(
			                Stetho.defaultInspectorModulesProvider(this))
			        .build());

	}

	public static MyApplication getInstance() {
		return instance;
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

}
