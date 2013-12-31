package com.rabbit.magazine.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FlipperPageView2 extends ViewPager implements android.view.View.OnTouchListener{

	private Context context;
	
	private boolean isVideoStop=false;
	
	private List<PageView2> mPages;
	
	private int mOffscreenPageLimit=0;
	
	private NavBarPopupWindow popupWindow;
	
	private CollectPopupWindow collectPopupWindow;
	
	public List<PageView2> getmPages() {
		return mPages;
	}

	public void setmPages(List<PageView2> mPages) {
		this.mPages = mPages;
	}

	private ArrayList<HorizontalGroupView> hGroupViewList=new ArrayList<HorizontalGroupView>();

	public FlipperPageView2(final Context context) {
		super(context);
		this.context = context;
		android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setLayoutParams(params);
		setCurrentItem(0);
		setOnTouchListener(this);
		setOnPageChangeListener(new OnPageChangeListener() {
			// position:当前选中的页面，这事件是在页面跳转完毕的时候调用的。
			@Override
			public void onPageSelected(int position) {
				popupWindow.setIndex(position);
				popupWindow.loadBitmap();
				isVideoStop=false;
				PageView2 page=mPages.get(position);
				View progressView=page.getFrameLayout().findViewWithTag("progress");
				page.setVerticalScrollBarEnabled(false);
				progressView.setVisibility(View.VISIBLE);
				progressView.bringToFront();
			}

			// 前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法。
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			// state ==1:正在滑动，state==2:滑动完毕，state==0:什么都没做，就是停在那。
			@Override
			public void onPageScrollStateChanged(int state) {
				if(state==1&&!isVideoStop){
					videoPause();
					isVideoStop=true;
				}
			}
		});
		//setOffscreenPageLimit(0);
	}
	
	public void videoPause(){
		for(CustVideoView2 video:((MagazineActivity)context).videos){
			video.normalPlay();
			video.getVideoView().pause();
			video.getVideoView().seekTo(0);
			if(video.getVideoPreview()!=null){
				video.getVideoPreview().setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void showNavBar() {
		if(popupWindow.isShowing()){
			popupWindow.dismiss();
		}else{
			popupWindow.showAtLocation(this, Gravity.LEFT|Gravity.TOP, 0, 0);
		}
		if(collectPopupWindow.isShowing()){
			collectPopupWindow.dismiss();
		}else{
			collectPopupWindow.showAtLocation(this, Gravity.LEFT|Gravity.BOTTOM, 0, 0);
		}
	}
	
	public void dismissNavBar(){
		if(popupWindow!=null){
			popupWindow.dismiss();
		}
		if(collectPopupWindow!=null){
			collectPopupWindow.dismiss();
		}
	}
	
	public void newNavBarPopupWindow(){
		((MagazineActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				popupWindow=new NavBarPopupWindow(context,FlipperPageView2.this);
			}
		});
		collectPopupWindow=new CollectPopupWindow(context, this);
	}

	/**
	 * 跳到指定页
	 * 
	 * @param pageIndex
	 */
	public void gotoPage(int pageIndex) {
		setCurrentItem(pageIndex);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		for(HorizontalGroupView hgv:hGroupViewList){
			hgv.getParent().requestDisallowInterceptTouchEvent(false);
		}
		for(CustVideoView2 videoView:((MagazineActivity)context).videos){
			videoView.getControllerBar().getParent().requestDisallowInterceptTouchEvent(false);
		}
		return false;
	}

	public ArrayList<HorizontalGroupView> gethGroupViewList() {
		return hGroupViewList;
	}

}
