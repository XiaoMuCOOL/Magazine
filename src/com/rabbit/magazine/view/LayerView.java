package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

public class LayerView extends ImageView{

	private Layer layer;
	
	private boolean isLoad=false;
	
	private HotView hotView;
	
	private int frames[];
	
	public boolean isLoad() {
		return isLoad;
	}

	
	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	public Layer getLayer() {
		return layer;
	}

	public LayerView(Context context, Layer layer) {
		super(context);
		this.layer=layer;
		int[] frames =FrameUtil.frame2int(layer.getFrame());
		// 自动转换坐标，需要测试
		frames=FrameUtil.autoAdjust(frames,context);
		this.frames=frames;
		LayoutParams params = new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setTag(layer.getName()+"#"+layer.getTag());
		String visiable=layer.getVisible();
		if(visiable!=null){
			if("true".equals(visiable.toLowerCase())){
				setVisibility(View.VISIBLE);
			}else{
				setVisibility(View.INVISIBLE);
			}
		}
		setScaleType(ScaleType.FIT_XY);
	}
	
	public Bitmap loadBitmap(){
		Picture picture=layer.getPicture();
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
		Picture picture=layer.getPicture();
		if(picture!=null){
			String resource=picture.getResource();
			if(!isLoad&&resource!=null){
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bitmap=ImageUtil.loadImage(imgPath);
				setImageBitmap(bitmap);
			}
		}
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
		setVisibility(View.VISIBLE);
	}

	public HotView getHotView() {
		return hotView;
	}


	public void setHotView(HotView hotView) {
		this.hotView = hotView;
	}
}
