package com.rabbit.magazine.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.rabbit.magazine.util.AnimationUtil;
import com.rabbit.magazine.util.BuildViewUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.util.StringUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.MediaController;

/**
 * 最低层的GroupView
 * 
 * @author cyqin
 * 
 */
public class FirstGroupView extends FrameLayout {

	private Context context;

	private FlipperPageView2 flipperPageView;

	private Group group;
	
	private PageView2 pageView;
	
	private int UNIT=AppConfigUtil.HEIGHT_ADJUST;
	
	private List<AnimationView2> animList=new ArrayList<AnimationView2>();

	public List<AnimationView2> getAnimList() {
		return animList;
	}

	public FirstGroupView(Context context, Group group, FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context = context;
		this.flipperPageView = flipperPageView;
		this.pageView=pageView;
		this.group = group;
		UNIT=AppConfigUtil.HEIGHTPIXELS;
		
		for(Object obj:group.list){
			if(obj.getClass().equals(Picture.class)){
				BuildViewUtil.buildPictureView(context,(Picture)obj,group,this,flipperPageView,pageView);
			}else if(obj.getClass().equals(Layer.class)){
				BuildViewUtil.buildLayerView(context,(Layer)obj,group,this,flipperPageView,pageView);
			}else if(obj.getClass().equals(Hot.class)){
				BuildViewUtil.buildHotView(context,(Hot)obj,group,this,flipperPageView,pageView);
			}else if(obj.getClass().equals(PictureSet.class)){
				BuildViewUtil.buildPictureSetView(context,(PictureSet)obj,this);
			}else if(obj.getClass().equals(Animation.class)){
				BuildViewUtil.buildAnimationView(context,(Animation)obj,this);
			}else if(obj.getClass().equals(Video.class)){
				BuildViewUtil.buildVideoView(context,(Video)obj,this);
			}else if(obj.getClass().equals(Rotater.class)){
				BuildViewUtil.buildRotaterView(context,(Rotater)obj,this,group,flipperPageView,pageView);
			}else if(obj.getClass().equals(Slider.class)){
				BuildViewUtil.buildSliderView(context,(Slider)obj,this,pageView);
			}else if(obj.getClass().equals(Shutter.class)){
				BuildViewUtil.buildShutterView(context,(Shutter)obj,this,group,flipperPageView);
			}
		}
	}

	/**
	 * 释放GroupView下控件的图片资源
	 */
	public void releaseGroupViewImg() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView instanceof PictureView) {
				PictureView pictureView = (PictureView) childView;
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
			} else if (childView instanceof PictureSetView) {

			} else if (childView instanceof HotView) {
				HotView hotView=(HotView)childView;
				ImageUtil.recycle(hotView);
			} else if (childView instanceof LayerView) {
				LayerView layerView = (LayerView) childView;
				ImageUtil.recycle(layerView);
			} else if (childView instanceof AnimationView2) {
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.release();
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
	 * 加载GroupView下控件的图片资源
	 */
	public void loadGroupViewImg(int Y,Handler handler) {
		int minY=Y-UNIT;
		int maxY=Y+UNIT*2;
		int childCount2=getChildCount();
		for(int j=0;j<childCount2;j++){
			View view=getChildAt(j);
			if(view instanceof PictureView){
				PictureView pictureView=(PictureView)view;
				LayoutParams params=(LayoutParams)pictureView.getLayoutParams();
				if(minY<=params.topMargin&&params.topMargin<maxY){//加载FirstGroupView资源
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
				}else{//释放FirstGroupView资源,如果只有一张底图,将不释放资源
					if((params.topMargin+params.height)>Y){
						//pictureView处于显示区域，不释放
					}else{
						if(handler==null){
							ImageUtil.recycle(pictureView);
						}else{
							Message msg=new Message();
							msg.what=1;
							msg.obj=pictureView;
							msg.arg1=1;
							handler.sendMessage(msg);
						}
					}
				}
			}else if(view instanceof HotView){
				HotView hotView=(HotView)view;
				LayoutParams params=(LayoutParams)hotView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
						hotView.loadImg();
					}else{
						Bitmap bm=hotView.loadBitmap();
						Message msg=new Message();
						msg.what=2;
						Object[] objs=new Object[]{hotView,bm};
						msg.obj=objs;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						ImageUtil.recycle(hotView);
					}else{
						Message msg=new Message();
						msg.what=2;
						msg.arg1=1;
						msg.obj=hotView;
						handler.sendMessage(msg);
					}
				}
				
				
				
				
				
				
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
						hotView.loadImg();
					}else{
						Bitmap bm=hotView.loadBitmap();
						Message msg=new Message();
						msg.what=2;
						Object[] objs=new Object[]{hotView,bm};
						msg.obj=objs;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						ImageUtil.recycle(hotView);
					}else{
						Message msg=new Message();
						msg.what=2;
						msg.arg1=1;
						msg.obj=hotView;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof AnimationView2){
				AnimationView2 animView=(AnimationView2)view;
				LayoutParams params=(LayoutParams)animView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
						animView.initial();
						animView.start();
					}else{
						List<Bitmap> bms=animView.loadBitmaps();
						Message msg=new Message();
						msg.what=3;
						Object[] objs=new Object[]{animView,bms};
						msg.obj=objs;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						animView.release();
					}else{
						Message msg=new Message();
						msg.what=3;
						msg.arg1=1;
						msg.obj=animView;
						handler.sendMessage(msg);
					}
				}
				
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
						animView.initial();
						animView.start();
					}else{
						List<Bitmap> bms=animView.loadBitmaps();
						Message msg=new Message();
						msg.what=3;
						Object[] objs=new Object[]{animView,bms};
						msg.obj=objs;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						animView.release();
					}else{
						Message msg=new Message();
						msg.what=3;
						msg.arg1=1;
						msg.obj=animView;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof RotaterView){
				RotaterView rotaterView=(RotaterView)view;
				LayoutParams params=(LayoutParams)rotaterView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
						rotaterView.loadImage();
					}else{
						Message msg=new Message();
						msg.what=4;
						msg.obj=rotaterView;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						rotaterView.releaseImages();
					}else{
						Message msg=new Message();
						msg.what=4;
						msg.arg1=1;
						msg.obj=rotaterView;
						handler.sendMessage(msg);
					}
				}
				
				
				
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
						rotaterView.loadImage();
					}else{
						Message msg=new Message();
						msg.what=4;
						msg.obj=rotaterView;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						rotaterView.releaseImages();
					}else{
						Message msg=new Message();
						msg.what=4;
						msg.arg1=1;
						msg.obj=rotaterView;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof SliderView){
				SliderView sliderView=(SliderView)view;
				LayoutParams params=(LayoutParams) sliderView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
						sliderView.loadResource();
					}else{
						List<HashMap<ImageView,Bitmap>> list=sliderView.loadBitmaps();
						Message msg=new Message();
						msg.what=5;
						msg.obj=new Object[]{null,list};
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						sliderView.release();
					}else{
						Message msg=new Message();
						msg.what=5;
						msg.arg1=1;
						msg.obj=sliderView;
						handler.sendMessage(msg);
					}
				}
				
				
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
						sliderView.loadResource();
					}else{
						List<HashMap<ImageView,Bitmap>> list=sliderView.loadBitmaps();
						Message msg=new Message();
						msg.what=5;
						msg.obj=new Object[]{null,list};
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						sliderView.release();
					}else{
						Message msg=new Message();
						msg.what=5;
						msg.arg1=1;
						msg.obj=sliderView;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof ShutterView){
				ShutterView shutterView=(ShutterView)view;
				LayoutParams params=(LayoutParams) shutterView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
						shutterView.loadResource();
					}else{
						List<HashMap<ImageView,Bitmap>> list=shutterView.loadBitmaps();
						Message msg=new Message();
						msg.what=5;
						msg.obj=new Object[]{shutterView,list};
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						shutterView.release();
					}else{
						Message msg=new Message();
						msg.what=5;
						msg.arg1=1;
						msg.obj=shutterView;
						handler.sendMessage(msg);
					}
				}
				
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
						shutterView.loadResource();
					}else{
						List<HashMap<ImageView,Bitmap>> list=shutterView.loadBitmaps();
						Message msg=new Message();
						msg.what=5;
						msg.obj=new Object[]{shutterView,list};
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						shutterView.release();
					}else{
						Message msg=new Message();
						msg.what=5;
						msg.arg1=1;
						msg.obj=shutterView;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof CustVideoView2){
				CustVideoView2 video=(CustVideoView2)view;
				LayoutParams params=(LayoutParams) video.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
					if(handler==null){
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
					}else{
						Message msg=new Message();
						msg.what=6;
						msg.obj=video;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						video.releasePreview();
					}else{
						Message msg=new Message();
						msg.what=6;
						msg.arg1=1;
						msg.obj=video;
						handler.sendMessage(msg);
					}
				}
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
					if(handler==null){
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
					}else{
						Message msg=new Message();
						msg.what=6;
						msg.obj=video;
						handler.sendMessage(msg);
					}
				}else{
					if(handler==null){
						video.releasePreview();
					}else{
						Message msg=new Message();
						msg.what=6;
						msg.arg1=1;
						msg.obj=video;
						handler.sendMessage(msg);
					}
				}*/
			}else if(view instanceof LayerView){
				LayerView layerView=(LayerView)view;
				LayoutParams params=(LayoutParams) layerView.getLayoutParams();
				if(params.topMargin+params.height>=Y&&params.topMargin<(Y+UNIT)){
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
				}else{
					if(handler==null){
						ImageUtil.recycle(layerView);
					}else{
						Message msg=new Message();
						msg.what=7;
						msg.arg1=1;
						msg.obj=layerView;
						handler.sendMessage(msg);
					}
				}
				
				
				/*int min=minY+UNIT;
				int max=maxY-UNIT;
				if(min<=params.topMargin&&params.topMargin<max){
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
				}else{
					if(handler==null){
						ImageUtil.recycle(layerView);
					}else{
						Message msg=new Message();
						msg.what=7;
						msg.arg1=1;
						msg.obj=layerView;
						handler.sendMessage(msg);
					}
				}*/
			}
		}
	}

	public Group getGroup() {
		return group;
	}
	
	/*public void showLayer(String name,String tag){
		for(LayerView layerView:pageView.getLayerViewList()){
			Layer layer=layerView.getLayer();
			String effect=layer.getEffect();
			if(layer.getName().equals(name)&&layer.getTag().equals(tag)){
				if(layerView.isLoad()&&layerView.getVisibility()==View.VISIBLE){
					continue;
				}
				layerView.loadImg();
				HotView hotView=layerView.getHotView();
				if(hotView!=null){
					hotView.setVisibility(View.VISIBLE);
					hotView.bringToFront();
					layerView.setLoad(true);
				}
				if(effect!=null){
					AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
					alpha.setDuration(800);
					layerView.setAnimation(alpha);
					alpha.startNow();
				}
			}else if(layer.getName().equals(name)){
				if(effect!=null){
					int w=layerView.getLayoutParams().width;
					int h=layerView.getLayoutParams().height;
					if("FADE".equals(effect)){
						AnimationUtil.startAlphaAnimation(layerView);
					}else if("SLIDE_LEFT".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, -w, 0, 0);
					}else if("SLIDE_RIGHT".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, w, 0, 0);
					}else if("SLIDE_TOP".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, 0, 0, -h);
					}else if("SLIDE_BOTTOM".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, 0, 0, h);
					}else if("FLIP_LEFT".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,-180,"Y");
					}else if("FLIP_TOP".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,180,"X");
					}else if("FLIP_RIGHT".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,180,"Y");
					}else if("FLIP_BOTTOM".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,-180,"X");
					}
				}else{
					ImageUtil.recycle(layerView);
					HotView hotView=layerView.getHotView();
					if(hotView!=null){
						hotView.setVisibility(View.INVISIBLE);
						layerView.setLoad(false);
					}
				}
			}
				
		}
	}*/
	
}
