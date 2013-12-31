package com.rabbit.magazine.view;

import java.util.List;
import com.rabbit.magazine.util.ImageUtil;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class FlipperPagerAdapter extends PagerAdapter {

	private List<PageView2> pageViews;
	
	public List<PageView2> getPageViews() {
		return pageViews;
	}

	public FlipperPagerAdapter(List<PageView2> pageViews){
		this.pageViews=pageViews;
	}
	
	// 销毁position位置的界面
	@Override
	public void destroyItem(View view, int position, Object obj) {
		if(pageViews!=null&&pageViews.size()>0){
			PageView2 pageView=pageViews.get(position);
			pageView.scrollTo(0, 0);
			ImageUtil.releasePageViewImg(pageView);
			((ViewPager) view).removeView(pageView);
		}
	}

	// 获取当前窗体界面数
	@Override
	public int getCount() {
		if(pageViews!=null){
			return pageViews.size();
		}
		return 0;
	}

	//初始化position位置的界面
	@Override
	public Object instantiateItem(View view, int position) {
		PageView2 pageView=pageViews.get(position);
		ImageUtil.loadPageViewImg(pageView);
		((ViewPager) view).addView(pageViews.get(position), 0);
		// 第一页中视频自动播放
		if(position==0){
			FrameLayout frame = pageView.getFrameLayout();
			int childCount = frame.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = frame.getChildAt(i);
				if (childView instanceof FirstGroupView) {
					FirstGroupView groupView = (FirstGroupView) childView;
					int count = groupView.getChildCount();
					for (int j = 0; j < count; j++) {
						View groupchild = groupView.getChildAt(j);
						if (groupchild instanceof CustVideoView) {
							CustVideoView video = (CustVideoView) groupchild;
							String automatic = video.getVideo().getAutomatic();
							if(video.isUnload()){
								if(automatic.equalsIgnoreCase("true")){
									video.start();
								}
							}
						}
					}
				}
			}
		}
		return pageView;
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		boolean b = (view == obj);
		return b;
	}
	
	
}
