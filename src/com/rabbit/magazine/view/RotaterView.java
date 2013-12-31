package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.*;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

/**
 * 360旋转的视图,提供一组资源图片，手势滑动按照360的形式旋转
 * 
 * @author litingwen
 * 
 */
//TODO:ImageView不支持Action_UP事件，需要重构，套一层Layout
public class RotaterView extends ImageView implements OnTouchListener,OnGestureListener ,Runnable {

	private Rotater rotater;
	private Group group;
	private boolean isLoad;
	private HotView rotateIcon;
	
	private int curIndex=0;
	private int[] indexs;
	private Bitmap[] bms;
	private String[] imgs;
	private int length;
	
	private GestureDetector mGestureDetector;
	
	public boolean threadStart=false;

	public HotView getRotateIcon() {
		return rotateIcon;
	}

	private float distance_x;

	/**
	 * 图片资源的显示索引,用于判断方向
	 */
	private int index = 0;

	public RotaterView(Context context, Group group, Rotater rotater) {
		super(context);
		this.rotater = rotater;
		this.group = group;
		distance_x=3*AppConfigUtil.WIDTHPIXELS/AppConfigUtil.WIDTH_ADJUST;
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
		imgs=rotater.getImages();
		bms=new Bitmap[imgs.length];
		length=imgs.length;
		indexs=new int[]{length-2,length-1,length,0,1,2,3};
		((MagazineActivity)context).runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mGestureDetector = new GestureDetector(RotaterView.this);
			}
		});
		((MagazineActivity)context).rotaterVeiws.add(this);
	}
	
	public void loadImage(){
		threadStart=true;
		Thread t=new Thread(this);
        t.start();
        loadFirstImage();
	}
	
	public void releaseImages(){
		curIndex=0;
		threadStart=false;
		setImageBitmap(null);
		for(int i=0;i<length;i++){
			if(bms[i]!=null){
				recycle(bms[i]);
			}
		}
	}

	public void loadFirstImage() {
		if(rotateIcon!=null){
			rotateIcon.setVisibility(View.VISIBLE);
		}
		setBitmap();
	}

	public void setRotateIcon(HotView rotateIcon) {
		this.rotateIcon = rotateIcon;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.getParent().requestDisallowInterceptTouchEvent(true);
		return mGestureDetector.onTouchEvent(event);
    }
	private void showNextImg() {
		if(rotateIcon!=null){
			rotateIcon.setVisibility(View.INVISIBLE);
		}
		setBitmap();
	}
	private void setBitmap(){
		if(bms[curIndex]!=null&&!bms[curIndex].isRecycled()){
			setImageBitmap(bms[curIndex]);
		}else{
			String image = imgs[curIndex];
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, image);
			Bitmap bm=ImageUtil.loadImage(imgPath);
			bms[curIndex]=bm;
			setImageBitmap(bm);
		}
	}
	@Override
	public void run() {
		while(threadStart){
			int k=0;
			int m=curIndex;
			for(int i=0;i<3;i++){
				m--;
				if(m<0){
					indexs[k++]=length+m;
				}else{
					indexs[k++]=m;
				}
			}
			indexs[k++]=curIndex;
			for(int i=0;i<3;i++){
				m++;
				if(m>length-1){
					indexs[k++]=m-(length-1);
				}else{
					indexs[k++]=m;
				}
			}
			for(int i=0;i<length;i++){
				boolean flag=false;
				for(int j=0;j<7;j++){
					if(indexs[j]==i){
						flag=true;
						if(bms[i]==null){
							String image = imgs[curIndex];
							String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, image);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							bms[i]=bm;
						}
						break;
					}
				}
				if(!flag){
					if(bms[i]!=null){
						BitmapDrawable drawable = (BitmapDrawable) getDrawable();
						Bitmap bm=drawable.getBitmap();
						if(bm!=bms[i]){
							recycle(bms[i]);
							bms[i]=null;
						}
					}
				}
			}
		}
	}
	private void recycle(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
			bm = null;
			System.gc();
		}
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		return true;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		return true;
	}
	@Override
	public void onLongPress(MotionEvent e) {
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(distanceX<0&&distanceX<-distance_x){
			curIndex++;
			if(curIndex>length-1){
				curIndex=0;
			}
			showNextImg();
		}
		if(distanceX>0&&distanceX>distance_x){
			curIndex--;
			if(curIndex<0){
				curIndex=length-1;
			}
			showNextImg();
		}
		return true;
	}
	@Override
	public void onShowPress(MotionEvent e) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}
}
