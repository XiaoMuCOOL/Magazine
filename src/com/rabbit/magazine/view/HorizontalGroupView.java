package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.BuildViewUtil;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

public class HorizontalGroupView extends HorizontalScrollView  implements android.view.View.OnTouchListener{
	
	private Context context;
	
	private Group group;
	
	private FlipperPageView2 flipperPageView;
	
	private float downYValue = 0;
	
	private float downXValue=0;
	
	private float scrollX;
	
	private FrameLayout frameLayout;
	
	private List<AnimationView2> animList=new ArrayList<AnimationView2>();
	
	public List<AnimationView2> getAnimList() {
		return animList;
	}

	private PageView2 pageView;

	public FrameLayout getFrameLayout() {
		return frameLayout;
	}
	
	public HorizontalGroupView(Context context,Group group,FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context=context;
		this.group=group;
		this.pageView=pageView;
		this.flipperPageView=flipperPageView;
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
		flipperPageView.gethGroupViewList().add(this);
		setOnTouchListener(this);
		setHorizontalFadingEdgeEnabled(false);
		setHorizontalScrollBarEnabled(false);
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
			}else if(childView instanceof HorizontalGroupView){
				HorizontalGroupView groupView=(HorizontalGroupView)childView;
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
	public boolean onTouch(View view, MotionEvent event) {
		setHorizontalScrollBarEnabled(true);
		view.getParent().requestDisallowInterceptTouchEvent(true);
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			downYValue = event.getY();
			downXValue=event.getX();
			scrollX=getScrollX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			int height=pageView.getFrameLayout().getHeight();
			int childCount = pageView.getFrameLayout().getChildCount();
			float currentY = event.getY();
			float currentX=event.getX();
			if ((downYValue - currentY) > 50) {// 向下滑动
				pageView.scrollDown(pageView,childCount,height);
			}else if ((currentY - downYValue) > 50) {// 向上滑动
				pageView.scrollUp(pageView,childCount);
			}else if(downXValue-currentX>50){
				int width=getFrameLayout().getWidth();
				int X=getScrollX();
				int width_=getWidth();
				if(X>=(width-width_)&&scrollX==(width-width_)){
					int curIndex = flipperPageView.getCurrentItem();
					int count = flipperPageView.getmPages().size();
					if (curIndex < (count - 1)) {
						flipperPageView.gotoPage(curIndex + 1);
						return true;
					} 
				}
			}else if(currentX-downXValue>50){
				int X=getScrollX();
				if(X<0&&scrollX==0){
					int curIndex = flipperPageView.getCurrentItem();
				    if (curIndex >0) {
					  flipperPageView.gotoPage(curIndex - 1);
				 	  return true;
				  }  
				}
			}else{
				flipperPageView.showNavBar();
			}
			break;
		}
		}
		return false;
	}
}
