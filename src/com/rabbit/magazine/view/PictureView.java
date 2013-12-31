package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class PictureView extends ImageView implements android.view.View.OnTouchListener,OnGestureListener{

	private boolean isLoad=false;
	
	private PictureView zoomePicView;
	
	private static final int FLING_MIN_DISTANCE = 50;  
    private static final int FLING_MIN_VELOCITY = 0; 
    
    private GestureDetector mGesture = null;
    
    private float downY;
    
    private FlipperPageView2 flipperPageView;
	
	private HotView zoomIcon;
	
	public HotView getZoomIcon() {
		return zoomIcon;
	}

	public void setZoomIcon(HotView zoomIcon) {
		this.zoomIcon = zoomIcon;
	}

	public HotView getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(HotView closeIcon) {
		this.closeIcon = closeIcon;
	}

	private HotView closeIcon;
	
	private PageView2 pageView;
	
	public boolean isLoad() {
		return isLoad;
	}

	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	private Picture picture;
	public Picture getPicture() {
		return picture;
	}

	private Group group;
	public Group getGroup() {
		return group;
	}
	

	public PictureView(Context context,Group group,Picture picture,PageView2 pageView,FlipperPageView2 flipperPageView){
		super(context);
		this.picture = picture;
		this.group=group;
		this.pageView=pageView;
		this.flipperPageView=flipperPageView;
		String frame=picture.getFrame();
		if(frame==null){
			frame=group.getFrame();
		}
		int[] frames=FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames,context);
		LayoutParams params=new LayoutParams(frames[2],frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		String zoomable=picture.getZoomable();
		if(zoomable!=null&&"true".equals(zoomable.toLowerCase())){
			setOnTouchListener(this);
			setClickable(true);
		}
		setTag(true);
		
		((MagazineActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mGesture = new GestureDetector(PictureView.this);
			}
		});
	}
	
	public Bitmap loadBitmap(){
		if(picture!=null){
			String resource=picture.getResource();
			if(isLoad&&resource!=null){
				BitmapDrawable drawable = (BitmapDrawable) getDrawable();
				if(drawable!=null){
					Bitmap bm=drawable.getBitmap();
					if(bm!=null){
						return bm;
					}
				}
			}else{
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bm=ImageUtil.loadImage(imgPath);
				return bm;
			}
		}
		return null;
	}
	
	public void loadImg(){
		if(!isLoad&&picture!=null){
			String resource=picture.getResource();
			if(resource!=null){
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bitmap=ImageUtil.loadImage(imgPath);
				setImageBitmap(bitmap);
				setScaleType(ScaleType.FIT_XY);
			}
		}
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
	}

	/*@Override
	public void onClick(View v) {
		if(zoomePicView!=null){
			if(!zoomePicView.isLoad){
				zoomePicView.loadImg();
			}
			zoomePicView.setVisibility(View.VISIBLE);
			zoomePicView.bringToFront();
			pageView.getFrameLayout().addView(zoomePicView);
			zoomePicView.setScaleType(ScaleType.CENTER);
			if(closeIcon==null){
				zoomePicView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setVisibility(View.INVISIBLE);
						ImageUtil.recycle((PictureView)v);
						pageView.getFrameLayout().removeView(zoomePicView);
					}
				});
			}else{
				if(!closeIcon.isLoad()){
					closeIcon.loadImg();
				}
				closeIcon.setVisibility(View.VISIBLE);
				pageView.getFrameLayout().addView(closeIcon);
			}
		}
	}*/

	public PictureView getZoomePicView() {
		return zoomePicView;
	}

	public void setZoomePicView(PictureView zoomePicView) {
		this.zoomePicView = zoomePicView;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(zoomePicView!=null){
			if(!zoomePicView.isLoad){
				zoomePicView.loadImg();
			}
			zoomePicView.setVisibility(View.VISIBLE);
			zoomePicView.bringToFront();
			pageView.getFrameLayout().addView(zoomePicView);
			zoomePicView.setScaleType(ScaleType.CENTER);
			if(closeIcon==null){
				zoomePicView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setVisibility(View.INVISIBLE);
						ImageUtil.recycle((PictureView)v);
						pageView.getFrameLayout().removeView(zoomePicView);
					}
				});
			}else{
				if(!closeIcon.isLoad()){
					closeIcon.loadImg();
				}
				closeIcon.setVisibility(View.VISIBLE);
				pageView.getFrameLayout().addView(closeIcon);
			}
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		ViewParent parent = getParent();
		if (parent.getClass().equals(FrameLayout.class)) {
			ViewParent parent2 = parent.getParent();
			if (parent2.getClass().equals(GroupView2.class)) {
				GroupView2 groupView = (GroupView2) parent2;
				int scrollY=groupView.getScrollY();
				int distance=Float.valueOf(downY-e2.getY()).intValue();
				groupView.smoothScrollTo(0, scrollY+distance);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			 int curIndex = flipperPageView.getCurrentItem();
				int count = flipperPageView.getmPages().size();
				if (curIndex < (count - 1)) {
					flipperPageView.gotoPage(curIndex + 1);
					return true;
				} 
        } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			 int curIndex = flipperPageView.getCurrentItem();
			 if (curIndex >0) {
				 flipperPageView.gotoPage(curIndex - 1);
				 return true;
			 }  
        } else if(e1.getY()-e2.getY()>FLING_MIN_DISTANCE&&Math.abs(velocityY) > FLING_MIN_VELOCITY){
       	 int curIndex = flipperPageView.getCurrentItem();
			PageView2 pageView = flipperPageView.getmPages().get(curIndex);
			int childCount = pageView.getFrameLayout().getChildCount();
			int height = pageView.getFrameLayout().getHeight();
			pageView.scrollDown(pageView, childCount, height);
			return true;
        }else if(e2.getY()-e1.getY()>FLING_MIN_DISTANCE&&Math.abs(velocityY) > FLING_MIN_VELOCITY){
			int curIndex = flipperPageView.getCurrentItem();
			PageView2 pageView = flipperPageView.getmPages().get(curIndex);
			int childCount = pageView.getFrameLayout().getChildCount();
			pageView.scrollUp(pageView, childCount);
			return true;
        }
        return false;   
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.getParent().requestDisallowInterceptTouchEvent(true);
		return mGesture.onTouchEvent(event);
	}
}
