package com.storm.fliplayout.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

/**
 * 
 * 
 * Title: CommonUtil
 * 
 * Description: 常用工具类
 * 
 * Company: Goouu
 * 
 * @author hehaitao
 * 
 * @date 2014-8-19
 */
public class CommonUtil {

	public static final String DESPASSWORD = "xuduoduo";

	public static final String[] WEEKDAYS = { "周日", "周一", "周二", "周三", "周四",
			"周五", "周六" };

	/**
	 * 获取通知栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 计算两经纬度之间距离，
	 * 
	 * @param start
	 * @param end
	 * @return 米
	 */
	public static double getDistance(LatLng start, LatLng end) {
		double lat1 = (Math.PI / 180) * start.latitude;
		double lat2 = (Math.PI / 180) * end.latitude;

		double lon1 = (Math.PI / 180) * start.longitude;
		double lon2 = (Math.PI / 180) * end.longitude;

		// 地球半径
		double R = 6371;

		// 两点间距离 km，如果想要米的话，结果*1000就可以了
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon2 - lon1))
				* R;

		return d * 1000;
	}

	/**
	 * 获取适配后的宽度
	 * 
	 * @param context
	 * @param width
	 * @return
	 */
	public static int getAdaptWidth(Context context, int width) {
		return (int) (getScreenWidth(context) / 480.0f * width);
	}

	/**
	 * 获取适配后的高度
	 * 
	 * @param context
	 * @param height
	 * @return
	 */
	public static int getAdaptHeight(Context context, int height) {
		return (int) (getScreenHeight(context) / 800.0f * height);
	}

	/**
	 * 屏幕的宽度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕dp宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidthDP(Context context) {
		return CommonUtil.px2dip(context, getScreenWidth(context));
	}

	/**
	 * 屏幕的高度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取屏幕dp高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeightDp(Context context) {
		return CommonUtil.px2dip(context, getScreenHeight(context));
	}

	// 获取屏幕密度
	public static float getDensity(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		// int width = metric.widthPixels; // 屏幕宽度（像素）
		// int height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		// int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		return density;
	}

	// 获取屏幕信息,并拼接字符串
	public static String getScreenInfo(Activity context) {
		int width = getScreenWidth(context);
		int height = getScreenHeight(context);

		return "?width=" + (int) Math.ceil(width / 1.5) + "&height="
				+ (int) Math.ceil(height / 4.5) + "&quality=100&mode=crop";
	}

	public static String getScreenInf(String img, int width, int height) {
		StringBuffer sb = new StringBuffer();
		sb.append(img);
		if (width >= 0 || height >= 0) {
			sb.append("?");
		}
		if (width >= 0) {
			sb.append("width=" + width + "&");
		}
		if (height >= 0) {
			sb.append("height=" + height + "&");
		}

		if (width >= 0 || height >= 0) {
			sb.append("quality=100&mode=crop");
		}
		return sb.toString();
	}

	// 获取img标签正则
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	// 获取src路径的正则
	private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

	public static final String MOBILE = "^[1][3,4,5,8][0-9]{9}$";
	public static final String MOBILE2 = "(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,8][0-9]{9})";// 电话号码的验证（手机号和座机号）

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile(MOBILE); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 电话号码验证（包括手机号和座机号）
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isTL(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile(MOBILE2);
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/***
	 * 获取ImageUrl地址
	 * 
	 * @param HTML
	 * @return
	 */
	private static List<String> getImageUrl(String HTML) {
		Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
		List<String> listImgUrl = new ArrayList<String>();
		while (matcher.find()) {
			listImgUrl.add(matcher.group());
		}
		return listImgUrl;
	}

	/***
	 * 获取ImageSrc地址
	 * 
	 * @param listImageUrl
	 * @return
	 */
	private static List<String> getImageSrc(List<String> listImageUrl) {
		List<String> listImgSrc = new ArrayList<String>();
		for (String image : listImageUrl) {
			Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
			while (matcher.find()) {
				listImgSrc.add(matcher.group().substring(0,
						matcher.group().length() - 1));
			}
		}
		return listImgSrc;
	}

	/**
	 * 得到网页中图片的地址
	 */
	public static String replaceImgSrc(String htmlStr, String replaceStr) {
		String result = "";
		List<String> list = getImageUrl(htmlStr);
		List<String> listimgsrc = getImageSrc(list);
		for (int i = 0; i < listimgsrc.size(); i++) {
			htmlStr = htmlStr.replaceAll(listimgsrc.get(i), listimgsrc.get(i)
					+ replaceStr);
		}

		// Log.i("CommonUtil___", list.toString());
		// Log.i("CommonUtil***", listimgsrc.toString());
		// Log.i("CommonUtil###", htmlStr);
		return htmlStr;
		// String patternStr = "<img.*src=[^>]*?>";
		// // String patternStr =
		// "^.*<img\\s*.*\\s*src=\\\"(.*?)\\\"\\s*.*>.*$";
		// // // 图片链接地址
		// Pattern pattern = Pattern.compile(patternStr,
		// Pattern.CASE_INSENSITIVE);
		// Matcher matcher = pattern.matcher(htmlStr);
		// List<String> list = new ArrayList<String>();
		// while (matcher.find()) {
		// list.add(matcher.group());
		// }
		//
		// Log.i("CommonUtil", list.toString());
		//
		// if (matcher.matches()) {
		// result = htmlStr.replaceAll(matcher.group(1),
		// (matcher.group(1) + replaceStr));
		// System.out.println(" result == " + result);
		// } else {
		// result = htmlStr;
		// }
		// return result;
	}

	/**
	 * 获取版本名
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			return -1;
		}
	}

	/**
	 * 获取md5加密的值
	 * 
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
		return md5(string, null);
	}

	/**
	 * 获取md5加密后的字符串
	 * 
	 * @param string
	 * @param method
	 *            加密方法:md5或是sha-1等
	 * @return
	 */
	public static String md5(String string, String method) {
		if (string == null || string.trim().length() < 1) {
			return null;
		}
		String m = "md5";
		if (!TextUtils.isEmpty(method)) {
			m = method;
		}
		try {
			byte[] source = string.getBytes("UTF-8");
			MessageDigest md5 = MessageDigest.getInstance(m);
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void showToast(Context context, int resId) {
		showToast(context, context.getString(resId));
	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 启动一个activity
	 * 
	 * @param activity
	 * @param intent
	 * @param enterAnim
	 * @param exitAnim
	 */
	public static void goToActivity(Activity activity, Intent intent,
			int enterAnim, int exitAnim) {
		activity.startActivity(intent);
		activity.overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * 结束一个activity
	 * 
	 * @param activity
	 * @param enterAnim
	 * @param exitAnim
	 */
	public static void finishActivity(Activity activity, int enterAnim,
			int exitAnim) {
		activity.finish();
		activity.overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * dip转化为px
	 * 
	 * @param context
	 * @param dipValue
	 *            要转化的dip
	 * @return int 单位px
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转化为dip
	 * 
	 * @param context
	 * @param pxValue
	 *            要转化的px
	 * @return int 单位dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * 
	 * Title: getDensity
	 * 
	 * Description: 获取手机屏幕密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getDensity(Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return scale;
	}

	/**
	 * 将输入流度成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String changeIsToStr(InputStream is) {
		StringBuilder builder = new StringBuilder();
		try {
			String str;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"));
			while ((str = reader.readLine()) != null) {
				builder.append(str);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * 将Bundle中的数据对编码成http-query的形式的字符串
	 * 
	 * @param parameters
	 * @return
	 */
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (key == null)
				continue;
			String value = parameters.getString(key);
			if (value == null)
				continue;
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			try {
				sb.append(key + "="
						+ URLEncoder.encode(parameters.getString(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 将http-query的形式的字符串解码存入Bundle中
	 * 
	 * @param s
	 * @return
	 */
	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length == 2)
					try {
						params.putString(URLDecoder.decode(v[0], "UTF-8"),
								URLDecoder.decode(v[1], "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
			}
		}
		return params;
	}

	/**
	 * 获取当前时间(13位)
	 * 
	 * @return long
	 */
	public static long getNowTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 格式化时间
	 * 
	 * @param format
	 *            yyyy-MM-dd HH:mm
	 * @param time
	 * @return
	 */
	public static String formatTime(String format, long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(time));
	}

	/**
	 * 获取星期
	 * 
	 * @param time
	 * @return
	 */
	public static String getWeekdayString(long time) {
		return WEEKDAYS[getWeekdayIndex(time)];
	}

	/**
	 * 获取星期
	 * 
	 * @param timeOffset
	 * @return
	 */
	public static int getWeekdayIndex(long timeOffset) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeOffset);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return w;
	}

	/**
	 * 获取捕获的异常的字符串
	 * 
	 * @param Exception
	 *            e
	 * @return
	 */
	public static String getErrorString(Exception e) {
		StringBuilder builder = new StringBuilder();
		StackTraceElement[] elements = e.getStackTrace();
		for (StackTraceElement item : elements) {
			builder.append(item.toString()).append("\n");
		}
		return builder.toString();
	}

	/**
	 * 过滤SQL特殊字符防止注入
	 * 
	 * @param sql
	 * @return
	 */
	public static String transactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	/***
	 * 去掉字符串前后的空间，中间的空格保留
	 * 
	 * @param str
	 * @return
	 */
	public static String trimInnerSpaceStr(String str) {
		if(str!=null){
			str = str.trim();
			while (str.startsWith(" ")) {
				str = str.substring(1, str.length()).trim();
			}
			while (str.endsWith(" ")) {
				str = str.substring(0, str.length() - 1).trim();
			}
		}else{
			str = "";
		}
	

		return str;
	}

	/**
	 * DES加密
	 * 
	 * @param datasource
	 * @param password
	 * @return
	 */
	public static byte[] desCrypto(byte[] datasource) {
		String password = DESPASSWORD;
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DES解密
	 * 
	 * @param src
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src) throws Exception {
		String password = DESPASSWORD;
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

}
