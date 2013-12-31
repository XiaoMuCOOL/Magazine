package com.rabbit.magazine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rabbit.magazine.AppConfigUtil;

import android.content.Context;
import android.util.DisplayMetrics;

public class FrameUtil {

	public static int[] frame2int(String frame) {
		int[] array = new int[4];
		if (frame != null) {
			String[] frames = frame.split(",");
			for (int i = 0; i < frames.length; i++) {
				array[i] = formatFrames(frames[i], i % 2 == 0 ? AppConfigUtil.WIDTH_ADJUST : AppConfigUtil.HEIGHT_ADJUST);
			}
		}
		return array;
	}
	
	public static int[] frame2int(String frame,int width,int height) {
		int[] array = new int[4];
		if (frame != null) {
			String[] frames = frame.split(",");
			for (int i = 0; i < frames.length; i++) {
				array[i] = formatFrames(frames[i], i % 2 == 0 ? width : height);
			}
		}
		return array;
	}

	// 将Frame转化成整数
	private static int formatFrames(String frame, int dimention) {
		int result = 0;
		frame = frame.replace(" ", "");
		if (frame.contains("+")) {
			String[] temps = frame.split("\\+");
			int[] tempArray = new int[temps.length];
			for (int i = 0; i < temps.length; i++) {
				if (temps[i].contains("%")) {
					tempArray[i] = new Float(Float.parseFloat(temps[i].replace("%", "")) * dimention / 100).intValue();
				}else{
					tempArray[i] =new Float(Float.parseFloat(temps[i])).intValue();
				}
				result += tempArray[i];
			}
		} else if (frame.contains("-")) {
			String[] temps = frame.split("\\-");
			int[] tempArray = new int[temps.length];
			for (int i = 0; i < temps.length; i++) {
				if (temps[i].contains("%")) {
					String replace = temps[i].replace("%", "");
					String value = compile(replace);
					tempArray[i] = new Float(Float.parseFloat(value) * dimention / 100).intValue();
					if (i == 0) {
						result = tempArray[i];
					}
				} else {
					String value = compile(temps[i]);
					value = value.equals("") ? "0" : value;
					result = result - Integer.parseInt(value);
				}
			}
		} else {
			String trim = frame.trim();
			String value = compile(trim);
			value = value.equals("") ? "0" : value;
			if (trim.contains("%")) {
				float f = Float.parseFloat(value.replace("%", ""));
				f = f * dimention / 100;
				result = new Float(f).intValue();
			} else {
				result = new Float(Float.parseFloat(value)).intValue();
			}
		}
		return result;
	}

	/**
	 * 支持数字、"%"、"+"、"-"、"."这几种符号
	 * 
	 * @param string
	 * @return
	 */
	private static String compile(String string) {
		Pattern p = Pattern.compile("[\\d%\\-\\+\\.]+");
		Matcher m = p.matcher(string);
		String value = "";
		while (m.find()) {
			value += m.group();
		}
		return value;
	}

	public static int[] content2Int(String contentSize) {
		String[] split = contentSize.split(",");
		int[] result = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = formatFrames(split[i], i % 2 == 0 ? AppConfigUtil.WIDTH_ADJUST : AppConfigUtil.HEIGHT_ADJUST);
		}
		return result;
	}

	public static int[] autoAdjust(int[] frames, Context context) {
		int[] result = new int[frames.length];
		for (int i = 0; i < frames.length; i++) {
			if (i == 0 || i == 2) {
				if(AppConfigUtil.WIDTH_ADJUST>AppConfigUtil.HEIGHT_ADJUST){
					float float1 = (frames[i]*100*AppConfigUtil.WIDTHPIXELS)/(AppConfigUtil.WIDTH_ADJUST*100);
					result[i] = new Float(float1).intValue();
				}else{
					float float1 = (frames[i]*100*AppConfigUtil.HEIGHTPIXELS)/(AppConfigUtil.WIDTH_ADJUST*100);
					result[i] = new Float(float1).intValue();
				}
			} else {
				if(AppConfigUtil.WIDTH_ADJUST>AppConfigUtil.HEIGHT_ADJUST){
					float float1 = (frames[i]*100*AppConfigUtil.HEIGHTPIXELS)/(AppConfigUtil.HEIGHT_ADJUST*100);
					result[i] = new Float(float1).intValue();
				}else{
					float float1 = (frames[i]*100*AppConfigUtil.WIDTHPIXELS)/(AppConfigUtil.HEIGHT_ADJUST*100);
					result[i] = new Float(float1).intValue();
				}
			}
		}
		return result;
	}
}
