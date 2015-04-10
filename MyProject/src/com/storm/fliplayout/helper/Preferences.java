package com.storm.fliplayout.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class Preferences {

	public static final String USER_PREFERENCE = "Goouu_pref";

	public static int getUserPrefInt(Context context, String key,
			int defaultValue) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		return pref.getInt(key, defaultValue);
	}

	public static boolean getUserPrefBoolean(Context context, String key,
			boolean defaultValue) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		return pref.getBoolean(key, defaultValue);
	}

	public static String getUserPrefString(Context context, String key,
			String defaultValue) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		return pref.getString(key, defaultValue);
	}

	public static long getUserPrefLong(Context context, String key,
			long defaultValue) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		return pref.getLong(key, defaultValue);
	}

	public static void setUserPrefInt(Context context, String key, int value) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		Editor edit = pref.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public static void setUserPrefBoolean(Context context, String key,
			boolean value) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		Editor edit = pref.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static void setUserPrefString(Context context, String key,
			String value) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		Editor edit = pref.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static void setUserPrefLong(Context context, String key, long value) {
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		Editor edit = pref.edit();
		edit.putLong(key, value);
		edit.commit();
	}
	
	public static void removeKey(Context context,String key){
		SharedPreferences pref = Preferences.getSharedPreferences(context);
		Editor edit = pref.edit();
		edit.remove(key);
		edit.commit();
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? Context.MODE_MULTI_PROCESS : Context.MODE_PRIVATE);
		return pref;
	}
	
	
	

}
