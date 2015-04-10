package com.storm.fliplayout.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	public static final int DAY_SECONDE = 24 * 3600;

	public static final long DAY_MILL_SECONDE = 1000L * 24 * 3600;

	/**
	 * 判断time2和time1是否是同一天
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean judgeOneday(long time1, long time2) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		long timeOffset = c.getTimeInMillis();
		if (time2 >= timeOffset && time2 < timeOffset + DAY_MILL_SECONDE) {
			return true;
		}
		return false;
	}

	/**
	 * 判断d1是否比d2大
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean DateCompare(Date d1 , Date d2) {
		if(d1.getTime() >= d2.getTime()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断d1与d2相差的天数是否大于Interval
	 * @param d1
	 * @param d2
	 * @param Interval
	 * @return
	 */
	public static boolean DateCompare(Date d1 , Date d2,int Interval) {
		if(((d1.getTime() - d2.getTime())/1000/60) >= Interval) {
			return true;
		}
		return false;
	}
	/**
	 * 获取今天开始时间戳
	 * 
	 * @return
	 */
	public static long getTodayTimeStart() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 格式化时间为 xx小时xx分钟
	 * 
	 * @param time
	 *            ，单位，秒
	 * @return
	 */
	public static String formatTime(int time) {
		int minutes = time / 60;
		int hour = minutes / 60;
		minutes %= 60;
		return hour + "小时" + minutes + "分钟";
	}

	public static String formatStudyTime(int time) {
		int minutes = time / 60;
		int hour = minutes / 60;
		minutes %= 60;
		return hour + "时" + minutes + "分";
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 *            单位，秒
	 * @return
	 */
	public static String format(String format, int time) {
		int minutes = time / 60;
		int hour = minutes / 60;
		minutes %= 60;
		return String.format(format, hour, minutes);
	}

	public static String formatTimeZero(int time) {
		int minutes = time / 60;
		int hour = minutes / 60;
		minutes %= 60;
		if (hour > 0) {
			if (minutes > 0) {
				return hour + "小时" + minutes + "分钟";
			} else {
				return hour + "小时";
			}
		} else {
			return minutes + "分钟";
		}
	}

	public static String formatStudy(int time) {
		int minutes = time / 60;
		int hour = minutes / 60;
		int second = time % 60;
		minutes %= 60;
		if (hour > 0) {
			if (minutes > 0) {
				return hour + "小时" + minutes + "分钟";
			} else {
				return hour + "小时" + second + "秒";
			}
		} else {
			if (minutes > 0) {
				return minutes + "分钟" + second + "秒";
			} else {
				return second + "秒";
			}
		}
	}

	public static String formatTimeSince(int time) {
		int now = (int) (System.currentTimeMillis() / 1000);
		int offset = now - time;
		if (offset < 60) {
			return "刚刚";
		} else if (offset < 3600) {
			int minute = offset / 60;
			return minute + "分钟前";
		} else if (offset < 3600 * 24) {
			int hour = offset / 3600;
			return hour + "小时前";
		} else {
			int days = offset / (3600 * 24);
			if (days > 10) {
				return CommonUtil.formatTime("MM-dd",
						Long.valueOf(time) * 1000L);
			} else {
				return days + "天前";
			}
		}
	}

	/**
	 * 
	 * 方法名称：isSameDate(String date1,String date2)
	 * 功能描述：判断date1和date2是否在同一周
	 * 输入参数：date1,date2
	 * 返 回 值：false 或 true
	 * 其它说明：主要用到Calendar类中的一些方法
	 */
	public static boolean isSameDate(String date1, String date2) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(date1);
			d2 = format.parse(date2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		// 例子:cal1是"2005-1-1"，cal2是"2004-12-25"
		// java对"2004-12-25"处理成第52周
		// "2004-12-26"它处理成了第1周，和"2005-1-1"相同了
		// 大家可以查一下自己的日历
		// 处理的比较好
		// 说明:java的一月用"0"标识，那么12月用"11"
		else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		// 例子:cal1是"2004-12-31"，cal2是"2005-1-1"
		else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;

		}
		return false;
	}

}
