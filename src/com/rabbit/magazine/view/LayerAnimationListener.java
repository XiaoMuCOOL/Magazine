package com.rabbit.magazine.view;

import com.rabbit.magazine.util.ImageUtil;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class LayerAnimationListener implements AnimationListener {
	
	private LayerView layerView;
	
	public LayerAnimationListener(LayerView layerView){
		this.layerView=layerView;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		layerView.setVisibility(View.INVISIBLE);
		ImageUtil.recycle(layerView);
		HotView hotView=layerView.getHotView();
		if(hotView!=null){
			hotView.setVisibility(View.INVISIBLE);
			layerView.setLoad(false);
		}
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	@Override
	public void onAnimationStart(Animation animation) {
	}
}
