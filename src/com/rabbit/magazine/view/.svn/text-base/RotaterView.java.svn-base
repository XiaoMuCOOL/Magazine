package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.util.ImageUtil;

/**
 * 360旋转的视图,提供一组资源图片，手势滑动按照360的形式旋转
 * 
 * @author litingwen
 * 
 */
//TODO:ImageView不支持Action_UP事件，需要重构，套一层Layout
public class RotaterView extends ImageView implements OnTouchListener {

	private Rotater rotater;
	private Group group;
	private boolean isLoad;

	private float downX;

	/**
	 * 图片资源的显示索引,用于判断方向
	 */
	private int index = 0;

	public RotaterView(Context context, Group group, Rotater rotater) {
		super(context);
		this.rotater = rotater;
		this.group = group;
		String frame = rotater.getFrame();
		if (frame == null) {
			frame = group.getFrame();
		}
		int[] frames = FrameUtil.frame2int(frame);
		frames = FrameUtil.autoAdjust(frames, context);
		LayoutParams params = new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setFocusable(false);
		setOnTouchListener(this);
	}

	/**
	 * 加载第一个图片
	 */
	public void loadFirstImage() {
		String[] images = rotater.getImages();
		if (images != null && images.length > 0) {
			String image = images[0];
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, image);
			Bitmap bitmap = ImageUtil.loadImage(imgPath);
			setImageBitmap(bitmap);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.getParent().requestDisallowInterceptTouchEvent(true);
		int action = event.getAction();
		float currX;
		boolean flag=false;
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getX();
				flag=true;
				break;
			case MotionEvent.ACTION_UP:
				currX = event.getX();
				if (currX - downX > 200) {
					index += 1;
				} else if (downX - currX > 200) {
					if (index > 0) {
						index -= 1;
					} else {
						index = rotater.getImages().length - 2;
					}
				}
				showIndexImage(index);
				break;
			default:
				break;
		}
		return flag;
	}

	private void showIndexImage(int index2) {
		String[] images = rotater.getImages();
		if(index2>-1&&index2<images.length){
			String image = images[index2];
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, image);
			Bitmap bitmap = ImageUtil.loadImage(imgPath);
			setImageBitmap(bitmap);
		}
	}

}
