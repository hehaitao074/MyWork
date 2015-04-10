package com.storm.fliplayout.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * 显示吐司的类
 * @author U2
 *
 */
public class ToastUtils {

	private ToastUtils() {
	}

	/**
	 * 显示一个短吐司
	 * @param context
	 * @param msg
	 */
	public static void showShortToast(Context context,String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 显示一个长吐司
	 * @param context
	 * @param msg
	 */
	public static void showLongToast(Context context,String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 显示一个短吐司
	 * @param context
	 * @param msg
	 */
	public static void showShortToast(Context context,int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
