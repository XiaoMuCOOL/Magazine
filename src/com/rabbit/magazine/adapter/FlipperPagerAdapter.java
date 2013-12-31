package com.rabbit.magazine.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.AnimationView2;
import com.rabbit.magazine.view.CustVideoView2;
import com.rabbit.magazine.view.GroupView2;
import com.rabbit.magazine.view.HorizontalGroupView;
import com.rabbit.magazine.view.HotView;
import com.rabbit.magazine.view.LayerView;
import com.rabbit.magazine.view.PageView2;
import com.rabbit.magazine.view.PictureView;
import com.rabbit.magazine.view.RotaterView;
import com.rabbit.magazine.view.ShutterView;
import com.rabbit.magazine.view.SliderView;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FlipperPagerAdapter extends PagerAdapter {

	private List<PageView2> pageViews;
	
	private Context context;
	
	private Handler handler;
	
	public List<PageView2> getPageViews() {
		return pageViews;
	}

	public FlipperPagerAdapter(List<PageView2> pageViews,Context context){
		this.pageViews=pageViews;
		this.context=context;
		((MagazineActivity)context).runOnUiThread(new MyRunnable());
	}
	
	// 获取当前窗体界面数
	@Override
	public int getCount() {
		if(pageViews!=null){
			return pageViews.size();
		}
		return 0;
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

	//初始化position位置的界面
	@Override
	public Object instantiateItem(View view, int position) {
		final PageView2 pageView=pageViews.get(position);
		final View progressView=pageView.getFrameLayout().findViewWithTag("progress");
		new Thread(new Runnable(){
			@Override
			public void run() {
				Message msg=new Message();
				ImageUtil.loadPageViewImg(pageView,handler);
				msg.what=8;
				msg.obj=progressView;
				handler.sendMessage(msg);
			}
		}).start();
		pageView.setBottom(false);
		pageView.setY(0);
		((ViewPager) view).addView(pageView, 0);
		return pageView;
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		boolean b = (view == obj);
		return b;
	}
	
	class MyRunnable implements Runnable {
		public void run() {
			handler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch(msg.what){
					case 1:
						if(msg.arg1==1){
							PictureView pictureView=(PictureView)msg.obj;
							ImageUtil.recycle(pictureView);
						}else{
							Object[] objs1=(Object[]) msg.obj;
							PictureView picView=(PictureView) objs1[0];
							Bitmap bm1=(Bitmap) objs1[1];
							picView.setImageBitmap(bm1);
							picView.setScaleType(ScaleType.FIT_XY);
						}
						break;
					case 2:
						if(msg.arg1==1){
							HotView hotView=(HotView)msg.obj;
							ImageUtil.recycle(hotView);
						}else{
							Object[] objs2=(Object[]) msg.obj;
							HotView hotView=(HotView) objs2[0];
							Bitmap bm2=(Bitmap)objs2[1];
							hotView.setImageBitmap(bm2);
						}
						break;
					case 3:
						if(msg.arg1==1){
							AnimationView2 animView=(AnimationView2) msg.obj;
							animView.release();
						}else{
							Object[] objs3=(Object[]) msg.obj;
							AnimationView2 animView=(AnimationView2) objs3[0];
							List<Bitmap> bms=(List<Bitmap>) objs3[1];
							for(int i=0;i<bms.size();i++){
								ImageView img=new ImageView(context);
								img.setImageBitmap(bms.get(i));
								img.setTag(i);
								animView.addView(img);
							}
							animView.start();
						}
						break;
					case 4:
						RotaterView rotaterView=(RotaterView) msg.obj;
						if(msg.arg1==1){
							rotaterView.releaseImages();
						}else{
							rotaterView.loadImage();
						}
						break;
					case 5:
						if(msg.arg1==1){
							if(msg.obj.getClass().equals(SliderView.class)){
								SliderView sliderView=(SliderView)msg.obj;
								sliderView.release();
							}
							if(msg.obj.getClass().equals(ShutterView.class)){
								ShutterView shutterView=(ShutterView)msg.obj;
								shutterView.release();
							}
						}else{
							Object[] objs=(Object[])msg.obj;
							List<HashMap<ImageView,Bitmap>> list=(List<HashMap<ImageView, Bitmap>>) objs[1];
							for(HashMap<ImageView,Bitmap> map:list){
								for(Map.Entry<ImageView,Bitmap> entry:map.entrySet()){
									ImageView img=entry.getKey();
									Bitmap bm=entry.getValue();
									img.setImageBitmap(bm);
								}
							}
							if(objs[0]!=null&&objs[0].getClass().equals(ShutterView.class)){
								ShutterView shutterView=(ShutterView)objs[0];
								shutterView.start();
							}
						}
						break;
					case 6:
						if(msg.arg1==1){
							CustVideoView2 video=(CustVideoView2)msg.obj;
							video.releasePreview();
						}else{
							CustVideoView2 video=(CustVideoView2) msg.obj;
							video.loadPreview();
							String automatic = video.getVideo().getAutomatic();
							if(automatic.equalsIgnoreCase("true")){
								video.start();
								video.getVideoView().seekTo(0);
								String fullScreen=video.getVideo().getFullscreen();
								if("true".equalsIgnoreCase(fullScreen)){
									video.fullPlay();
								}
							}
						}
						break;
					case 7:
						if(msg.arg1==1){
							LayerView layerView=(LayerView)msg.obj;
							ImageUtil.recycle(layerView);
						}else{
							Object[] objs=(Object[])msg.obj;
							LayerView layerView=(LayerView) objs[0];
							Bitmap bm7=(Bitmap)objs[1];
							layerView.setImageBitmap(bm7);
						}
						break;
					case 8:
						View progressView=(View)msg.obj;
						progressView.setVisibility(View.INVISIBLE);
						break;
					case 9:
						PageView2 pageView=(PageView2)msg.obj;
						pageView.scrollTo(0, msg.arg1);
						break;
					case 10:
						GroupView2 groupView=(GroupView2)msg.obj;
						groupView.scrollTo(0, msg.arg1);
						break;
					case 11:
						HorizontalGroupView HgroupView=(HorizontalGroupView)msg.obj;
						HgroupView.scrollTo(msg.arg1, 0);
						break;
					}
				}
		};
		}
	}
}
