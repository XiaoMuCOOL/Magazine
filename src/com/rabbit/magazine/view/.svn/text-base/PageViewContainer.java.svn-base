package com.rabbit.magazine.view;

import java.util.HashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * 维护页面链表，防止内存溢出
 * 
 * @author litingwen
 * 
 */
public class PageViewContainer {

	private static Map<String, PageView> views = new HashMap<String, PageView>();

	public static PageView getPage(String pageNum) {
		PageView pageView = views.get(pageNum);
		if (pageView != null) {
			ViewGroup parent = (ViewGroup) pageView.getParent();
			if (parent != null) {
//				parent.removeView(pageView);
			}
		}
		return pageView;
	}

	public static void addPage(String pageNum, PageView view) {
		if (views.keySet().size() > 5) {
			Set<Entry<String, PageView>> entrySet = views.entrySet();
			int i = 0;
			for (Iterator iterator = entrySet.iterator(); iterator.hasNext() && i < 2;) {
				i++;
				Entry<String, PageView> entry = (Entry<String, PageView>) iterator.next();
				PageView temp = entry.getValue();
				Map<Integer, GroupSplitView> splitViews = temp.getSplitViews();
				Set<Entry<Integer, GroupSplitView>> entrySet2 = splitViews.entrySet();
				for (Iterator iterator2 = entrySet2.iterator(); iterator2.hasNext();) {
					Entry<Integer, GroupSplitView> entry2 = (Entry<Integer, GroupSplitView>) iterator2.next();
					GroupSplitView value = entry2.getValue();
					for (int j = 0; j < value.getChildCount(); j++) {
						View view1 = value.getChildAt(j);
						if (view1 instanceof PictureView) {
							PictureView pic = (PictureView) view1;
							BitmapDrawable drawable = (BitmapDrawable) pic.getDrawable();
							if (drawable != null) {
								Bitmap bitmap = drawable.getBitmap();
								if (bitmap != null && !bitmap.isRecycled()) {
									bitmap.recycle();
									bitmap = null;
								}
							}
						}
					}

				}
				ViewGroup parent = (ViewGroup) temp.getParent();
				if (parent != null) {
					parent.removeView(temp);
				}
			}

			System.gc();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		views.put(pageNum, view);
	}
}
