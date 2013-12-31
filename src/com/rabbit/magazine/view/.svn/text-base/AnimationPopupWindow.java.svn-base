package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.rabbit.magazine.R;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.util.ImageUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ViewFlipper;
import android.widget.FrameLayout.LayoutParams;

public class AnimationPopupWindow extends PopupWindow {
	
	private ViewPager viewPager;
	private List<PopupWindowAnimationView> views;
	private AtomicInteger what = new AtomicInteger(0);
	private Animation animation;
	
	private final Handler viewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			viewPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}
	};
	
	public AnimationPopupWindow(Context context,Animation animation){
		super(context);
		this.animation=animation;
		View popupWindowLayout= ((Activity)context).getLayoutInflater().inflate(R.layout.popupwindow, null);
		setContentView(popupWindowLayout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		FrameLayout frameLayout =(FrameLayout)popupWindowLayout.findViewById(R.id.popupwidowFrameLayout);
		viewPager = (ViewPager) frameLayout.findViewById(R.id.viewpagerLayout);
		String[] resources=animation.getResources();
		views=new ArrayList<PopupWindowAnimationView>();
		for(String resource:resources){
			if(!resource.equals("")){
				PopupWindowAnimationView view=new PopupWindowAnimationView(context);
				view.setResource("/"+resource);
				views.add(view);
			}
		}
		boolean isCycling=Boolean.parseBoolean(animation.getCycling());
		PopupWindowAnimationAdapter adapter=new PopupWindowAnimationAdapter(views,isCycling);
		viewPager.setAdapter(adapter);
		viewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		//右上角关闭Button
		ImageButton closeBtn = (ImageButton) frameLayout.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				for(PopupWindowAnimationView view:views){
					ImageUtil.recycle(view);
				}
			}
		});
		//背景透明
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					what.incrementAndGet();
					viewHandler.sendEmptyMessage(what.get());
					try {
						Thread.sleep(Integer.parseInt(AnimationPopupWindow.this.animation.getFrameLength())*1000);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}
	
	
}
