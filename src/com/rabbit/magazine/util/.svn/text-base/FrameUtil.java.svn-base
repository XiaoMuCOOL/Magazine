package com.rabbit.magazine.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class FrameUtil {

	static final int UNIT = 768;

	public static Float[] getFrames(String frame) {
		String[] split = frame.split(",");
		Float[] result = new Float[split.length];
		for (int i = 0; i < split.length; i++) {
			String value = split[i];
			if (value.endsWith("%")) {
				value = value.substring(0, value.length() - 1);
				double percent = Float.parseFloat(value) * (0.01);
				value = String.valueOf(percent * UNIT);
			}
			result[i] = Float.parseFloat(value);
		}
		return result;
	}

	public static Integer[] getIntFrames(String frame) {
		String[] split = frame.split(",");
		Integer[] result = new Integer[split.length];
		for (int i = 0; i < split.length; i++) {
			String value = split[i].trim();

			if (value.endsWith("%")) {
				value = value.substring(0, value.length() - 1);
				double percent = Float.parseFloat(value) * (0.01);
				double d = percent * UNIT;
				value = String.valueOf(d);
				int indexOf = value.indexOf(".");
				value = value.substring(0, indexOf);
			}
			result[i] = Integer.parseInt(value);
		}
		return result;
	}

	public static int[] frame2int(String frame) {
		int[] array = new int[4];
		if (frame != null) {
			String[] frames = frame.split(",");
			for (int i = 0; i < frames.length; i++) {
				array[i] = formatFrames(frames[i], i % 2 == 0 ? 1024 : 768);
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

	public static Integer[] getContentSize(String contentSize) {
		String[] split = contentSize.split(",");
		Integer[] result = new Integer[split.length];
		for (int i = 0; i < split.length; i++) {
			String value = split[i].trim();
			if (value.endsWith("%")) {
				value = value.substring(0, value.length() - 1);
				double percent = Float.parseFloat(value) * (0.01);
				double d = percent * UNIT;
				value = String.valueOf(d);
				int indexOf = value.indexOf(".");
				value = value.substring(0, indexOf);
			}
			result[i] = Integer.parseInt(value);
		}
		return result;
	}
	
	public static int[] content2Int(String contentSize) {
		String[] split = contentSize.split(",");
		int[] result = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = formatFrames(split[i], i % 2 == 0 ? 1024 : 768);
		}
		return result;
	}

	public static int[] autoAdjust(int[] frames, Context context) {
		int[] result = new int[frames.length];
		int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
		int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
		for (int i = 0; i < frames.length; i++) {
			if (i == 0 || i == 2) {
				float float1 = (frames[i]*100*widthPixels)/(1024*100);
				result[i] = new Float(float1).intValue();
			} else {
				float float1 = (frames[i]*100*heightPixels)/(768*100);
				result[i] = new Float(float1).intValue();
			}
		}
		return result;
	}

}
