package com.rabbit.magazine;

import java.io.File;

import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.download.FileDownloader;

import android.app.Activity;
import android.os.Environment;

public class AppConfigUtil {
	
	private static final String RESOURCE = "resource";
	public static final String MAG_ID = "MAG_ID";
	private static final String CACHE = "cache";
	
	public static String MAGAZINE_ID=null;
	public static String MAGAZINE_TITLE=null;
	public static BookshelfActivity bookshelfActivity;
	public static FileDownloader curDownloader=null;
	public static boolean servicerunning=false;
	public static int WIDTH_ADJUST=1024;
	public static int HEIGHT_ADJUST=768;
	public static int WIDTHPIXELS;
	public static int HEIGHTPIXELS;
	
	/**
	 * 获取杂志封面
	 * @param magId
	 * @return
	 */
	public static String getCoverImgPath(String magId){
		String path = Environment.getExternalStorageDirectory() + File.separator + "magazine" + File.separator+"covers"+File.separator+magId+File.separator+"cover";
		return path;
	}
	
	/**
	 * 获取杂志预览图
	 * @param magId
	 * @return
	 */
	public static String getPreviewsPath(){
		String path = Environment.getExternalStorageDirectory() + File.separator + "magazine" + File.separator+"previews";
		return path;
	}
	
	public static String getPreviewImgPath(String magId,int index){
		String path = Environment.getExternalStorageDirectory() + File.separator + "magazine" + File.separator+"previews"+File.separator+magId+File.separator+"preview"+index;
		return path;
	}

	/**
	 * App获取杂志目录
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getAppExtDir(String fileName) {
		String path = Environment.getExternalStorageDirectory() + File.separator + "magazine" + File.separator
				+ fileName;
		return path;
	}

	public static String getAppExtDir() {
		String path = Environment.getExternalStorageDirectory() + File.separator + "magazine";
		return path;
	}

	/**
	 * 获取杂志Content.xml路径
	 * 
	 * @param app
	 * @return
	 */
	public static String getAppContent(String app) {
		String path = getAppResource(app) + File.separator + "content.xml";
		return path;
	}

	/**
	 * 获取杂志Content.xml路径
	 * 
	 * @param app
	 * @return
	 */
	public static String getAppResource(String app) {
		String path = getAppExtDir(app) + File.separator + RESOURCE;
		return path;
	}
	
	public static String getAppResourceImage(String app,String imgName) {
		String path = getAppResource(app) +imgName;
		return path;
	}

	public static String getAppCache(String app) {
		String path = getAppExtDir(app) + File.separator + CACHE;
		return path;
	}
}
