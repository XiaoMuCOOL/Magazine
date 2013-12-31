package com.rabbit.magazine.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FileService {
	private DBOpenHelper openHelper;
	private final static byte[] _writeLock = new byte[0];

	public FileService(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	
	public List<Map<String, String>> query(String account){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select save_path,fileid,downpath,fileid from filedownlog where account=?", new String[]{account});
		List<Map<String, String>> list=new ArrayList<Map<String, String>>();
		while(cursor.moveToNext()){
			Map<String, String> data = new HashMap<String, String>();
			int count=cursor.getColumnCount();
			for(int i=0;i<count;i++){
				data.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(data);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 获取每条线程已经下载的文件长度
	 * @param path
	 * @return
	 */
	public Map<Integer, Long> getData(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select threadid, downlength from filedownlog where downpath=?", new String[]{path});
		Map<Integer, Long> data = new HashMap<Integer, Long>();
		while(cursor.moveToNext()){
			data.put(cursor.getInt(0), cursor.getLong(1));
		}
		cursor.close();
		db.close();
		return data;
	}
	
	/**
	 * 保存每条线程已经下载的文件长度
	 * @param path
	 * @param map
	 */
	public void save(String path,  Map<Integer, Long> map){
		synchronized (_writeLock) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			db.beginTransaction();
			try{
				for(Map.Entry<Integer, Long> entry : map.entrySet()){
					db.execSQL("insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
							new Object[]{path, entry.getKey(), entry.getValue()});
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}finally{
				if(db!=null){
					db.close();
				}
			}
		}
	}
	
	/**
	 * 实时更新每条线程已经下载的文件长度
	 * @param path
	 * @param map
	 */
	public void update(String path, Map<Integer, Long> map){
		synchronized (_writeLock) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			try{
				db.beginTransaction();
				for(Map.Entry<Integer, Long> entry : map.entrySet()){
					db.execSQL("update filedownlog set downlength=? where downpath=? and threadid=?",new Object[]{entry.getValue(), path, entry.getKey()});
				}
				db.setTransactionSuccessful();
			}catch(Exception e){
				Log.e("FileService--update", e.getMessage());
			}finally{
				db.endTransaction();
				if(db!=null){
					db.close();
				}
			}
		}
	}
	
	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * @param path
	 */
	public void delete(String path){
		synchronized (_writeLock) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			db.execSQL("delete from filedownlog where downpath=?", new Object[]{path});
			db.close();
		}
	}
}
