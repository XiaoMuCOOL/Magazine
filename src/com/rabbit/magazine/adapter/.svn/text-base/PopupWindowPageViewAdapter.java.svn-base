package com.rabbit.magazine.view;

import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.util.ImageUtil;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class PopupWindowPageViewAdapter extends PagerAdapter {
	
	private List<PopupWindowPictureView> views;
	
	public PopupWindowPageViewAdapter(List<PopupWindowPictureView> views){
		this.views=views;
	}
	
	public List<PopupWindowPictureView> getPageViews() {
		return views;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	// 销毁position位置的界面
	@Override
	public void destroyItem(View view, int position, Object obj) {
		if(views!=null&&views.size()>0){
			PopupWindowPictureView v=views.get(position);
			ImageUtil.recycle(v);
			((ViewPager) view).removeView(v);
		}
	}

	// 获取当前窗体界面数
	@Override
	public int getCount() {
		if(views!=null){
			return views.size();
		}
		return 0;
	}

	//初始化position位置的界面
	@Override
	public Object instantiateItem(View view, int position) {
		PopupWindowPictureView v=views.get(position);
		if(!v.isLoad()){
			String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, v.getResource());
			imgPath=imgPath.replace("\n", "");
			Bitmap bitmap=ImageUtil.loadImage(imgPath);
			v.setImageBitmap(bitmap);
		}
		((ViewPager) view).addView(views.get(position), 0);
		return v;
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		boolean b = (view == obj);
		return b;
	}

}
