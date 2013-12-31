package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class PopupWindowPictureView extends ImageView implements android.view.View.OnClickListener{
	
	private boolean isLoad=false;
	
	private String resource;
	
	private int position;
	
	public PopupWindowPictureView(Context context,int position){
		super(context);
		this.position=position;
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public boolean isLoad() {
		return isLoad;
	}

	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	public PopupWindowPictureView(Context context) {
		super(context);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
	}

	@Override
	public void onClick(View v) {
		
	}
}
