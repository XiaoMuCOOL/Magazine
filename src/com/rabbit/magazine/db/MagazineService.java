package com.rabbit.magazine.db;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbit.magazine.FavoriteInfo;
import com.rabbit.magazine.GerenInfo;
import com.rabbit.magazine.Magazineinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MagazineService {

	private DBOpenHelper openHelper;
	
	public MagazineService(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	
	public List<FavoriteInfo> queryFavorite(String magId,int curindex,String orientation){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		List<FavoriteInfo> list=new ArrayList<FavoriteInfo>();
		Cursor cursor=db.query("favoriteinfo", new String[]{"id"}, "magid=? and curindex=? and orientation=?", new String[]{magId,String.valueOf(curindex),orientation}, null, null, null);
		while(cursor.moveToNext()){
			FavoriteInfo info=new FavoriteInfo();
			list.add(info);
		}
		if(db!=null&&db.isOpen()){
			db.close();
		}
		if(cursor!=null&&!cursor.isClosed()){
			cursor.close();
		}
		return list;
	}
	
	public void saveFavorite(FavoriteInfo info){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("magid", info.getMagId());
		values.put("curindex", info.getIndex());
		values.put("pagesize", info.getPageSize());
		values.put("imgpath", info.getImgPath());
		values.put("title", info.getTitle());
		values.put("orientation", info.getOrientation());
		db.beginTransaction();
		db.insert("favoriteinfo", null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	public void deleteFavorite(int id){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		db.delete("favoriteinfo", "id=?", new String[]{String.valueOf(id)});
		db.setTransactionSuccessful();
		db.endTransaction();
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	public void deleteAll(int id){
//		SQLiteDatabase db = openHelper.getWritableDatabase();
//		db.beginTransaction();
//		db.delete("maginfo", "id=?", new String[]{String.valueOf(id)});
//		db.setTransactionSuccessful();
//		db.endTransaction();
//		if(db!=null&&db.isOpen()){
//			db.close();
//		}
	}
	
	public List<FavoriteInfo> getAllFavorites(){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		List<FavoriteInfo> list=new ArrayList<FavoriteInfo>();
		Cursor cursor=db.query("favoriteinfo", new String[]{"id","magid","curindex","pagesize","imgpath","title","orientation"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			FavoriteInfo info=new FavoriteInfo();
			info.setId(cursor.getInt(0));
			info.setMagId(cursor.getString(1));
			info.setIndex(cursor.getInt(2));
			info.setPageSize(cursor.getInt(3));
			info.setImgPath(cursor.getString(4));
			info.setTitle(cursor.getString(5));
			info.setOrientation(cursor.getString(6));
			list.add(info);
		}
		if(db!=null&&db.isOpen()){
			db.close();
		}
		if(cursor!=null&&!cursor.isClosed()){
			cursor.close();
		}
		return list;
	} 
	
	public void saveGeren(GerenInfo info){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues values=new ContentValues();
		values.put("magid", info.getMagId());
		values.put("cover", info.getCover());
		values.put("title", info.getTitle());
		values.put("price", info.getPrice());
		db.insert("gereninfo", null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	public void deleteGeren(int id){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		db.delete("gereninfo", "id=?", new String[]{String.valueOf(id)});
		db.setTransactionSuccessful();
		db.endTransaction();
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	public List<GerenInfo> getAllGerens(){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		List<GerenInfo> list=new ArrayList<GerenInfo>();
		Cursor cursor=db.query("gereninfo", new String[]{"id","magid","cover","title","price"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			GerenInfo info=new GerenInfo();
			info.setId(cursor.getInt(0));
			info.setMagId(cursor.getString(1));
			info.setCover(cursor.getString(2));
			info.setTitle(cursor.getString(3));
			info.setPrice(cursor.getString(4));
			list.add(info);
		}
		if(db!=null&&db.isOpen()){
			db.close();
		}
		if(cursor!=null&&!cursor.isClosed()){
			cursor.close();
		}
		return list;
	}
	
	public void saveMagazine(Magazineinfo info){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from maginfo where id=?",  new String[]{info.getId()});
		if(cursor.getCount()==0){
			db.beginTransaction();
			try{
				Gson gson=new Gson();
				db.execSQL("insert into maginfo(id, coverimage, zipurl,title,iosprice,previewimage,status,description) values(?,?,?,?,?,?,?,?)",
						new Object[]{info.getId(), info.getCover_image(), info.getZip_url(),info.getTitle(),info.getIosprice(),gson.toJson(info.getPreview_image()),info.getStatus(),info.getDescription()});
				db.setTransactionSuccessful();
				db.endTransaction();
			}finally{
				if(db!=null&&db.isOpen()){
					db.close();
				}
			}
		}
		if(db!=null&&db.isOpen()){
			db.close();
		}
		if(cursor!=null&&!cursor.isClosed()){
			cursor.close();
		}
	}
	
	public List<Magazineinfo> getAllMagazines(){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		List<Magazineinfo> list=new ArrayList<Magazineinfo>();
		Cursor cursor =db.query("maginfo", new String[]{"id","zipurl","iosprice","title","description","previewimage","dbid","status","coverimage"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			Magazineinfo info=new Magazineinfo();
			info.setId(cursor.getString(0));
			info.setZip_url(cursor.getString(1));
			info.setIosprice(cursor.getString(2));
			info.setTitle(cursor.getString(3));
			info.setDescription(cursor.getString(4));
			Gson gson=new Gson();
			List<String> preimgs=gson.fromJson(cursor.getString(5), new TypeToken<List<String>>(){}.getType());
			info.setPreview_image(preimgs);
			info.setDbid(cursor.getInt(6));
			info.setStatus(cursor.getInt(7));
			info.setCover_image(cursor.getString(8));
			list.add(info);
		}
		if(db!=null&&db.isOpen()){
			db.close();
		}
		if(cursor!=null&&!cursor.isClosed()){
			cursor.close();
		}
		return list;
	}

	public void updateMagazineStatus(String magId,int status){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("status", status);
		db.beginTransaction();
		db.update("maginfo", values, "id=?", new String[]{magId});
		db.setTransactionSuccessful();
		db.endTransaction();
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
}
