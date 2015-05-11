package com.storm.fliplayout.helper;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.http.RequestParams;

public class HttpHelper {

	public static String clienttype = "2";
	private static String userName = null;
	public static final String PREF_USERNAME = "username";
	public static final String TAG = "NetworkUtil";
	public static final String CTWAP = "ctwap";
	public static final String CMWAP = "cmwap";
	public static final String WAP_3G = "3gwap";
	public static final String UNIWAP = "uniwap";
	public static final int TYPE_WAP = 5;// 电信wap 10.0.0.200
	public static final int TYPE_NET = 6;// 电信,移动,联通,wifi 等net网络
	public static final int TYPE_WIFI = 1;// wifi
	public static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	private HttpHelper() {
	}

	public static RequestParams getRequestParams(Context context) {

		RequestParams params = new RequestParams();
		return params;
	}

	/**
	 * 判断网络连接是否可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		context = context.getApplicationContext();
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connect == null) {
			return false;
		} else// get all network info
		{
			NetworkInfo[] info = connect.getAllNetworkInfo();
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

	public static int getNetworkType(Context mContext) {
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				// 注意一：
				// NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
				// 但是有些电信机器，仍可以正常联网，
				// 所以当成net网络处理依然尝试连接网络。
				// （然后在socket中捕捉异常，进行二次判断与用户提示）。
				Log.i(TAG, "=====================>无网络");
				return TYPE_NET;
			} else {
				int netType = mobNetInfoActivity.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					return TYPE_WIFI;
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {
					// 注意二：
					// 判断是否电信wap:
					// 不要通过getExtraInfo获取接入点名称来判断类型，
					// 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
					// 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
					// 所以可以通过这个进行判断！
					final Cursor cursor = mContext.getContentResolver().query(
							PREFERRED_APN_URI, null, null, null, null);
					if (cursor != null) {
						cursor.moveToFirst();
						final String user = cursor.getString(cursor
								.getColumnIndex("user"));
						if (!TextUtils.isEmpty(user)) {
							Log.i("",
									"=====================>代理："
											+ cursor.getString(cursor
													.getColumnIndex("proxy")));
							if (user.startsWith(CTWAP)) {
								Log.i("", "=====================>电信wap网络");
								return TYPE_WAP;
							}
						}
						cursor.close();
					}
					// 注意三：
					// 判断是移动联通wap:
					// 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
					// 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
					// 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
					// 所以采用getExtraInfo获取接入点名字进行判断

					String netMode = mobNetInfoActivity.getExtraInfo();
					Log.i("", "netMode ================== " + netMode);
					if (netMode != null) {
						// 通过apn名称判断是否是联通和移动wap
						netMode = netMode.toLowerCase();
						if (netMode.equals(CMWAP) || netMode.equals(WAP_3G)
								|| netMode.equals(UNIWAP)) {
							Log.i("", "=====================>移动联通wap网络");
							return TYPE_WAP;
						}

					}

				}
			}
		} catch (Exception ex) {
			return TYPE_NET;
		}

		return TYPE_NET;

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

	
}
