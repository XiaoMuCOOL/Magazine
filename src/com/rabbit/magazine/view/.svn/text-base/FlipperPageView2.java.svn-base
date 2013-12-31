package com.rabbit.magazine.view;

import java.io.File;
import java.util.ArrayList;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.MagazineLoaderActivity;
import com.rabbit.magazine.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FlipperPageView2 extends ViewPager implements android.view.View.OnTouchListener{

	private Context context;
	
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
			
			boolean finish=false;
			// position:当前选中的页面，这事件是在页面跳转完毕的时候调用的。
			@Override
			public void onPageSelected(int position) {
				Log.d("", "PageSelected");
				int currentItem = getCurrentItem();
				View page = getChildAt(0);
				// 翻页之后自动播放判断
				if (page instanceof PageView2) {
					PageView2 view = (PageView2) page;
					FrameLayout frame = view.getFrameLayout();
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
											video.setUnload(false);
											video.start();
										}
									}
								}
							}

						}
					}
				}
			}

			// 前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法。
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.d("", "onPageScrolled");
			}

			// state ==1:正在滑动，state==2:滑动完毕，state==0:什么都没做，就是停在那。
			@Override
			public void onPageScrollStateChanged(int state) {
				if(state==2){
					finish=true;
					int position=getCurrentItem();
//					Toast.makeText(FlipperPageView2.this.context, "第" + (position + 1) + "页", Toast.LENGTH_SHORT).show();
//					// 页面加载后播放视频
//					View page = getChildAt(0);
//					if (page instanceof PageView2) {
//						PageView2 view = (PageView2) page;
//						FrameLayout frame = view.getFrameLayout();
//						int childCount = frame.getChildCount();
//						for (int i = 0; i < childCount; i++) {
//							View childView = frame.getChildAt(i);
//							if (childView instanceof FirstGroupView) {
//								FirstGroupView groupView = (FirstGroupView) childView;
//								int count = groupView.getChildCount();
//								for (int j = 0; j < count; j++) {
//									View groupchild = groupView.getChildAt(j);
//									if (groupchild instanceof CustVideoView) {
//										CustVideoView video = (CustVideoView) groupchild;
//										if (video.getVideo().getAutomatic().equalsIgnoreCase("true")) {
//											video.pause();
//										}
//									}
//								}
//
//							}
//						}
//					}
					String cacheDir = AppConfigUtil.getAppCache(AppConfigUtil.MAGAZINE_ID);
					String path = cacheDir + File.separator + position + ".png";
					File bitmap = new File(path);
					if (bitmap.exists()) {
						return;
					} else {
						// 创建缩略图，在快速导航中使用
						buildDrawingCache();
						Bitmap drawingCache = getDrawingCache();
						MagazineLoaderActivity activity=(MagazineLoaderActivity) context;
						activity.addCacheImage(path, drawingCache);
					}
				}
			}
		});
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
		return false;
	}

	public ArrayList<HorizontalGroupView> gethGroupViewList() {
		return hGroupViewList;
	}
}
