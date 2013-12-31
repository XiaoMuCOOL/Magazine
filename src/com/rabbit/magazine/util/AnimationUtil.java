package com.rabbit.magazine.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import com.rabbit.magazine.view.LayerAnimationListener;
import com.rabbit.magazine.view.LayerView;
import com.rabbit.magazine.view.Rotate3dAnimation;

public class AnimationUtil {

	public static void startAlphaAnimation(LayerView layerView){
		AlphaAnimation alpha=new AlphaAnimation(1.0f, 0.0f);
		alpha.setDuration(700);
		LayerAnimationListener listener=new LayerAnimationListener(layerView);
		alpha.setAnimationListener(listener);
		layerView.setAnimation(alpha);
		alpha.startNow();
	}

	//动画End之后释放图片
	public static void startTranslateAnimation(LayerView layerView,int X,int toX,int Y,int toY){
		AnimationSet animSet=new AnimationSet(false);
		TranslateAnimation translate=new TranslateAnimation(X, toX, Y, toY);
		AlphaAnimation alpha=new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(translate);
		animSet.addAnimation(alpha);
		alpha.setDuration(450);
		translate.setDuration(500);
		LayerAnimationListener listener=new LayerAnimationListener(layerView);
		translate.setAnimationListener(listener);
		layerView.setAnimation(animSet);
		animSet.startNow();
	}
	
	public static void start3Danimation(LayerView layerView,int start,int end,String axis){
		AnimationSet animSet=new AnimationSet(false);
		AlphaAnimation alpha=new AlphaAnimation(1.0f, 0.0f);
		alpha.setDuration(300);
		animSet.addAnimation(alpha);
		
		float centerX = layerView.getWidth() / 2.0f;
        float centerY = layerView.getHeight() / 2.0f;
        Rotate3dAnimation rotation =new Rotate3dAnimation(start, end, centerX, centerY,axis);
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        animSet.addAnimation(rotation);
        
        LayerAnimationListener listener=new LayerAnimationListener(layerView);
        rotation.setAnimationListener(listener);
        
        layerView.setAnimation(animSet);
        
        animSet.startNow();
	}
}
