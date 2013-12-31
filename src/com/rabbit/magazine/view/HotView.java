package com.rabbit.magazine.view;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.activity.WebViewActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

public class HotView extends ImageView implements android.view.View.OnTouchListener,OnGestureListener,OnClickListener {

	public static final String PAGE = "page";
	public static final String LAYER = "layer";
	public static final String AUDIO = "audio";
	public static final String LINK = "link";
	public static final String RESETSCROLL = "reset scroll";
	public static final String ZOOM_IN = "zoom-in";
	public static final String ZOOM_OUT = "zoom-out";
	public static final String MAIL = "mail";
	public static final String MAP = "map";

	private Hot hot;
	
	private boolean paged=false;
	
	private Group group;
	
	private PageView2 pageView;

	public Hot getHot() {
		return hot;
	}

	public void setHot(Hot hot) {
		this.hot = hot;
	}

	private float downY;
	
	private boolean isLoad=false;

	public boolean isLoad() {
		return isLoad;
	}

	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	private float downX;

	private FlipperPageView2 flipperPageView;

	private LayoutParams params;
	
	private GestureDetector mGesture = null;

	public void setParams(LayoutParams params) {
		this.params = params;
	}

	private static final int FLING_MIN_DISTANCE = 50;  
    private static final int FLING_MIN_VELOCITY = 0; 

	private Context context;

	public HotView(Context context, Hot hot, Group group,
			FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context = (MagazineActivity) context;
		this.hot = hot;
		this.pageView=pageView;
		this.group=group;
		this.flipperPageView = flipperPageView;
		String frame = hot.getFrame();
		int[] gFrames=FrameUtil.frame2int(group.getFrame());
		int[] frames = FrameUtil.frame2int(frame,gFrames[2],gFrames[3]);
		frames = FrameUtil.autoAdjust(frames, context);
		params = new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		if(group.getPaged()!=null&&"true".equalsIgnoreCase(group.getPaged())){
			paged=true;
		}
		if(pageView.getFirstGroup()==group&&!pageView.isPaged()){
			this.setOnClickListener(this);
		}else{
			setOnTouchListener(this);
		}
		((MagazineActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mGesture = new GestureDetector(HotView.this);
			}
		});
		setScaleType(ScaleType.FIT_XY);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
	}
	
	public Bitmap loadBitmap(){
		String pic = hot.getPicture();
		if(pic!=null){
			if (isLoad) {
				BitmapDrawable drawable = (BitmapDrawable) getDrawable();
				if(drawable!=null){
					Bitmap bm=drawable.getBitmap();
					if(bm!=null){
						return bm;
					}
				}
			}else{
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, pic);
				Bitmap bm=ImageUtil.loadImage(imgPath);
				return bm;
			}
		}
		return null;
	}

	public void loadImg() {
		String pic = hot.getPicture();
		if (!isLoad&&pic != null) {
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, pic);
			Bitmap bitmap = ImageUtil.loadImage(imgPath);
			setImageBitmap(bitmap);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.getParent().requestDisallowInterceptTouchEvent(true);
		return mGesture.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		click();
		return true;
	}

	private void click() {
		String action = hot.getAction();
		String argument = hot.getArgument();
		if (action.equals(PAGE)) {
			int pageNumber;
			if (argument.contains("+")) {
				argument = argument.replace("+", "");
				pageNumber = Integer.parseInt(argument)
						+ flipperPageView.getCurrentItem() + 1;
			} else if (argument.contains("-")) {
				argument = argument.replace("-", "");
				pageNumber = flipperPageView.getCurrentItem()
						- Integer.parseInt(argument) + 1;
			} else {
				pageNumber = Integer.parseInt(argument);
			}
			flipperPageView.gotoPage(pageNumber - 1);
		} else if (action.equals(LAYER)) {
			String tag = argument;
			View viewParent = (View) getParent();
			int index = argument.indexOf("#");
			if (index > 0) {
				String[] split = argument.split("#");
				tag = split[0] + "#" + split[1];
			}
			pageView.showLayer(argument.split("#")[0],argument.split("#")[1]);
			
		} else if (action.equals(LINK)) {
			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra("url", argument);
			context.startActivity(intent);
		} else if (action.equals(RESETSCROLL)) {
			View parent = (View) getParent();
			if (parent.getClass().equals(FrameLayout.class)) {
				GroupView2 groupView = (GroupView2) parent.getParent();
				groupView.smoothScrollTo(0, 0);
			} else if (parent.getClass().equals(FirstGroupView.class)) {
				PageView2 pageView = (PageView2) parent.getParent().getParent();
				pageView.loadResource(0, pageView.getFrameLayout().getChildCount(),null);
				pageView.smoothScrollTo(0, 0);
				pageView.Y = 0;
				pageView.setBottom(false);
			}
		} else if (action.equals(ZOOM_IN)) {
			HashMap<String,PictureView> tag= (HashMap<String, PictureView>) getTag();
			PictureView zoomePicView = (PictureView)tag.get("zoomePicView");
			pageView.getFrameLayout().addView(zoomePicView);
			if(!zoomePicView.isLoad()){
				zoomePicView.loadImg();
			}
			zoomePicView.setVisibility(View.VISIBLE);
			zoomePicView.setScaleType(ScaleType.CENTER);
			
			HotView closeHotView=((PictureView)tag.get("pictureView")).getCloseIcon();
			pageView.getFrameLayout().addView(closeHotView);
			if(!closeHotView.isLoad()){
				closeHotView.loadImg();
			}
			closeHotView.setVisibility(View.VISIBLE);
			closeHotView.bringToFront();
		} else if(action.equals(ZOOM_OUT)){
			setVisibility(View.INVISIBLE);
			ImageUtil.recycle(this);
			
			PictureView zoomePicView=(PictureView)getTag();
			
			((FrameLayout)getParent()).removeView(zoomePicView);
			((FrameLayout)getParent()).removeView(this);
			
			zoomePicView.setVisibility(View.INVISIBLE);
			ImageUtil.recycle(zoomePicView);
			
		}else if (action.equals(MAIL)) {
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("plain/text");
			intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{argument}); // 设置收件人
			intent.putExtra(android.content.Intent.EXTRA_TEXT,""); // 设置内容
			context.startActivity(Intent.createChooser(intent,"Choose Email Client"));
		} else if (action.equals(MAP)) {
			String[] args = argument.split("|");
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+ args[0] + "," + args[1]));
			context.startActivity(intent);
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		if(!paged){
			ViewParent parent=this.getParent();
    		if(parent.getClass().equals(FirstGroupView.class)){
    			int curIndex = flipperPageView.getCurrentItem();
	 			PageView2 pageView = flipperPageView.getmPages().get(curIndex);
	 			pageView.scrollTo(0, pageView.getScrollY()+new Float(distanceY).intValue());
	 			return true;
    		}
    		if(parent.getClass().equals(FrameLayout.class)){
    			ViewParent parent_=parent.getParent();
    			if(parent_.getClass().equals(GroupView2.class)){
    				GroupView2 groupView=(GroupView2)parent_;
    				groupView.scrollTo(0, groupView.getScrollY()+new Float(distanceY).intValue());
    				return true;
    			}
    		}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		 if (e1.getX()-e2.getX() > 120 && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			 int curIndex = flipperPageView.getCurrentItem();
				int count = flipperPageView.getmPages().size();
				if (curIndex < (count - 1)) {
					flipperPageView.gotoPage(curIndex + 1);
					return true;
				} 
         } else if (e2.getX()-e1.getX() > 120&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
			 int curIndex = flipperPageView.getCurrentItem();
			 if (curIndex >0) {
				 flipperPageView.gotoPage(curIndex - 1);
				 return true;
			 }  
         } else if(e1.getY()-e2.getY()>50&& Math.abs(velocityX) > FLING_MIN_VELOCITY){
        	 if(paged){
        		 ViewParent parent=this.getParent();
         		if(parent.getClass().equals(FirstGroupView.class)){
         			int curIndex = flipperPageView.getCurrentItem();
     	 			PageView2 pageView = flipperPageView.getmPages().get(curIndex);
     	 			int childCount = pageView.getFrameLayout().getChildCount();
     	 			int height = pageView.getFrameLayout().getHeight();
     	 			pageView.scrollDown(pageView, childCount, height);
     	 			return true;
         		}
         		if(parent.getClass().equals(FrameLayout.class)){
         			ViewParent parent_=parent.getParent();
         			if(parent_.getClass().equals(GroupView2.class)){
         				GroupView2 groupView=(GroupView2)parent_;
         				groupView.scrollDown(groupView, groupView.getFrameLayout().getHeight());
         			}
         			return true;
         		}
        	 }
         }else if(e2.getY()-e1.getY()>50&& Math.abs(velocityX) > FLING_MIN_VELOCITY){
        	 if(paged){
        		ViewParent parent=this.getParent();
         		if(parent.getClass().equals(FirstGroupView.class)){
         			int curIndex = flipperPageView.getCurrentItem();
     				PageView2 pageView = flipperPageView.getmPages().get(curIndex);
     				int childCount = pageView.getFrameLayout().getChildCount();
     				pageView.scrollUp(pageView, childCount);
     				return true;
         		}
         		if(parent.getClass().equals(FrameLayout.class)){
         			ViewParent parent_=parent.getParent();
         			if(parent_.getClass().equals(GroupView2.class)){
         				GroupView2 groupView=(GroupView2)parent_;
         				groupView.scrollUp(groupView);
         			}
         			return true;
         		}
        	 }
         }
         return false;   
	}

	@Override
	public void onClick(View arg0) {
		click();
	}
}
