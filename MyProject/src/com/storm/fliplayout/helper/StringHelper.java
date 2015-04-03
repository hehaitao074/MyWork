package com.storm.fliplayout.helper;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	private static final String PAYURlCHECK = "^(http|https):\\/\\/[a-z]{0,}(.g|g)oouu.com.cn";

	// 判断是否符合url规范
	private static final String ISURL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	/**
	 * 去除所有换行，空格
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 将多个换行符替换成一个
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceLineBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\n+");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 判断是否属于购买的url规范，并拼接字符串
	 * 
	 * @param url
	 * @param password
	 * @return
	 */
	public static String withUrlOrSplit(String url, String password) {
		Pattern pat = Pattern.compile(PAYURlCHECK, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(url);
		if (mat.find()) {
			StringBuffer sb = new StringBuffer();
			if (url.indexOf("?") > 0 && url.indexOf("?") != url.length()) {

				sb.append(url);
				sb.append("&");
				sb.append("status=");
				sb.append(password);
				return sb.toString();
			} else {
				sb.append(url);
				sb.append("?");
				sb.append("status=");
				sb.append(password);
				return sb.toString();
			}
		}
		return url;
	}

	/**
	 * 把中文转成Unicode码
	 * 
	 * @param str
	 * @return
	 */
	public static String chinaToUrlEncoder(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result += URLEncoder.encode(str.charAt(i) + "");
			} else {
				result += str.charAt(i);
			}
		}
		return result;
	}

	/**
	 * 判断是否符合url规范
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isURLSpecification(String url) {
		Pattern p = Pattern.compile(ISURL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 
	 * Title: spiltString
	 * 
	 * Description: 按一定字符切分字符串
	 * 
	 * @param str
	 * @param split
	 * @return
	 */
	public static List<String> spiltString(String str, String split) {
		String[] s = str.split(split);
		List<String> list = Arrays.asList(s);
		return list;
	}

}
