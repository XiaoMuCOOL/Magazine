package com.rabbit.magazine;

import java.io.File;

import android.os.Environment;

public class AppConfigUtil {
	
	private static final String RESOURCE = "resource";
	public static final String MAG_ID = "MAG_ID";
	private static final String CACHE = "cache";
	
	public static String MAGAZINE_ID=null;

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
