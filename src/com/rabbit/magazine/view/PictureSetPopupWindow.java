package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.adapter.ImageViewAdapter;
import com.rabbit.magazine.adapter.PopupWindowPageViewAdapter;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.util.ImageUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;

public class PictureSetPopupWindow extends PopupWindow{
	private ViewPager viewPager;
	private List<PopupWindowPictureView> views;
	private Gallery gallery;
	private ArrayList<ImageView> thumbnails;

	public PictureSetPopupWindow(Context context, PictureSet pictureSet) {
		super(context);
		View popupWindowLayout= ((Activity)context).getLayoutInflater().inflate(R.layout.popupwindow, null);
		setContentView(popupWindowLayout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		FrameLayout frameLayout =(FrameLayout)popupWindowLayout.findViewById(R.id.popupwidowFrameLayout);
		viewPager = (ViewPager) frameLayout.findViewById(R.id.viewpagerLayout);
		String[] resources=pictureSet.getResources();
		views=new ArrayList<PopupWindowPictureView>();
		thumbnails=new ArrayList<ImageView>();
		for(String resource:resources){
			if(!resource.equals("")&&!resource.equals("\n")){
				resource=resource.replace("\n", "");
				resource=resource.trim();
				PopupWindowPictureView view=new PopupWindowPictureView(context);
				view.setResource("/"+resource);
				views.add(view);
				ImageView img=new ImageView(context);
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, "/"+resource);
				Bitmap bitmap=ImageUtil.createThumbnail(imgPath, 200);
				img.setScaleType(ScaleType.FIT_XY);
				img.setLayoutParams(new Gallery.LayoutParams(200,200));
				img.setBackgroundResource(R.drawable.imageview_background_unselected);
				img.setImageBitmap(bitmap);
				thumbnails.add(img);
			}
		}
		gallery=(Gallery) frameLayout.findViewById(R.id.gallery);
		ImageViewAdapter imgadapter=new ImageViewAdapter(thumbnails);
		gallery.setAdapter(imgadapter);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int position=gallery.getSelectedItemPosition();
				viewPager.setCurrentItem(position);
				for(ImageView imgView:thumbnails){
					imgView.setBackgroundResource(R.drawable.imageview_background_unselected);
				}
				ImageView curImgView=(ImageView) gallery.getSelectedItem();
				curImgView.setBackgroundResource(R.drawable.imageview_background_selected);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		PopupWindowPageViewAdapter adapter=new PopupWindowPageViewAdapter(views,gallery);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(1);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				ImageView oldImgView=(ImageView) gallery.getSelectedItem();
				oldImgView.setBackgroundResource(R.drawable.imageview_background_unselected);
				gallery.setSelection(arg0);
				ImageView curImgView=(ImageView) gallery.getSelectedItem();
				curImgView.setBackgroundResource(R.drawable.imageview_background_selected);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		//右上角关闭Button
		ImageButton closeBtn = (ImageButton) frameLayout.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int count=views.size();
				for(int i=0;i<count;i++){
					ImageUtil.recycle(views.get(i));
					ImageUtil.recycle(thumbnails.get(i));
				}
				dismiss();
			}
		});
		//背景透明
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		if(pictureSet.getIndex()!=null){
			int index=Integer.parseInt(pictureSet.getIndex());
			viewPager.setCurrentItem(index-1);
		}
	}

}
