package com.rabbit.magazine.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
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
import com.rabbit.magazine.view.AnimationView2;
import com.rabbit.magazine.view.CustVideoView2;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.GroupView2;
import com.rabbit.magazine.view.HotView;
import com.rabbit.magazine.view.LayerView;
import com.rabbit.magazine.view.PageView2;
import com.rabbit.magazine.view.PictureSetView;
import com.rabbit.magazine.view.PictureView;
import com.rabbit.magazine.view.RotaterView;
import com.rabbit.magazine.view.ShutterView;
import com.rabbit.magazine.view.SliderView;
import com.rabbit.magazine.view.VideoPreview;

public class BuildViewUtil {

	public static void buildShutterView(Context context,Shutter shutter,FrameLayout layout,Group group,FlipperPageView2 flipperPageView) {
		ShutterView shutterView = new ShutterView(context, shutter,group,flipperPageView);
		layout.addView(shutterView);
	}

	public static  void buildSliderView(Context context,Slider slider,FrameLayout layout,PageView2 pageView) {
		SliderView sliderView = new SliderView(context, slider);
		layout.addView(sliderView);
	}

	public static  void buildRotaterView(Context context,Rotater rotater,FrameLayout layout,Group group,FlipperPageView2 flipperPageView,PageView2 pageView) {
		RotaterView rotaterView = new RotaterView(context,group,rotater);
		layout.addView(rotaterView);
		String rotateIcon=rotater.getRotateIcon();
		if(rotateIcon!=null){
			LayoutParams params=(LayoutParams) rotaterView.getLayoutParams();
			int left=params.leftMargin+(params.width/2)-15;
			int top=params.topMargin+params.height-40;
			Hot hot =new Hot();
			hot.setAction("null");
			hot.setFrame(rotater.getFrame());
			hot.setPicture(rotateIcon);
			HotView hotview=new HotView(context,hot,group,flipperPageView,pageView);
			LayoutParams hotParams=new LayoutParams(30, 30);
			hotParams.setMargins(left, top, 0, 0);
			hotview.setLayoutParams(hotParams);
			layout.addView(hotview);
			rotaterView.setRotateIcon(hotview);
		}
	}

	public static  void buildVideoView(Context context,Video video,FrameLayout layout) {
		String previewPath=video.getPreview();
		if(StringUtil.isNotNull(previewPath)){
			CustVideoView2 videoView=new CustVideoView2(context, video);
			layout.addView(videoView);
			VideoPreview preview=new VideoPreview(context, video,videoView);
			layout.addView(preview);
			videoView.setVideoPreview(preview);
		}else{
			CustVideoView2 videoView=new CustVideoView2(context, video);
			layout.addView(videoView);
		}
	}

	public static  void buildAnimationView(final Context context,final Animation a,final FrameLayout layout) {
		((MagazineActivity)context).runOnUiThread(new Runnable(){
			@Override
			public void run() {
				AnimationView2 animationView = new AnimationView2(context, a);
				layout.addView(animationView);
			}
		});
	}

	public static  void buildPictureSetView(Context context,PictureSet ps,FrameLayout layout) {
		PictureSetView pictureSetView = new PictureSetView(context, ps);
		layout.addView(pictureSetView);
	}

	public static  void buildHotView(Context context,Hot hot, Group group,FrameLayout layout,FlipperPageView2 flipperPageView,PageView2 pageView) {
		HotView hotview = new HotView(context, hot, group, flipperPageView,pageView);
		layout.addView(hotview);
	}

	public static  void buildLayerView(Context context,Layer layer, Group group,FrameLayout layout,FlipperPageView2 flipperPageView,PageView2 pageView) {
		LayerView layerView = new LayerView(context, layer);
		layout.addView(layerView);
		pageView.getLayerViewList().add(layerView);
		Hot hot=layer.getHot();
		if(hot!=null){
			if(hot.getFrame()==null){
				hot.setFrame(layer.getFrame());
			}else{
				String hotFrame=hot.getFrame();
				String layerFrame=layer.getFrame();
				int[] hotframes = FrameUtil.frame2int(hotFrame);
				int[] layerframes=FrameUtil.frame2int(layerFrame);
				String frame=(hotframes[0]+layerframes[0])+","+(hotframes[1]+layerframes[1])+","+hotframes[2]+","+hotframes[3];
				hot.setFrame(frame);
			}
			HotView hotView=new HotView(context, hot, group, flipperPageView,pageView);
			String visible=layer.getVisible();
			if(!"true".equals(visible.toLowerCase())){
				hotView.setVisibility(View.INVISIBLE);
			}
			layerView.setHotView(hotView);
			layout.addView(hotView);
		}
			
	}

	public static  void buildPictureView(Context context,Picture picture, Group group,FrameLayout layout,FlipperPageView2 flipperPageView,PageView2 pageView) {
		PictureView pictureView = new PictureView(context, group, picture,pageView,flipperPageView);
		layout.addView(pictureView);
		String zoomedImg=picture.getZoomedImage();
		String zoomeIcon=picture.getZoomIcon();
		String closeIcon=picture.getCloseIcon();
		String zoomable=picture.getZoomable();
		if(zoomable!=null&&zoomable.equalsIgnoreCase("true")){
			PictureView zoomedPicView=null;
			if(zoomedImg!=null&&!"".equals(zoomedImg.toString().trim())){
				Picture pic=new Picture();
				int[] frames=FrameUtil.frame2int(picture.getFrame());
				int topMargin=(frames[1]/AppConfigUtil.HEIGHT_ADJUST)*AppConfigUtil.HEIGHT_ADJUST;
				pic.setFrame("0,"+topMargin+","+AppConfigUtil.WIDTH_ADJUST+","+AppConfigUtil.HEIGHT_ADJUST);
				pic.setResource(zoomedImg);
				zoomedPicView=new PictureView(context, group, pic,pageView,flipperPageView); 
				zoomedPicView.setScaleType(ScaleType.CENTER);
				//layout.addView(zoomedPicView);
				zoomedPicView.setVisibility(View.INVISIBLE);
				pictureView.setZoomePicView(zoomedPicView);
			}
			if(zoomeIcon!=null){
				pictureView.setOnClickListener(null);
				int[] frames=FrameUtil.frame2int(picture.getFrame());
				Hot zoomHot=new Hot();
				String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, zoomeIcon);
				int[] size=ImageUtil.getBitmapSize(imgPath);
				int w=size[0];
				int h=size[1];
				zoomHot.setFrame(frames[0]+frames[2]-w+","+frames[1]+","+w+","+h);
				zoomHot.setPicture(zoomeIcon);
				zoomHot.setAction("zoom-in");
				HotView zoomInhotview = new HotView(context, zoomHot, group, flipperPageView,pageView);
				HashMap<String,PictureView> tag=new HashMap<String,PictureView>();
				tag.put("pictureView", pictureView);
				tag.put("zoomePicView", zoomedPicView);
				zoomInhotview.setTag(tag);
				pictureView.setZoomIcon(zoomInhotview);
				layout.addView(zoomInhotview);
			}
			if(closeIcon!=null&&!"".equals(closeIcon.toString().trim())){
				Hot closeHot=new Hot();
				String closeImgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, closeIcon);
				int closeImgSize[]=ImageUtil.getBitmapSize(closeImgPath);
				int[] frames=FrameUtil.frame2int(picture.getFrame());
				int topMargin=(frames[1]/AppConfigUtil.HEIGHT_ADJUST)*AppConfigUtil.HEIGHT_ADJUST+20;
				int leftMargin=AppConfigUtil.WIDTHPIXELS-20-closeImgSize[0];
				closeHot.setFrame(leftMargin+","+topMargin+","+closeImgSize[0]+","+closeImgSize[1]);
				closeHot.setPicture(closeIcon);
				closeHot.setAction("zoom-out");
				HotView clostHotView=new HotView(context, closeHot, group, flipperPageView,pageView);
				clostHotView.setTag(zoomedPicView);
				pictureView.setCloseIcon(clostHotView);
				clostHotView.setVisibility(View.INVISIBLE);
			}
			
		}
	}
}
