package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class ShutterView extends FrameLayout implements android.view.View.OnTouchListener,OnGestureListener{
	
	private ViewFlipper viewFlipper;
	
	private LinearLayout linear;
	
	private Context context;
	
	private Shutter shutter;
	
	private FlipperPageView2 flipperPageView;
	
	private boolean paged=false;
	
	private GestureDetector mGesture = null;

	public ShutterView(final Context context,final Shutter shutter,Group group,final FlipperPageView2 flipperPageView) {
		super(context);
		this.context=context;
		this.shutter=shutter;
		((MagazineActivity)context).runOnUiThread(new Runnable(){
			@Override
			public void run() {
				ShutterView.this.flipperPageView=flipperPageView;
				mGesture = new GestureDetector(ShutterView.this);
				viewFlipper=new ViewFlipper(context);
				addView(viewFlipper);
				linear=new LinearLayout(context);
				linear.setGravity(Gravity.LEFT|Gravity.BOTTOM);
				linear.setOrientation(LinearLayout.HORIZONTAL);
				List<Picture> pictures=shutter.getPictures();
				for(int i=0;i<pictures.size();i++){
					ImageView img=new ImageView(context);
					String resource=pictures.get(i).getResource();
					img.setTag(resource);
					img.setId(i);
					img.setScaleType(ScaleType.FIT_XY);
					viewFlipper.addView(img);
					Button btn=new Button(context);
					btn.setText(String.valueOf(i+1));
					btn.setTag(i);
					android.widget.LinearLayout.LayoutParams params_=new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					if(i==0){
						String selected=shutter.getSelected();
						if(selected!=null&&!selected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, selected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							params_=new android.widget.LinearLayout.LayoutParams(bm.getWidth(), bm.getHeight());
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.RED);
						}
					}else{
						String unselected=shutter.getUnselected();
						if(unselected!=null&&!unselected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, unselected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							params_=new android.widget.LinearLayout.LayoutParams(bm.getWidth(), bm.getHeight());
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.WHITE);
						}
					}
					params_.setMargins(0, 0, 10, 0);
					btn.setLayoutParams(params_);
					linear.addView(btn);
				}
				addView(linear);
				setFocusable(false);
				Float interval=Float.parseFloat(shutter.getInterval())*1000;
				viewFlipper.setFlipInterval(interval.intValue());
			}
		});
		setOnTouchListener(this);
		if(group.getPaged()!=null&&"true".equalsIgnoreCase(group.getPaged())){
			paged=true;
		}
		String frame = shutter.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames, context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
	}
	
	public List<HashMap<ImageView,Bitmap>> loadBitmaps(){
		List<HashMap<ImageView,Bitmap>> list=new ArrayList<HashMap<ImageView,Bitmap>>();
		int count=viewFlipper.getChildCount();
		for(int i=0;i<count;i++){
			View view=viewFlipper.getChildAt(i);
			if(view instanceof ImageView){
				HashMap<ImageView,Bitmap> map=new HashMap<ImageView,Bitmap>();
				ImageView img=(ImageView)view;
				String resource=img.getTag().toString();
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bm=ImageUtil.loadImage(imgPath);
				//img.setImageBitmap(bitmap);
				map.put(img, bm);
				list.add(map);
			}
		}
		return list;
	}
	
	public void loadResource(){
		int count=viewFlipper.getChildCount();
		for(int i=0;i<count;i++){
			View view=viewFlipper.getChildAt(i);
			if(view instanceof ImageView){
				ImageView img=(ImageView)view;
				String resource=img.getTag().toString();
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bitmap=ImageUtil.loadImage(imgPath);
				img.setImageBitmap(bitmap);
			}
			if(view instanceof LinearLayout){
				
			}
		}
		start();
	}

	public void start() {
		Animation alpha_in=AnimationUtils.loadAnimation(context, R.anim.alpha_in);
		alpha_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ImageView img=(ImageView) viewFlipper.getCurrentView();
				int id=img.getId();
				int count=linear.getChildCount();
				for(int i=0;i<count;i++){
					Button btn=(Button) linear.getChildAt(i);
					if(i!=id){
						String unselected=shutter.getUnselected();
						if(unselected!=null&&!unselected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, unselected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.WHITE);
						}
					}else{
						String selected=shutter.getSelected();
						if(selected!=null&&!selected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, selected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.RED);
						}
					}
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		viewFlipper.setInAnimation(alpha_in);
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha_out));
		viewFlipper.setAutoStart(true);
		viewFlipper.startFlipping();
	}

	public ViewFlipper getViewFlipper() {
		return viewFlipper;
	}
	
	public void release(){
		int count=viewFlipper.getChildCount();
		for(int i=0;i<count;i++){
			View view=viewFlipper.getChildAt(i);
			if(view instanceof ImageView){
				ImageUtil.recycle((ImageView)view);
			}
			if(view instanceof LinearLayout){
				
			}
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		if (e1.getX()-e2.getX() > 120 && Math.abs(velocityX) > 0) {   
			 int curIndex = flipperPageView.getCurrentItem();
				int count = flipperPageView.getmPages().size();
				if (curIndex < (count - 1)) {
					flipperPageView.gotoPage(curIndex + 1);
					return true;
				} 
        } else if (e2.getX()-e1.getX() > 120&& Math.abs(velocityX) > 0) {   
			 int curIndex = flipperPageView.getCurrentItem();
			 if (curIndex >0) {
				 flipperPageView.gotoPage(curIndex - 1);
				 return true;
			 }  
        } else if(e1.getY()-e2.getY()>50&& Math.abs(velocityX) > 0){
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
        }else if(e2.getY()-e1.getY()>50&& Math.abs(velocityX) > 0){
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
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		viewFlipper.stopFlipping();
		viewFlipper.showNext();
		viewFlipper.startFlipping();
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.getParent().requestDisallowInterceptTouchEvent(true);
		return mGesture.onTouchEvent(event);
	}
}
