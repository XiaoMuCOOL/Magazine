package com.rabbit.magazine.view;

import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.PictureSet;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class AnimationView extends ImageView implements OnClickListener{
	
	private Context context;
	
	private Animation animation;
	
	public AnimationView(Context context,Animation animation) {
		super(context);
		this.context=context;
		this.animation=animation;
		setBackgroundColor(Color.GREEN);
		String frame = animation.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		AnimationPopupWindow popupWindow=new AnimationPopupWindow(context, animation);
		popupWindow.showAtLocation((View)v.getParent(), Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
	}
}
