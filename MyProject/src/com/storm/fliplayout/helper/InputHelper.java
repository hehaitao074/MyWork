package com.storm.fliplayout.helper;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

/**
 * 
 * 
 * Title: InputHelper
 * 
 * Description: 输入过滤
 * 
 * Company: Goouu
 * 
 * @author hehaitao
 * 
 * @date 2015-1-10
 */
public class InputHelper {

	public static final String TAG = "InputHelper";
	public static final String MoneyRegEx = "^[0-9]+\\.[0-9]{3,}$";
	public static final String FIRSTISNUM = "^[0-9]+\\.?[0-9]*$";
	public static final String MoneyRegEx2 = "\\d{1,10}(\\.\\d{1,2})?$";

	private InputHelper() {
		// TODO Auto-generated constructor stub
	}

	public static InputFilter[] MoneyFilter(final Context context) {
		return new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				String data = dest.toString() + source;
				Pattern patm = Pattern.compile(MoneyRegEx);
				Pattern patn = Pattern.compile(FIRSTISNUM);
				Matcher matn = patn.matcher(data);
				Matcher matm = patm.matcher(data);

				if (!matn.find()) {
					return "";
				} else {
					if (matm.find()) {
						return "";
					}
				}

				if (data.length() > 10) {
					return "";
				} else {
					return source;
				}

			}
		} };
	}

	/**
	 * 限制只能购买500张
	 * 
	 * @param context
	 * @return
	 */
	public static InputFilter[] BuyNumFilter(final Context context , final int maxbuy) {
		return new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				String data = dest.toString() + source;
				int num = Integer.valueOf(data);
				if (num <= maxbuy) {
					return source;
				} else {
					ToastUtils.showShortToast(context, "您最多能购买" + maxbuy + "张");
				}
				return "";
			}
		} };
	}

	public static InputFilter[] Instantiation(final Context context,
			final int charlength) {
		return new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				byte[] cahr = null;
				byte[] sour = null;
				try {
					cahr = dest.toString().getBytes("GBK");
					sour = source.toString().getBytes("GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 如果当前字节数大于或者等于设置的字节长度
				if (cahr.length >= charlength) {
					ToastUtils.showShortToast(context, "您只能输入" + charlength / 2
							+ "个字");
					return "";
				} else {
					int diff = sour.length - (charlength - cahr.length);
					if (diff >= 0) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < diff; i++) {
							sb.append(sour[i]);
							return sb.toString();
						}
					}
				}
				return source;
				// Log.i(TAG,source+"--"+start +"--" + end);
				// Log.i(TAG,"字节个数"+ cahr.length + 1);
				// return source;
			}
		} };
	}

	// 字符个数
	public static InputFilter[] InstantiationZ(final Context context,
			final int charlength) {
		return new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				byte[] cahr = null;
				byte[] sour = null;
				try {
					cahr = dest.toString().getBytes("GBK");
					sour = source.toString().getBytes("GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 如果当前字节数大于或者等于设置的字节长度
				if (cahr.length >= charlength) {
					ToastUtils.showShortToast(context, "您只能输入" + charlength
							+ "个字");
					return "";
				} else {
					int diff = sour.length - (charlength - cahr.length);
					if (diff >= 0) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < diff; i++) {
							sb.append(sour[i]);
							return sb.toString();
						}
					}
				}
				return source;
				// Log.i(TAG,source+"--"+start +"--" + end);
				// Log.i(TAG,"字节个数"+ cahr.length + 1);
				// return source;
			}
		} };
	}

	/**
	 * 限制数量
	 * 
	 * @param context
	 * @return
	 */
	public static InputFilter[] CountFilter(final Context context,
			final int count) {
		return new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				String data = dest.toString() + source;
				int num = Integer.valueOf(data);
				if (num <= count) {
					return source;
				}
				return "";
			}
		} };
	}

}
