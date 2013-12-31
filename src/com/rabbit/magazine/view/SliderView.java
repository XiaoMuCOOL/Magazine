package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.Slider;
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
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.FrameLayout.LayoutParams;

public class SliderView extends FrameLayout  implements android.view.View.OnTouchListener{
	
	private ViewFlipper viewFlipper;
	
	private LinearLayout linear;
	
	private Slider slider;
	
	private Context context;
	
	private float downXValue=0;
	
	public SliderView(final Context context,final Slider slider) {
		super(context);
		this.context=context;
		this.slider=slider;
		String frame = slider.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames, context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
		((MagazineActivity)context).runOnUiThread(new Runnable(){
			@Override
			public void run() {
				viewFlipper=new ViewFlipper(context);
				addView(viewFlipper);
				linear=new LinearLayout(context);
				linear.setGravity(Gravity.LEFT|Gravity.BOTTOM);
				linear.setOrientation(LinearLayout.HORIZONTAL);
				List<Picture> pictures=slider.getPictures();
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
						String selected=slider.getSelected();
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
						String unselected=slider.getUnselected();
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
			}
		});
		setFocusable(true);
		setOnTouchListener(this);
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

	private void setIndicatorBackground() {
		ImageView img=(ImageView) viewFlipper.getCurrentView();
		int id=img.getId();
		int count=linear.getChildCount();
		for(int i=0;i<count;i++){
			Button btn=(Button) linear.getChildAt(i);
			if(i!=id){
				String unselected=slider.getUnselected();
				if(unselected!=null&&!unselected.contains("undefined")){
					String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, unselected);
					Bitmap bm=ImageUtil.loadImage(imgPath);
					BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
					btn.setBackgroundDrawable(bd);
				}else{
					btn.setBackgroundColor(Color.WHITE);
				}
			}else{
				String selected=slider.getSelected();
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
	public boolean onTouch(View view, MotionEvent event) {
		view.getParent().requestDisallowInterceptTouchEvent(true);
		int i=event.getAction();
		switch(i){   
			case MotionEvent.ACTION_DOWN:{    
	           downXValue =event.getX();   
	           return true;   
			}    
			case MotionEvent.ACTION_UP:{   
	           float currentX =event.getX();               
	           if ((currentX-downXValue) <-80){   
	        	   viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context,R.anim.push_left_in));
	        	   viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context,R.anim.push_left_out));
	        	   viewFlipper.showNext(); 
	        	   setIndicatorBackground();
	           }    
	           if ((currentX-downXValue) >80){   
	        	   viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context,R.anim.push_right_in));
	        	   viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context,R.anim.push_right_out));
	        	   viewFlipper.showPrevious();   
	        	   setIndicatorBackground();
	           }   
	           break;  
			}
		}
		return false;
	}
}
