package com.rabbit.magazine.view;

import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.util.ImageUtil;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class PopupWindowAnimationAdapter extends PagerAdapter {

	private List<PopupWindowAnimationView> views;

	private int count;

	private boolean isCycling;

	public PopupWindowAnimationAdapter(List<PopupWindowAnimationView> views,
			boolean isCycling) {
		this.views = views;
		count = views.size();
		this.isCycling = isCycling;
	}

	public List<PopupWindowAnimationView> getPageViews() {
		return views;
	}

	// 销毁position位置的界面
	@Override
	public void destroyItem(View view, int position, Object obj) {
		if (isCycling) {
			if (position >= views.size()) {
				int newPosition = position % views.size();
				position = newPosition;
				// ((ViewPager) collection).removeView(views.get(position));
			}
			if (position < 0) {
				position = -position;
				// ((ViewPager) collection).removeView(views.get(position));
			}
		} else {
			if (views != null && views.size() > 0) {
				PopupWindowAnimationView v = views.get(position);
				ImageUtil.recycle(v);
				((ViewPager) view).removeView(v);
			}
		}
	}

	// 获取当前窗体界面数
	@Override
	public int getCount() {
		if (isCycling) {
			return count + 1;
		} else {
			if (views != null) {
				return views.size();
			}
			return 0;
		}
	}

	// 初始化position位置的界面
	@Override
	public Object instantiateItem(View view, int position) {
		if (isCycling) {
			if (position >= views.size()) {
				int newPosition = position % views.size();
				position = newPosition;
				count++;
			}
			if (position < 0) {
				position = -position;
				count--;
			}
			try {
				((ViewPager) view).addView(views.get(position), 0);
			} catch (Exception e) {
			}
			return views.get(position);
		} else {
			PopupWindowAnimationView v = views.get(position);
			if (!v.isLoad()) {
				String imgPath = AppConfigUtil.getAppResourceImage(
						AppConfigUtil.MAGAZINE_ID, v.getResource());
				imgPath = imgPath.replace("\n", "");
				Bitmap bitmap = ImageUtil.loadImage(imgPath);
				v.setImageBitmap(bitmap);
			}
			((ViewPager) view).addView(views.get(position), 0);
			return v;
		}
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		boolean b = (view == obj);
		return b;
	}

	@Override
	  public Parcelable saveState() {
	   return null;
	  }
	
	 @Override
	  public void startUpdate(View arg0) {
	  }
	 
	 @Override
	  public void restoreState(Parcelable arg0, ClassLoader arg1) {
	  }
	 
	 @Override
	  public void finishUpdate(View arg0) {
	  }
}
