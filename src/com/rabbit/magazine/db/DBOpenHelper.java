package com.rabbit.magazine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作类
 * @author Administrator
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "magazine.db";
	private static final int VERSION = 1;
	
	/**
	 * 构造器
	 * @param context
	 */
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (" +
				"id integer primary key autoincrement, " +
				"downpath varchar(100), " +
				"threadid INTEGER, " +
				"downlength BIGINT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS maginfo (" +
				"dbid integer primary key autoincrement, " +
				"id varchar(10), " +
				"coverimage varchar(255), " +
				"zipurl varchar(255), " +
				"title varchar(255), " +
				"iosprice varchar(255), " +
				"previewimage text, " +
				"status integer, " +
				"description text, " +
				"updatetick varchar(255))");
				//"description text)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS gereninfo (" +
				"id integer primary key autoincrement, " +
				"magid varchar(10), " +
				"cover varchar(255), " +
				"title varchar(255), " +
				"price text)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS favoriteinfo (" +
				"id integer primary key autoincrement, " +
				"magid varchar(10), " +
				"curindex integer, " +
				"pagesize integer, " +
				"imgpath varchar(255), " +
				"orientation varchar(255), " +
				"title varchar(255))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS filedownlog");
		db.execSQL("DROP TABLE IF EXISTS maginfo");
		onCreate(db);
	}
}