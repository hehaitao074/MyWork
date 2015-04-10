package com.storm.fliplayout.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 缓存，键值对
 * 
 * @author wyz
 * 
 */
public class Cache {

	private static final String CACHE_KEY = "cache_key";

	private static final String CACHE_VALUE = "cache_value";

	private static final String CACHE_TABLE = "cache_table";

	private static final String CACHE_DB_NAME = "cache_db";

	private static final int CACHE_DB_VERSION = 2;

	private static final String CREATE_SQL = "CREATE TABLE cache_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT , cache_key TEXT , cache_value TEXT )";
	
	private static final String CREATE_INDEX = "CREATE INDEX IF NOT EXISTS index_cache_key on cache_table(cache_key);";

	private static Cache cache;

	private SQLiteDatabase mSqliteDb;

	private Cache(Context context) {
		mSqliteDb = new CacheDBHelper(context).getWritableDatabase();
	}

	public static Cache getInstance(Context context) {
		if (cache == null) {
			synchronized (Cache.class) {
				Cache dao = cache;
				if (dao == null) {
					synchronized (Cache.class) {
						cache = new Cache(context.getApplicationContext());
					}
				}
			}
		}
		return cache;
	}

	class CacheDBHelper extends SQLiteOpenHelper {

		public CacheDBHelper(Context context) {
			super(context, CACHE_DB_NAME, null, CACHE_DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_SQL);
			db.execSQL(CREATE_INDEX);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion <= 1){
				db.execSQL(CREATE_INDEX);
			}
		}

	}

	/**
	 * 保存int型数据
	 * 
	 * @param key
	 * @param value
	 */
	public void setPrefInt(String key, int value) {
		if (null == key)
			throw new IllegalArgumentException(
					"the argument key can not be null");
		if (keyExist(key))
			updateValue(key, String.valueOf(value));
		else
			insertValue(key, String.valueOf(value));
	}

	/**
	 * 保存boolean型数据
	 * 
	 * @param key
	 * @param value
	 */
	public void setPrefBoolean(String key, boolean value) {
		if (null == key)
			throw new IllegalArgumentException(
					"the argument key can not be null");
		if (keyExist(key))
			updateValue(key, value ? "1" : "0");
		else
			insertValue(key, value ? "1" : "0");
	}

	/**
	 * 保存long型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void setPrefLong(String key, long value) {
		if (null == key)
			throw new IllegalArgumentException(
					"the argument key can not be null");
		if (keyExist(key))
			updateValue(key, String.valueOf(value));
		else
			insertValue(key, String.valueOf(value));
	}

	/**
	 * 保存字符串型数据
	 * 
	 * @param key
	 * @param value
	 */
	public void setPrefString(String key, String value) {
		if (null == key)
			throw new IllegalArgumentException(
					"the argument key can not be null");
		if (keyExist(key))
			updateValue(key, String.valueOf(value));
		else
			insertValue(key, String.valueOf(value));
	}

	/**
	 * 获取int型数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getPrefInt(String key, int defaultValue) {
		Cursor cursor = mSqliteDb.rawQuery("SELECT cache_value FROM cache_table WHERE cache_key = ?",
				new String[] { key });
		if (cursor == null)
			return defaultValue;
		int count = defaultValue;
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			count = Integer.valueOf(cursor.getString(0));
		}
		cursor.close();
		return count;
	}

	/**
	 * 获取boolean型数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getPrefBoolean(String key, boolean defaultValue) {
		Cursor cursor = mSqliteDb.rawQuery("SELECT cache_value FROM cache_table WHERE cache_key = ?",
				new String[] { key });
		if (cursor == null)
			return defaultValue;
		boolean value = defaultValue;
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			String data = cursor.getString(0);
			if (data.equals("1"))
				value = true;
			else
				value = false;
		}
		cursor.close();
		return value;
	}

	/**
	 * 获取字符串型数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getPrefString(String key, String defaultValue) {
		Cursor cursor = mSqliteDb.rawQuery("SELECT cache_value FROM cache_table WHERE cache_key = ?",
				new String[] { key });
		if (cursor == null)
			return defaultValue;
		String value = defaultValue;
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			value = cursor.getString(0);
		}
		cursor.close();
		return value;
	}

	/**
	 * 获取long型的数据
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getPrefLong(String key, long defaultValue) {
		Cursor cursor = mSqliteDb.rawQuery("SELECT cache_value FROM cache_table WHERE cache_key = ?",
				new String[] { key });
		if (cursor == null)
			return defaultValue;
		long value = defaultValue;
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			value = Long.valueOf(cursor.getString(0));
		}
		cursor.close();
		return value;
	}

	/**
	 * 清除某一个值
	 * 
	 * @param key
	 */
	public void removeKey(String key) {
		mSqliteDb
				.delete(CACHE_TABLE, CACHE_KEY + " = ? ", new String[] { key });
	}

	/**
	 * 判断某一个键值对是否存在
	 * 
	 * @param key
	 *            键的名称
	 * @return
	 */
	private boolean keyExist(String key) {
		boolean exist = false;
		try {
			Cursor cursor = mSqliteDb.rawQuery("SELECT _id FROM cache_table WHERE cache_key = ?", new String[] { key });
			if (cursor != null && cursor.getCount() > 0) {
				exist = true;
			}
			cursor.close();
		} catch (Exception e) {
			exist = false;
		}
		return exist;
	}

	/**
	 * 插入一个值
	 * 
	 * @param key
	 *            键名称
	 * @param value
	 *            键的值
	 */
	private long insertValue(String key, String value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CACHE_KEY, key);
		contentValues.put(CACHE_VALUE, value);
		return mSqliteDb.insert(CACHE_TABLE, null, contentValues);
	}

	/**
	 * 更新一个值
	 * 
	 * @param key
	 *            键名称
	 * @param value
	 *            键的值
	 */
	private int updateValue(String key, String value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CACHE_KEY, key);
		contentValues.put(CACHE_VALUE, value);
		return mSqliteDb.update(CACHE_TABLE, contentValues, CACHE_KEY + " = ?",
				new String[] { key });
	}

}
