package com.storm.fliplayout.helper;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author 邵宝安
 *
 */
public class FastJsonHelper {

	/**
	 * 将json字符串转换成javaBean
	 * @param jsonString json字符串
	 * @param cls bean类
	 * @return
	 */
	public static <T> T getPerson2Bean(String jsonString, Class cls) {
		T t = null;
		try {
			t = (T) JSON.parseObject(jsonString, cls);
		} catch (Exception e) {
		}
		return t;
	}
	
	/**
	 * 将json数组转换成list
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getPersonArray2Bean(String jsonString, Class cls) {
		List<T> list = null;
		try {
			list = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
		}
		return list;
	}
}
