package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class PopupWindowAnimationView extends ImageView {
	
	private boolean isLoad=false;
	
	private String resource;
	
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

	public PopupWindowAnimationView(Context context) {
		super(context);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
	}
}
