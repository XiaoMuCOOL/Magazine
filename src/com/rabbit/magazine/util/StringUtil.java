package com.rabbit.magazine.util;

public class StringUtil {
	
	public static boolean isNull(String obj){
		if(obj==null||obj.equals("")){
			return true;
		}
		return false;
	}
	public static boolean isNotNull(String obj){
		if(obj!=null&&!obj.equals("")){
			return true;
		}
		return false;
	}

}
