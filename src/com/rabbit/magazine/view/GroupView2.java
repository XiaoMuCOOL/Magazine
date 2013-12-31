package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.AnimationUtil;
import com.rabbit.magazine.util.BuildViewUtil;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;

public class GroupView2 extends ScrollView implements android.view.View.OnTouchListener{

	private Context context;
	
	private Group group;
	
	private FlipperPageView2 flipperPageView;
	
	private FrameLayout frameLayout;
	
	private List<AnimationView2> animList=new ArrayList<AnimationView2>();
	
	public List<AnimationView2> getAnimList() {
		return animList;
	}
	
	private PageView2 pageView;
	
	private float downXValue;
	private float downYValue;
	private int distanceY=0;public int getDistanceY() {
		return distanceY;
	}

	public void setDistanceY(int distanceY) {
		this.distanceY = distanceY;
	}
	private String paged;
	private int UNIT=AppConfigUtil.HEIGHT_ADJUST;
	
	public int getUNIT() {
		return UNIT;
	}

	public void setUNIT(int uNIT) {
		UNIT = uNIT;
	}

	private LayoutParams params;
	
	
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	public GroupView2(Context context,Group group,FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context=context;
		this.group=group;
		this.paged=group.getPaged();
		this.pageView=pageView;
		this.flipperPageView=flipperPageView;
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.frameLayout=new FrameLayout(context);
		this.frameLayout.setLayoutParams(params);
		addView(this.frameLayout);
		int[] frames=FrameUtil.frame2int(group.getFrame());
		// 自动转换坐标，需要测试
		frames=FrameUtil.autoAdjust(frames,context);
		params=new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setFocusable(false);
		buildView(group);
		if(paged!=null&&"true".equals(paged.toLowerCase())){
			setOnTouchListener(this);
		}else{
			((MagazineActivity)context).runOnUiThread(new Runnable(){
				@Override
				public void run() {
					final GestureDetector mGesture = new GestureDetector(new OnGestureListener(){
						@Override
						public boolean onDown(MotionEvent arg0) {
							return false;
						}
						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
							return false;
						}
						@Override
						public void onLongPress(MotionEvent e) {
						}
						@Override
						public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
							return false;
						}
						@Override
						public void onShowPress(MotionEvent e) {
						}
						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							GroupView2.this.flipperPageView.showNavBar();
							return false;
						}
					});
					setOnTouchListener(new OnTouchListener(){
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							v.getParent().requestDisallowInterceptTouchEvent(true);
							return mGesture.onTouchEvent(event);
						}
					});
				}
			});
		}
		setOnTouchListener(this);
		UNIT=frames[3];
		setVerticalFadingEdgeEnabled(false);
		setVerticalScrollBarEnabled(false);
	}

	/**
	 * 构建Group中的View，子Group不构建
	 * @param group
	 */
	private void buildView(Group group){
		for(Object obj:group.list){
			if(obj.getClass().equals(Picture.class)){
				BuildViewUtil.buildPictureView(context,(Picture)obj,group,getFrameLayout(),flipperPageView,pageView);
			}else if(obj.getClass().equals(Layer.class)){
				BuildViewUtil.buildLayerView(context,(Layer)obj,group,getFrameLayout(),flipperPageView,pageView);
			}else if(obj.getClass().equals(Hot.class)){
				BuildViewUtil.buildHotView(context,(Hot)obj,group,getFrameLayout(),flipperPageView,pageView);
			}else if(obj.getClass().equals(PictureSet.class)){
				BuildViewUtil.buildPictureSetView(context,(PictureSet)obj,getFrameLayout());
			}else if(obj.getClass().equals(Animation.class)){
				BuildViewUtil.buildAnimationView(context,(Animation)obj,getFrameLayout());
			}else if(obj.getClass().equals(Video.class)){
				BuildViewUtil.buildVideoView(context,(Video)obj,getFrameLayout());
			}else if(obj.getClass().equals(Rotater.class)){
				BuildViewUtil.buildRotaterView(context,(Rotater)obj,getFrameLayout(),group,flipperPageView,pageView);
			}else if(obj.getClass().equals(Slider.class)){
				BuildViewUtil.buildSliderView(context,(Slider)obj,getFrameLayout(),pageView);
			}else if(obj.getClass().equals(Shutter.class)){
				BuildViewUtil.buildShutterView(context,(Shutter)obj,getFrameLayout(),group,flipperPageView);
			}
		}
	}
	
	
	/**
	 * 释放GroupView下控件的图片资源
	 */
	public void releaseGroupViewImg(){
		int childCount=frameLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			View childView=frameLayout.getChildAt(i);
			if(childView instanceof PictureView){
				PictureView pictureView=(PictureView)childView;
				ImageUtil.recycle(pictureView);
				PictureView zommePicView=pictureView.getZoomePicView();
				if(zommePicView!=null){
					ImageUtil.recycle(zommePicView);
				}
				HotView zoomHotView=pictureView.getZoomIcon();
				if(zoomHotView!=null){
					ImageUtil.recycle(zoomHotView);
				}
				HotView closeHotView=pictureView.getCloseIcon();
				if(closeHotView!=null){
					ImageUtil.recycle(closeHotView);
				}
			}else if(childView instanceof PictureSetView){
				PictureSetView pictureSetView=(PictureSetView)childView;
				ImageUtil.recycle(pictureSetView);
			}else if(childView instanceof HotView){
				HotView hotView=(HotView)childView;
				ImageUtil.recycle(hotView);
			}else if(childView instanceof LayerView){
				LayerView layerView=(LayerView)childView;
				ImageUtil.recycle(layerView);
			}else if(childView instanceof AnimationView2){
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.release();
			}else if(childView instanceof GroupView2){
				GroupView2 groupView=(GroupView2)childView;
				groupView.releaseGroupViewImg();
			}else if(childView instanceof SliderView){
				SliderView sliderView=(SliderView)childView;
				sliderView.release();
			}else if(childView instanceof ShutterView){
				ShutterView shutterView=(ShutterView)childView;
				shutterView.release();
			}else if(childView instanceof CustVideoView2){
				CustVideoView2 videoView=(CustVideoView2)childView;
				videoView.release();
			}else if(childView instanceof RotaterView){
				RotaterView rotaterView=(RotaterView)childView;
				rotaterView.releaseImages();
			}
		}
	}

	/**
	 *加载GroupView下控件的图片资源
	 */
	public void loadGroupViewImg(Handler handler){
		int childCount=frameLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			View childView=frameLayout.getChildAt(i);
			if(childView instanceof PictureView){
				PictureView pictureView=(PictureView)childView;
				if(handler==null){
					pictureView.loadImg();
				}else{
					Bitmap bm=pictureView.loadBitmap();
					Message msg=new Message();
					msg.what=1;
					Object[] objs=new Object[]{pictureView,bm};
					msg.obj=objs;
					handler.sendMessage(msg);
				}
			}else if(childView instanceof GroupView2){
				GroupView2 groupView=(GroupView2)childView;
				groupView.loadGroupViewImg(handler);
			}else if(childView instanceof HotView){
				HotView view=(HotView) childView;
				if(handler==null){
					view.loadImg();
					view.setScaleType(ScaleType.FIT_XY);
				}else{
					Bitmap bm=view.loadBitmap();
					Message msg=new Message();
					msg.what=2;
					Object[] objs=new Object[]{view,bm};
					msg.obj=objs;
					handler.sendMessage(msg);
				}
			}else if(childView instanceof LayerView){
				LayerView layerView=(LayerView)childView;
				Layer layer=layerView.getLayer();
				String visible=layer.getVisible();
				if("TRUE".equals(visible)){
					if(handler==null){
						layerView.loadImg();
					}else{
						Bitmap bm=layerView.loadBitmap();
						Message msg=new Message();
						msg.what=7;
						msg.obj=new Object[]{layerView,bm};
						handler.sendMessage(msg);
					}
				}
			}else if (childView instanceof RotaterView) {
				RotaterView rotaterView = (RotaterView) childView;
				if(handler==null){
					rotaterView.loadImage();
				}else{
					Message msg=new Message();
					msg.what=4;
					msg.obj=rotaterView;
					handler.sendMessage(msg);
				}
			}else if(childView instanceof AnimationView2){
				AnimationView2 animationView=(AnimationView2)childView;
				if(handler==null){
					animationView.initial();
					animationView.start();
				}else{
					List<Bitmap> bms=animationView.loadBitmaps();
					Message msg=new Message();
					msg.what=3;
					Object[] objs=new Object[]{animationView,bms};
					msg.obj=objs;
					handler.sendMessage(msg);
				}
			}else if(childView instanceof SliderView){
				SliderView sliderView=(SliderView)childView;
				if(handler==null){
					sliderView.loadResource();
				}else{
					List<HashMap<ImageView,Bitmap>> list=sliderView.loadBitmaps();
					Message msg=new Message();
					msg.what=5;
					msg.obj=new Object[]{null,list};
					handler.sendMessage(msg);
				}
			}else if(childView instanceof ShutterView){
				ShutterView shutterView=(ShutterView)childView;
				if(handler==null){
					shutterView.loadResource();
				}else{
					List<HashMap<ImageView,Bitmap>> list=shutterView.loadBitmaps();
					Message msg=new Message();
					msg.what=5;
					msg.obj=list;
					msg.obj=new Object[]{shutterView,list};
					handler.sendMessage(msg);
				}
			}else if(childView instanceof CustVideoView2){
				CustVideoView2 video=(CustVideoView2)childView;
				video.loadPreview();
				String automatic = video.getVideo().getAutomatic();
				if(automatic.equalsIgnoreCase("true")){
					if(handler==null){
						video.start();
						video.getVideoView().seekTo(0);
						String fullScreen=video.getVideo().getFullscreen();
						if("true".equalsIgnoreCase(fullScreen)){
							video.fullPlay();
						}
					}else{
						Message msg=new Message();
						msg.what=6;
						msg.obj=video;
						handler.sendMessage(msg);
					}
				}
			}
		}
	}
	
	public Group getGroup() {
		return group;
	}

	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		// 消除ScrollView嵌套的滑动事件冲突
		setVerticalScrollBarEnabled(true);
		v.getParent().requestDisallowInterceptTouchEvent(true);
		int action=event.getAction();
		int curScrollY = v.getScrollY();
		int curScrollX = v.getScrollX();
		Float currentY = event.getY();
		Float currentX=event.getX();
		switch(action){
			case MotionEvent.ACTION_DOWN:
				downXValue =event.getX();
				downYValue = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				// 垂直方向的时候可以左右翻页或者上下滚动
				String orientation = group.getOrientation();
				if(orientation.equals(BasicView.PORTRAIT)){
						if ((currentX-downXValue) <-120){ 
							int curItem=flipperPageView.getCurrentItem();
							flipperPageView.setCurrentItem(curItem+1, true);
			            }    
						else if ((currentX-downXValue) >120){   
			            	int curItem=flipperPageView.getCurrentItem();
							flipperPageView.setCurrentItem(curItem-1, true);
			            }   
						else if ((downYValue - currentY) > 5) {// 向下滑动
							int height=getFrameLayout().getHeight();
							int Y=getScrollY();
							int height_=getHeight();
							if(Y>=(height-height_)){
								pageView.scrollDown(pageView, pageView.getFrameLayout().getChildCount(), pageView.getFrameLayout().getHeight());
								break;
							}
							if(paged!=null&&"true".equals(paged.toLowerCase())){
								scrollDown(v, height);
							}
						}else if ((currentY - downYValue) > 5) {// 向上滑动
							int Y=getScrollY();
							if(Y<=0){
								pageView.scrollUp(pageView,pageView.getFrameLayout().getChildCount());
								break;
							}
							if(paged!=null&&"true".equals(paged.toLowerCase())){
								scrollUp(v);
							}
						}else{
							flipperPageView.showNavBar();
						}
				}		
				break;
		}
		return false;
	}

	public void scrollDown(final View v, int height) {
		distanceY=distanceY+UNIT;
		if((distanceY)<height){
			v.post(new Runnable(){
				@Override
				public void run() {
					((GroupView2)v).smoothScrollTo(0, distanceY);
				}
			});
		}else{
			distanceY=distanceY-UNIT;
		}
	}

	public void scrollUp(final View v) {
		distanceY=distanceY-UNIT;
		if(distanceY>=0){
			v.post(new Runnable(){
				@Override
				public void run() {
					((GroupView2)v).smoothScrollTo(0, distanceY);
				}
			});
		}else{
			distanceY=distanceY+UNIT;
		}
	}
	
}































