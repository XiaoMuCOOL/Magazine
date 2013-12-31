package com.rabbit.magazine.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.adapter.FlipperPagerAdapter;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.CustVideoView2;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.PageView2;
import com.rabbit.magazine.view.RotaterView;

public class MagazineActivity extends Activity {

	private List<PageView2> mListViews_landscape = new ArrayList<PageView2>();
	private List<PageView2> mListViews_portrait = new ArrayList<PageView2>();
	private FlipperPageView2 flipperPageView_landscape;
	private FlipperPageView2 flipperPageView_portrait;
	private int currentItem=-1;
	public boolean isFullPlay=false;
	private static List<Object> bookshelfResource;
	private static int index;
	private static BookshelfActivity bookshelfActivity;
	private static Magazine magazine;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	public static void excute(BookshelfActivity activity,List<Object> objs,int index,Magazine magazine){
		MagazineActivity.bookshelfResource=objs;
		MagazineActivity.index=index;
		bookshelfActivity=activity;
		MagazineActivity.magazine=magazine;
		Intent intent=new Intent(activity,MagazineActivity.class);
		activity.startActivity(intent);
	}
	
	public ArrayList<CustVideoView2> videos=new ArrayList<CustVideoView2>();
	
	public ArrayList<RotaterView> rotaterVeiws=new ArrayList<RotaterView>();
	
	public void addRotaterVeiw(RotaterView rotaterView){
		rotaterVeiws.add(rotaterView);
	}
	
	public void addVideos(CustVideoView2 videoView){
		videos.add(videoView);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new AsyncTask<Void, Integer, String>(){
			ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				dialog=new ProgressDialog(MagazineActivity.this);
				dialog.setMessage("精彩杂志马上呈现...");
				dialog.setCancelable(false);
				dialog.show();
			};
			@Override
			protected String doInBackground(Void... params) {
				List<Category> categorys = magazine.getCategorys();
				// 避免解析不正确直接程序退出
				if(categorys!=null&&categorys.size()>0){
					flipperPageView_landscape=new FlipperPageView2(MagazineActivity.this);
					flipperPageView_portrait=new FlipperPageView2(MagazineActivity.this);
					Category category = categorys.get(0);
					List<Page> pages = category.getPages();
					int count=pages.size();
					for (int i = 0; i < count; i++) {
						Page page=pages.get(i);
						for(Group g:page.getGroups()){
							if(g.getOrientation().equals(BasicView.LANDSCAPE)){
								AppConfigUtil.WIDTH_ADJUST=1024;
								AppConfigUtil.HEIGHT_ADJUST=768;
								if(ImageUtil.findFirstGroup(g,1024,768)!=null){
									PageView2 pageView = new PageView2(MagazineActivity.this, page, i,flipperPageView_landscape,i,g);
									mListViews_landscape.add(pageView);
								}else{
									AppConfigUtil.WIDTH_ADJUST=768;
									AppConfigUtil.HEIGHT_ADJUST=1024;
								}
							}
							if(g.getOrientation().equals(BasicView.PORTRAIT)){
								AppConfigUtil.WIDTH_ADJUST=768;
								AppConfigUtil.HEIGHT_ADJUST=1024;
								if(ImageUtil.findFirstGroup(g,768,1024)!=null){
									PageView2 pageView = new PageView2(MagazineActivity.this, page, i,flipperPageView_portrait,i,g);
									mListViews_portrait.add(pageView);
								}else{
									AppConfigUtil.WIDTH_ADJUST=1024;
									AppConfigUtil.HEIGHT_ADJUST=768;
								}
							}
						}
					}
					if(mListViews_landscape.size()>0){
						flipperPageView_landscape.setmPages(mListViews_landscape);
						flipperPageView_landscape.newNavBarPopupWindow();
						FlipperPagerAdapter adapter = new FlipperPagerAdapter(mListViews_landscape,MagazineActivity.this);
						flipperPageView_landscape.setAdapter(adapter);
						flipperPageView_landscape.setCurrentItem(index);
					}
					if(mListViews_portrait.size()>0){
						flipperPageView_portrait.setmPages(mListViews_portrait);
						flipperPageView_portrait.newNavBarPopupWindow();
						FlipperPagerAdapter adapter = new FlipperPagerAdapter(mListViews_portrait,MagazineActivity.this);
						flipperPageView_portrait.setAdapter(adapter);
						flipperPageView_portrait.setCurrentItem(index);
					}
				}
				return null;
			}
			@Override
			protected void onPostExecute(String result) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					if(mListViews_landscape.size()>0){
						if(mListViews_portrait.size()==0){
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
						}
						setContentView(flipperPageView_landscape);
					}else{
						if(mListViews_portrait.size()>0){
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
							setContentView(flipperPageView_portrait);
						}
					}
				}else{
					if(mListViews_portrait.size()>0){
						if(mListViews_landscape.size()==0){
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						}
						setContentView(flipperPageView_portrait);
					}else{
						if(mListViews_landscape.size()>0){
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
							setContentView(flipperPageView_landscape);
						}
					}
				}
				dialog.dismiss();
			};
		}.execute();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁杂志资源
		if(mListViews_landscape!=null){
			for (PageView2 pageView : mListViews_landscape) {
				ImageUtil.releasePageViewImg(pageView);
			}
		}
		if(mListViews_portrait!=null){
			for (PageView2 pageView : mListViews_portrait) {
				ImageUtil.releasePageViewImg(pageView);
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(mListViews_landscape.size()>0&&mListViews_portrait.size()>0){
			if ( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { 
				flipperPageView_portrait.dismissNavBar();
				int item=flipperPageView_portrait.getCurrentItem();
				/*if(mListViews_portrait!=null){
					for (PageView2 pageView : mListViews_portrait) {
						ImageUtil.releasePageViewImg(pageView);
					}
				}*/
				flipperPageView_landscape.setCurrentItem(item);
				setContentView(flipperPageView_landscape);
			}else{
				int item=flipperPageView_landscape.getCurrentItem();
				flipperPageView_landscape.dismissNavBar();
				/*if(mListViews_landscape!=null){
					for (PageView2 pageView : mListViews_landscape) {
						ImageUtil.releasePageViewImg(pageView);
					}
				}*/
				flipperPageView_portrait.setCurrentItem(item);
				setContentView(flipperPageView_portrait);
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			FlipperPageView2 flipperPageView2 = getFlipperPageView_landscape();
			if(flipperPageView2!=null){
				currentItem = flipperPageView2.getCurrentItem();
			}
		}else{
			FlipperPageView2 flipperPageView2 = getFlipperPageView_portrait();
			if(flipperPageView2!=null){
				currentItem = flipperPageView2.getCurrentItem();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		FlipperPageView2 pager;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			pager=getFlipperPageView_landscape();
		}else{
			pager=getFlipperPageView_portrait();
		}
		if(currentItem!=-1&&pager!=null){
			List<PageView2> mListViews=((FlipperPagerAdapter)pager.getAdapter()).getPageViews();
			PageView2 pageView=mListViews.get(currentItem);
			ImageUtil.loadPageViewImg(pageView,null);
			if(currentItem>0){
				PageView2 pre=mListViews.get(currentItem-1);
				ImageUtil.loadPageViewImg(pre,null);
			}
			if(currentItem<(mListViews.size()-1)){
				PageView2 next=mListViews.get(currentItem+1);
				ImageUtil.loadPageViewImg(next,null);
			}
			pager.setCurrentItem(currentItem);
		}
		if(bookshelfResource!=null){
			ImageUtil.recycleBookshelfResource(bookshelfResource);
			bookshelfResource.clear();
			bookshelfResource=null;
		}
		if(bookshelfActivity!=null){
			bookshelfActivity.finish();
			bookshelfActivity=null;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			// 返回按键之后先判断是否有视频在播放,如果视频是全屏播放则调用视频停止
			if(isFullPlay){
				for(CustVideoView2 videoview:videos){
					videoview.normalPlay();
				}
				return true;
			}
			else {
				Intent intent=new Intent(this,BookshelfActivity.class);
				intent.putExtra("fromMagazineActivity", true);
				startActivity(intent);
				finish();
				//recycle();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public FlipperPageView2 getFlipperPageView_landscape() {
		return flipperPageView_landscape;
	}
	public void setFlipperPageView_landscape(
			FlipperPageView2 flipperPageView_landscape) {
		this.flipperPageView_landscape = flipperPageView_landscape;
	}
	public FlipperPageView2 getFlipperPageView_portrait() {
		return flipperPageView_portrait;
	}
	public void setFlipperPageView_portrait(
			FlipperPageView2 flipperPageView_portrait) {
		this.flipperPageView_portrait = flipperPageView_portrait;
	}
}
