package com.storm.fliplayout.helper;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lidroid.xutils.http.RequestParams;

public class HttpHelper {

	private static String PHONEID = "phoneid";
	private static String PASSWORD = "password";
	private static String CLIENTTYPE = "clienttype";
	private static String VERSION = "version";
	private static String CELLID = "cellid";
	private static String CHANNELID = "channelid";
	private static String PUSHUSERID = "pushuserid";
	public static String clienttype = "2";
	private static String userName = null;
	public static final String PREF_USERNAME = "username";

	private HttpHelper() {
	}

	public static RequestParams getRequestParams(Context context) {

		RequestParams params = new RequestParams();	
		return params;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 获取设备唯一标示
	public static String getDeviceId(Context context) {

		StringBuilder deviceId = new StringBuilder();
		// 渠道标志
		deviceId.append("");

		try {

			// IMEI（imei）
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (!TextUtils.isEmpty(imei)) {
				// deviceId.append("imei");
				deviceId.append(imei);
				return deviceId.toString();
			}

			// wifi mac地址
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if (!TextUtils.isEmpty(wifiMac)) {
				// deviceId.append("wifi");
				deviceId.append(wifiMac);
				return deviceId.toString();
			}

			// 序列号（sn）
			String sn = tm.getSimSerialNumber();
			if (!TextUtils.isEmpty(sn)) {
				// deviceId.append("sn");
				deviceId.append(sn);
				return deviceId.toString();
			}

			// 如果上面都没有， 则生成一个id：随机码
			String uuid = getUUID(context);
			if (!TextUtils.isEmpty(uuid)) {
				// deviceId.append("id");
				deviceId.append(uuid);
				return deviceId.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceId.append("id").append(getUUID(context));
		}

		return deviceId.toString();
	}

	/**
	 * 得到全局唯一UUID
	 */
	public static String getUUID(Context context) {
		String uuid;

		uuid = UUID.randomUUID().toString();
		return uuid;
	}

	/**
	 * 2 * 获取版本号 3 * @return 当前应用的版本号 4
	 */
	public static String getVersion(Context context) {

		return "版本号" + getVer(context);

	}

	/**
	 * 获取应用名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getApplicationName(Context context) {

		return getAppName(context) + "客户端Android版本";

	}

	public static String getVer(Context context) {
		String version = null;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			version = info.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return version;
		}

		return version;
	}

	public static String getVersionCode(Context context)// 获取版本号(内部识别号)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode + "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}

	public static String getAppName(Context context) {
		String name = null;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			name = info.applicationInfo.loadLabel(context.getPackageManager())
					.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return name;
		}
		return name;
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public static String getEauserName(Context applicationContext) {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			userName = preferences.getString(PREF_USERNAME, null);
		}
		return userName;
	}
}
