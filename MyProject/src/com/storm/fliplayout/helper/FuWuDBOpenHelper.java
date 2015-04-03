package com.storm.fliplayout.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FuWuDBOpenHelper extends SQLiteOpenHelper {

	public static final String FUWUNAME = "fuwulocal";
	// 数据库版本
	private static final int VERSION = 1;
	// 新建一个表
	String sql = "create table if not exists "+FUWUNAME+""
			+ "(_id,ServicName,ServiceImg,ServiceLink,TargetTypeId,TargetTypeDisplay,TargetId,ServiceTypeId,ServiceTypeDisplay)";

	public FuWuDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public FuWuDBOpenHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public FuWuDBOpenHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}