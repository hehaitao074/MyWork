package com.storm.fliplayout.communal;

import java.io.File;

import android.os.Environment;

public class Constant {

	// 是否在调试模式
	public static final boolean DEBUG = true;	
	// 图片存储路径
	public static final String SAVED_IMAGE_DIR_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "goouu"
			+ File.separator + "temp";
	// 数据库存储路径
	public static final String SAVED_DB_DIR_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "goouu"
			+ File.separator + "goouu_db";
	// 数据库名称
	public static final String DB_NAME = "Goouu.db";
	public static final String DB_NAME_NOTIFI = "Goouu_notifi.db";
	// 数据库版本
	public static final int DB_VERSION = 1;
	public static final String DB_TABLE_NOTIFI = "notification_info";

	
}
